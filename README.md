# SiardCmd - Command-line Interface to download and upload relational databases

This package contains the SIARD command-line interface to download and upload
relational databases in the SIARD (Software-Independent Archival of
Relational Databases) Format 2.2.

# Development

### Prerequisites
- JDK 8
- Docker (Used for integration tests)

### Run

Run the application from the command line:

```shell
./gradlew run
```

### Build application artifacts

Run tests and build the package

```shell
./gradlew build
```

### Execute integration tests

Runs all integration tests (Note: [TestContainers](https://testcontainers.com/)
is used, so Docker is required)

```shell
./gradlew build
```

### Create deliverables

It runs tests, builds the artifacts, packages them, and includes
all additional required files for execution in a CLI into a 
single zip file under 'libs/deliverables'.

```shell
./gradlew packDeliverables
```

## Versioning, tags and releases

Versions and tags are managed with the Axion Release Plugin for Gradle (https://github.com/allegro/axion-release-plugin)

Short overview:

```shell
./gradlew currentVersion # show the current version

./gradlew release        # creates a new release adds a tag and pushes it to remote.
```

Run the release task to create a new patch version and push it to remote. The GitHub Actions will create the
deliverables.

## Using Gradle and Testcontainers

A gradual migration to gradle has started. The roadmap for this migration looks as follows:

- [x] Enable unit tests using testcontainers
- [x] Equivalent testing restults for ant and gradle tests (see github pipelines)
- [x] Create archives and zip distribution with gradle that are equivalent to the ones generated with ant
- [x] Remove ant as build tool
- [x] Migrate project folder structure to match the standard maven project layout
- [ ] Remove the lib folder and let gradle manage all dependencies

In order to implement the last step, all other dependent siard repositories must be migrated to gradle


## Documentation
[./doc/manual/user/index.html](./doc/manual/user/index.html) contains the manual for using the binaries.

[./doc/manual/developer/index.html](./doc/manual/developer/index.html) is the manual for developers wishing to
build the binaries or work on the code.  

For building the binaries, Java JDK (1.8 or higher) and Ant must 
have been installed. A copy of build.properties.template must be called 
build.properties. In it using a text editor the local values must be 
entered as directed by the comments.

SiardCmd 2.1.58 has been built and tested with JAVA JDK 1.8, 9, and 10.

More information about the build process can be found in
[./doc/manual/developer/build.html](./doc/manual/developer/build.html).
