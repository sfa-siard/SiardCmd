# SiardCmd - Command-line Interface to download and upload relational databases

This package contains the SIARD command-line interface to download and upload
relational databases in the SIARD (Software-Independent Archival of
Relational Databases) Format 2.1.

## Getting started (for developers)

Clone this repository:

```shell
git clone git@github.com:sfa-siard/SiardCmd.git
```

Rename `build.properties.template` to `build.properties` and adjust the configuration to your system.

Check your configuration:
```shell
ant check
```

Start all databases with docker-compose:
```shell
docker-compose up -d
```

Run Database specific tests
```shell
ant tests-<dbname>
```

Valid values for `db-name`

* access
* aw
* db2
* dvd
* h2
* mssql
* mysql
* nw
* oe
* oracle
* postgres


Run all tests and create release:

```shell
ant release
```

## Using Gradle and Testcontainers

A gradual migration to gradle has started. The roadmap for this migration looks as follows:

[x]: Enable unit tests using testcontainers
[ ]: Equivalent testing restults for ant and gradle tests (see github pipelines)
[ ]: Create archives and zip distribution with gradle that are equivalent to the ones generated with ant
[ ]: Remove ant as build tool
[ ]: Migrate project folder structure to match the standard maven project layout
[ ]: Remove the lib folder and let gradle manage all dependencies

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


## IDE

The project can be opened in Eclipse and Intellij - both are optional.


