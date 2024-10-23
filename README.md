# jgit-tutorial

This repository is part of a tutorial on using JGit, a Java library for working with Git repositories.

The purpose of this repository is
to accompany an engineering blog article
that explains
how to use JGit to interact with GitHub repositories using SSH and configuring the SSH client programmatically.

You can find the engineering blog article here: [Working with Git in Java: Part 2 - Using SSH with JGit](https://engineering.autotrader.co.uk/2024/10/24/working-with-git-in-java-part-2-using-ssh-with-jgit.html)

The repository and the blog article together serve as a comprehensive guide for developers
who want to learn how to get started with using JGit commands to interact with Git repositories in Java and how to authenticate using SSH. 

It contains example code that demonstrates how to clone a remote repository and commit and push changes using JGit.

## Project Details

This project is built using:

- Java 21
- Gradle

The repository contains three main Java classes:

- `Step1_CloningARemoteRepository.java`: This class demonstrates how to clone a remote Git repository using JGit to a local directory on your machine.
- `Step2_AddingAndCommittingChanges.java`: This class demonstrates how to commit changes to a local repository using JGit.
- `Step3_PushingChanges.java`: This class demonstrates how to push changes to a remote repository using JGit.

## Clone the jgit-tutorial

To use this repository, clone it to your local machine and open it in your preferred IDE. 

```bash
git clone www.github.com/autotraderuk/jgit-tutorial.git
```

## Project setup

### SSH Key Configuration

Before you run the code in this project, you will need to set up an SSH key pair on your machine and 
either add the public key to your GitHub account or
configure the repository you intend to work with to use a Deploy Key.

The `jgit-tutorial` project relies on you setting up an environment variable that holds your private SSH key and passphrase (if you have one).

This keeps your key out of the codebase
and secure in case you decide to share the project with others or add it to version control.

You can export your SSH key by running the following command in your terminal when you open the project:

```bash
export SSH_KEY="<YOUR_PRIVATE_SSH_KEY_HERE>"
```

If you generated your SSH key with a passphrase, you will also need to export this as an environment variable:

```bash
export SSH_KEY_PASSPHRASE="<YOUR_SSH_KEY_PASSPHRASE_HERE>"
```

For more details on how to do this, you can refer to the blog article (Generating an SSH key pair).

### Setting the Remote Repository SSH URL

In the `Configuration` class, you will need to set the URL of the repository you will be cloning.

This is done by setting the `REMOTE_REPOSITORY_SSH_URL` constant.

```java
private static final String REMOTE_REPOSITORY_SSH_URL = "ssh://git@github.com/my-username/my-repo";
```

If you are using your own repository as the clone target, you need to adapt the URL to match your repository:
```
ssh://git@github.com/<your-github-name>/<your-repo-name>
```

### Setting the name of the folder to clone the repository to

In the `Configuration` class, you can set the name of the folder where the repository will be cloned.

By default, the repository will be cloned to a directory under your home directory:
`~/jgit-cloned-repositories/name-of-cloned-repo`.

You can change this by modifying the `LOCAL_REPOSITORY_DIRECTORY_NAME` constant.

## Running the different parts of the tutorial

### Cloning a Remote Repository

To run the `Step1_CloningARemoteRepository` class, use the following command at the root of the project:
```bash
./gradlew run -PmainClass=uk.co.autotrader.jgit.tutorial.Step1_CloningARemoteRepository
```

By default, the code will clone the repository to a directory under your home directory: `~/jgit-cloned-repositories/cloned-repo`.

Feel free to change this if you want to clone the repository to a different location in the `Configuration` class.

### Committing Changes

To run the `Step2_AddingAndCommittingChanges` class with a commit message,
use the following command at the root of the project:
```bash
./gradlew run -PmainClass=uk.co.autotrader.jgit.tutorial.Step2_AddingAndCommittingChanges -PcommitMessage="Your commit message here"
```

> **Note:** The commitMessage variable is passed in as a system property when running the Step2_AddingAndCommittingChanges code. 
> This allows you to specify a commit message when running the code from the command line.

### Pushing Changes

To run the `Step3_PushingChanges` class with a commit message and a branch name,
use the following command at the root of the project:
```bash
./gradlew run -PmainClass=uk.co.autotrader.jgit.tutorial.Step3_PushingChanges
```

## Contributing

This repository is publicly available for educational purposes. Feel free to fork it, submit issues, and create pull requests if you have any improvements or fixes to suggest.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
