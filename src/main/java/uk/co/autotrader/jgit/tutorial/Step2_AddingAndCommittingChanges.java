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
import org.eclipse.jgit.lib.PersonIdent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.autotrader.jgit.tutorial.helpers.LocalDirectory;

import java.io.IOException;

public class Step2_AddingAndCommittingChanges {

    private static final Logger logger = LoggerFactory.getLogger(Step2_AddingAndCommittingChanges.class);

    public static void main(String[] args) {

        /*
         * Get the Tutorial's configuration, which gives us the following information:
         * - Local Directory that represents a directory on the file system (defaults to ~/jgit-cloned-repositories/name-of-cloned-repo)
         */
        Configuration configuration = new Configuration();

        // Add and commit changes to the local repository
        try {
            addAndCommitChanges(configuration.getLocalRepositoryDirectory());
        } catch (GitAPIException e) {
            throw new RuntimeException("An error occurred while adding and committing changes to the local repository", e);
        }
    }

    private static void addAndCommitChanges(LocalDirectory localGitRootDirectory) throws GitAPIException {

        localGitRootDirectory.ensureDirectoryExistsAndIsGitRepository();

        // Since we're working with a local repository, we don't need to set up an SSH session factory

        // N.B: It's crucial to close Git objects when they are no longer needed.
        // Git is a closeable resource and should be closed to avoid resource leaks.
        logger.info("Attempting to open repository: {}", localGitRootDirectory.getPath().toAbsolutePath());
        try (Git git = Git.open(localGitRootDirectory.getPath().toFile())) {
            logger.info("Successfully opened repository: {}", git.getRepository().getDirectory());

            // Check if there are any uncommitted changes or untracked files
            if (git.status().call().isClean()) {
                logger.info("There are no uncommitted changes or untracked files in the repository. Nothing to commit.");
                return;
            }

            git.add()
                    .addFilepattern(".") // Just like you would use the Git CLI, you add all files in the repository using "."
                    .call();

            // The commit message can be supplied as a system property when running the program.
            String suppliedCommitMessage = System.getProperty("commitMessage");
            String commitMessage = suppliedCommitMessage != null ? suppliedCommitMessage : "Add all files";

            git.commit()
                    .setCommitter(
                            // A person ident is required to commit changes
                            new PersonIdent("David Davies", "david.davies@example.com"))
                    .setMessage(commitMessage)
                    .call();

            logger.info("Successfully staged and committed changes to the local repository");

        } catch (IOException e) {
            throw new RuntimeException("An error occurred opening the repository", e);
        }

    }
}
