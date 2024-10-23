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

import uk.co.autotrader.jgit.tutorial.helpers.LocalDirectory;

import java.nio.file.Path;

/**
 * This class represents the configuration for the jgit tutorial.
 */
public class Configuration {

    // Local directory on your file system where the remote repository will be cloned to.
    private static final String LOCAL_REPOSITORY_DIRECTORY_BASE = System.getProperty("user.home") + "/jgit-cloned-repositories";

    // Name of the local directory where the remote repository will be cloned to.
    private static final String LOCAL_REPOSITORY_DIRECTORY_NAME = "name-of-cloned-repo";

    // This flag determines whether the contents of the local repository directory should be deleted before cloning the remote repository.
    private static final boolean DELETE_LOCAL_REPOSITORY_DIRECTORY_CONTENTS_BEFORE_CLONE = false;

    // SSH URL of the remote repository to clone.
    private static final String REMOTE_REPOSITORY_SSH_URL = "ssh://git@github.com/my-username/my-repo";

    // This class is responsible for holding the SSH secrets required to authenticate with a remote Git repository.
    private final SshSecrets sshSecrets;

    // This is the SSH fingerprint for github.com
    // It is used to verify the server's identity when connecting via SSH.
    // The fingerprint is the Base64-encoded SHA-256 hash of the server's public key.
    // More information can be found at https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/githubs-ssh-key-fingerprints
    // IMPORTANT: Because this fingerprint is public knowledge, it is not a secret.
    private static final String GITHUB_ECDSA_SSH_FINGERPRINT = "ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBEmKSENjQEezOmxkZMy7opKgwFB9nkt5YRrYMjNuG5N87uRgg6CLrbo5wAdT/y6v0mKV0U2w0WZ2YB/++Tpockg=";

    // Local directory on your file system where the remote repository will be cloned to.
    private final LocalDirectory localRepositoryDirectory;

    public Configuration() {

        if (REMOTE_REPOSITORY_SSH_URL.isBlank()) {
            throw new IllegalArgumentException("REMOTE_REPOSITORY_SSH_URL must be set. Example: ssh://git@github.com/my-username/my-repo");
        }

        if (LOCAL_REPOSITORY_DIRECTORY_BASE.isBlank() || LOCAL_REPOSITORY_DIRECTORY_NAME.isBlank()) {
            throw new IllegalArgumentException("LOCAL_REPOSITORY_DIRECTORY_BASE and LOCAL_REPOSITORY_DIRECTORY_NAME must be set");
        }

        this.sshSecrets = SshSecrets.extractFromEnvironment();

        localRepositoryDirectory = new LocalDirectory(Path.of(LOCAL_REPOSITORY_DIRECTORY_BASE), LOCAL_REPOSITORY_DIRECTORY_NAME);
    }

    public LocalDirectory getLocalRepositoryDirectory() {
        return localRepositoryDirectory;
    }

    public boolean shouldDeleteAnyExistingDirectoryContents() {
        return DELETE_LOCAL_REPOSITORY_DIRECTORY_CONTENTS_BEFORE_CLONE;
    }

    public String getRemoteRepositoryUrl() {
        return REMOTE_REPOSITORY_SSH_URL;
    }

    public String getSshKey() {
        return sshSecrets.getKey();
    }

    public String getSshKeyPassphrase() {
        return sshSecrets.getKeyPassphrase();
    }

    public String getGithubEcdsaSshFingerprint() {
        return GITHUB_ECDSA_SSH_FINGERPRINT;
    }

    /**
     * This class is responsible for holding the SSH secrets required to authenticate with a remote Git repository.
     */
    public static class SshSecrets {

        private final String key;
        private final String keyPassphrase;

        public SshSecrets(String key, String sshKeyPassphrase) {
            assert key != null;
            this.key = key;
            this.keyPassphrase = sshKeyPassphrase;
        }

        public static SshSecrets extractFromEnvironment() {
            String sshKey = System.getenv("SSH_KEY");
            String sshKeyPassphrase = System.getenv("SSH_KEY_PASSPHRASE");
            if (sshKey == null) {
                throw new IllegalArgumentException("SSH_KEY environment variable must be set");
            }

            return new SshSecrets(sshKey, sshKeyPassphrase);
        }

        public String getKey() {
            return key;
        }

        public String getKeyPassphrase() {
            return keyPassphrase;
        }
    }
}
