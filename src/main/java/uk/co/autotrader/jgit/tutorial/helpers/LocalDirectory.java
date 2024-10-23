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

package uk.co.autotrader.jgit.tutorial.helpers;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This class represents a local directory on the file system and provides methods to delete and recreate the directory.
 */
public class LocalDirectory {

    private final Path path;

    /**
     * Creates a new LocalDirectory instance.
     *
     * @param basePath the base path where the directory will be created
     * @param directoryName the name of the directory under the base path
     */
    public LocalDirectory(Path basePath, String directoryName) {
        assert basePath.toFile().isDirectory();
        path = Path.of(basePath.toString(), directoryName);
    }

    /**
     * Deletes the directory if it exists and then recreates it.
     *
     * @param shouldDeleteAnyExistingDirectoryContents if true, the contents of the directory will be deleted
     */
    public void ensureDirectoryExistsAndIsEmpty(boolean shouldDeleteAnyExistingDirectoryContents) {
        Path localDirectoryPath = this.path;

        if (Files.exists(localDirectoryPath)) {
            if (shouldDeleteAnyExistingDirectoryContents) {
                try {
                    FileUtils.deleteDirectory(localDirectoryPath.toFile());
                } catch (IOException e) {
                    throw new RuntimeException("Failed to delete directory: " + localDirectoryPath, e);
                }
            } else {
                throw new IllegalStateException("Directory already exists. If you're re-running the tutorial, " +
                        "please confirm that you're comfortable with the directory being deleted and " +
                        "recreated in the Tutorial Configuration class by setting the 'DELETE_LOCAL_REPOSITORY_DIRECTORY_CONTENTS_BEFORE_CLONE' flag to 'true'.");
            }
        }

        try {
            Files.createDirectories(localDirectoryPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directories: " + localDirectoryPath, e);
        }
    }

    public Path getPath() {
        return path;
    }

    /**
     * Ensures the directory exists and is a Git repository.
     */
    public void ensureDirectoryExistsAndIsGitRepository() {
        Path directoryPath = this.path;

        if (!Files.exists(directoryPath)) {
            throw new IllegalStateException("Directory does not exist. " +
                    "Please clone the repository first by running Step 1 of the tutorial.");
        }

        // A cloned repository on your local machine will have a .git directory
        File gitDir = new File(directoryPath.toFile(), ".git");
        if (!gitDir.exists()) {
            throw new IllegalStateException("Directory is not a git repository. " +
                    "Please clone the repository first by running Step 1 of the tutorial.");
        }
    }
}
