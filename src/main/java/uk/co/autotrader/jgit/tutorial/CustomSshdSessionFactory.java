/*
 * Copyright 2024 AUTO TRADER GROUP PLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.autotrader.jgit.tutorial;

import org.apache.sshd.common.config.keys.KeyUtils;
import org.apache.sshd.common.config.keys.PublicKeyEntry;
import org.apache.sshd.common.config.keys.PublicKeyEntryResolver;
import org.apache.sshd.common.util.security.SecurityUtils;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.sshd.JGitKeyCache;
import org.eclipse.jgit.transport.sshd.ServerKeyDatabase;
import org.eclipse.jgit.transport.sshd.SshdSessionFactory;
import org.eclipse.jgit.transport.sshd.SshdSessionFactoryBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Collections;
import java.util.List;

/**
 * This class is responsible for creating a custom SSHD session factory.
 * It is used to authenticate with a remote Git repository using SSH.
 */
public class CustomSshdSessionFactory {

    private final Iterable<KeyPair> keyPairs;
    private final PublicKeyEntry publicSshKeyFingerprint;

    public CustomSshdSessionFactory(@Nonnull String sshKey, @Nullable String sshKeyPassphrase, @Nonnull String githubEcdsaSshFingerprint) {
        this.keyPairs = loadKeyPairs(sshKey, sshKeyPassphrase);
        this.publicSshKeyFingerprint = PublicKeyEntry.parsePublicKeyEntry(githubEcdsaSshFingerprint);
    }

    /**
     * Builds a custom SSHD session factory.
     *
     * @return a SshdSessionFactory
     */
    public SshdSessionFactory buildSshdSessionFactory() {
        Path tempDirectoryForSshSessionFactory = createTemporaryDirectory();

        return new SshdSessionFactoryBuilder()
                .setPreferredAuthentications("publickey")
                .setDefaultKeysProvider(ignoredSshDirBecauseWeUseAnInMemorySetOfKeyPairs -> this.keyPairs)
                // A requirement of the SshdSessionFactoryBuilder is
                // to set the home directory and ssh directory
                // despite providing our SSH key pair programmatically.
                // See: https://github.com/eclipse-jgit/jgit/issues/89
                .setHomeDirectory(tempDirectoryForSshSessionFactory.toFile())
                .setSshDirectory(tempDirectoryForSshSessionFactory.toFile())
                .setConfigStoreFactory((ignoredHomeDir, ignoredConfigFile, ignoredLocalUserName) -> null)
                .setConfigFile(ignoredSshDir -> null) //The function may return null, in which case no SSH config file will be used.
                .setServerKeyDatabase((ignoredHomeDir, ignoredSshDir) -> new ServerKeyDatabase() {
                    @Override
                    public List<PublicKey> lookup(String connectAddress, InetSocketAddress remoteAddress, Configuration config) {
                        return Collections.emptyList();
                    }

                    /**
                     * This method is used to compare the server's public key with the provided GitHub public key fingerprint.
                     * If the keys match, the connection is accepted.
                     *
                     * @param connectAddress the address to connect to
                     * @param remoteAddress the remote address
                     * @param serverKey the server's public key
                     * @param config the configuration
                     * @param provider the credentials provider
                     * @return true if the server's public key matches the GitHub public key fingerprint
                     */
                    @Override
                    public boolean accept(String connectAddress, InetSocketAddress remoteAddress, PublicKey serverKey, Configuration config, CredentialsProvider provider) {
                        PublicKey gitHubPublicKey;

                        try {
                            gitHubPublicKey = publicSshKeyFingerprint.resolvePublicKey(null, null, PublicKeyEntryResolver.IGNORING);
                        } catch (IOException | GeneralSecurityException e) {
                            throw new RuntimeException(e);
                        }

                        return KeyUtils.compareKeys(serverKey, gitHubPublicKey);
                    }
                })
                .build(new JGitKeyCache());
    }

    /**
     * Loads the SSH private key from the provided content.
     *
     * @param privateKeyContent the content of the private key
     * @param passphrase the passphrase for the private key
     * @return iterable of KeyPair
     */
    private Iterable<KeyPair> loadKeyPairs(@Nonnull String privateKeyContent, @Nullable String passphrase) {
        Iterable<KeyPair> keyPairs;
        try {
            keyPairs = SecurityUtils.loadKeyPairIdentities(null,
                    null, new ByteArrayInputStream(privateKeyContent.getBytes()), (session, resourceKey, retryIndex) -> passphrase);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to load ssh private key", e);
        }
        if (keyPairs == null) {
            throw new IllegalArgumentException("Failed to load ssh private key");
        }
        return keyPairs;
    }

    private Path createTemporaryDirectory() {
        Path temporaryDirectory;
        try {
            temporaryDirectory = Files.createTempDirectory("ssh-temp-dir");
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temporary directory", e);
        }
        return temporaryDirectory;
    }
}
