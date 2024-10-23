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
import org.eclipse.jgit.transport.sshd.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.autotrader.jgit.tutorial.helpers.LocalDirectory;

import java.util.function.Supplier;

public class Step1_CloningARemoteRepository {

    private static final Logger logger = LoggerFactory.getLogger(Step1_CloningARemoteRepository.class);

    public static void main(String[] args) {

        /*
         * Get the Tutorial's configuration which gives us the following information:
         * - Local Directory that represents a directory on the file system (defaults to ~/jgit-cloned-repositories/name-of-cloned-repo)
         * - Whether it is OK to delete the local directory at the start of each run (defaults to false to avoid this tutorial deleting things in your filesystem)
         * - SSH URL of a remote repository to clone
         * - SSH Key & (optional) Key Passphrase to authenticate with remote repository (taken from Env vars SSH_KEY & SSH_KEY_PASSPHRASE)
         * - Fingerprint of the server's public key (GitHub)
         */
        Configuration configuration = new Configuration();
        CustomSshdSessionFactory sshdSessionFactory = new CustomSshdSessionFactory(configuration.getSshKey(), configuration.getSshKeyPassphrase(), configuration.getGithubEcdsaSshFingerprint());

        // Clone the target repository with JGit into the local directory using SSH client based on the provided configuration
        try {
            cloneRepository(configuration.getRemoteRepositoryUrl(), configuration.getLocalRepositoryDirectory(),
                    configuration.shouldDeleteAnyExistingDirectoryContents(), sshdSessionFactory::buildSshdSessionFactory);
        } catch (GitAPIException e) {
            throw new RuntimeException("An error occurred while cloning the repository", e);
        }

    }

    private static void cloneRepository(String remoteRepositoryUrl, LocalDirectory localGitRootDirectory, boolean shouldDeleteAnyExistingDirectoryContents, Supplier<SshdSessionFactory> sshdSessionFactorySupplier) throws GitAPIException {

        localGitRootDirectory.ensureDirectoryExistsAndIsEmpty(shouldDeleteAnyExistingDirectoryContents);

        SshdSessionFactory.setInstance(sshdSessionFactorySupplier.get());

        // N.B: It's crucial to close Git objects when they are no longer needed.
        // Git is a closeable resource and should be closed to avoid resource leaks.
        logger.info("Attempting to clone repository at: {}", remoteRepositoryUrl);
        try (Git clonedRepository = Git.cloneRepository()
                .setURI(remoteRepositoryUrl)
                .setDirectory(localGitRootDirectory.getPath().toFile())
                .call()) {

            logger.info("Repository cloned to: {}", clonedRepository.getRepository().getDirectory());
        }
    }

}

