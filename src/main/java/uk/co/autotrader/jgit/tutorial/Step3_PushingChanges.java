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

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.sshd.SshdSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.autotrader.jgit.tutorial.helpers.LocalDirectory;

import java.io.IOException;
import java.util.function.Supplier;

public class Step3_PushingChanges {

    private static final Logger logger = LoggerFactory.getLogger(Step3_PushingChanges.class);

    public static void main(String[] args) {

        /*
         * Get the Tutorial's configuration which gives us the following information:
         * - Local Directory that represents a directory on the file system (defaults to ~/jgit-cloned-repositories/name-of-cloned-repo)
         * - SSH key & (optional) Key Passphrase to authenticate with remote repository (taken from Env vars SSH_KEY & SSH_KEY_PASSPHRASE)
         * - Fingerprint of the server's public key (GitHub)
         */
        Configuration configuration = new Configuration();
        CustomSshdSessionFactory sshdSessionFactory = new CustomSshdSessionFactory(configuration.getSshKey(), configuration.getSshKeyPassphrase(), configuration.getGithubEcdsaSshFingerprint());

        // Push changes to the remote repository using SSH client based on the provided configuration
        try {
            pushChangesToRemoteRepository(configuration.getLocalRepositoryDirectory(), sshdSessionFactory::buildSshdSessionFactory);
        } catch (GitAPIException e) {
            throw new RuntimeException("An error occurred while pushing changes to the remote repository", e);
        }
    }

    private static void pushChangesToRemoteRepository(LocalDirectory localGitRootDirectory, Supplier<SshdSessionFactory> sshdSessionFactorySupplier) throws GitAPIException {

        localGitRootDirectory.ensureDirectoryExistsAndIsGitRepository();

        SshdSessionFactory.setInstance(sshdSessionFactorySupplier.get());

        // N.B: It's crucial to close Git objects when they are no longer needed.
        // Git is a closeable resource and should be closed to avoid resource leaks.
        logger.info("Attempting to open repository: {}", localGitRootDirectory.getPath().toAbsolutePath());
        try (final Git git = Git.open(localGitRootDirectory.getPath().toFile())) {
            logger.info("Successfully opened repository: {}", git.getRepository().getDirectory());
            git.push().call();
            logger.info("Successfully pushed changes to the repository");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred opening the repository", e);
        }

    }
}
