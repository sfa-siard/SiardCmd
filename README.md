# SiardCmd - SIARD 2.2 Command-line Interface to download and upload relational databases
This package contains the SIARD command-line interface to download and upload relational databases in SIARD (Software-Independent Archival of Relational Databases) Format 2.2.

## Getting started (for developers)
### Prerequisites
- JDK 17

### Build the project
```shell
./gradlew clean build
```

### Integration tests
Runs all integration tests (Note: [TestContainers](https://testcontainers.com/) are used, so Docker is required):
```shell
./gradlew integrationTest
```

### SIARD command-line tools
To run the command-line tools `siard-from-db` and `siard-to-db`, first install the distribution:

```shell
./gradlew installDist
```

This will create the executable scripts in the `build/install/siardcmd/bin` directory. 

### Deliverables
Builds the project, runs unit tests, and packages all artifacts with additional required files for execution in a CLI into ZIP and TAR under `build/distributions/`:
```shell
./gradlew assembleDist
```

### Versioning, tags, and releases
Versions and tags are managed with the [Axion Release Plugin](https://github.com/allegro/axion-release-plugin) for Gradle.

Short overview:
```shell
./gradlew currentVersion  # Shows the current version

./gradlew release         # Creates a new release, adds a tag, and pushes it to remote
```

Once a tag is pushed to remote, GitHub Actions will create and upload deliverables specified in [deliverables.yml](.github/workflows/deliverables.yml).

## Documentation
- [User Manual](https://github.com/sfa-siard/siard-suite/blob/main/docs/user-manual/en/user-manual.adoc)
- [Software Architecture Document](https://github.com/sfa-siard/siard-suite/blob/main/docs/sad/sad.adoc)

## Declaration
Contributions to the codebase have been made with the support of Windsurf. Windsurf is AI-powered code completion tool, that is trained exclusively on natural language and source code data with [permissive licenses](https://windsurf.com/blog/copilot-trains-on-gpl-codeium-does-not). 
