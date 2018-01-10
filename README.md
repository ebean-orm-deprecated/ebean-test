# ebean-docker-run
Plugin that automatically starts docker containers (like Postgres, MySQL, ElasticSearch etc) typically for testing

## Prerequisite

You need docker installed locally.

## To use
#### 1) Add ebean-docker-run as a test scope dependency

```xml
    <dependency>
      <groupId>io.ebean</groupId>
      <artifactId>ebean-docker-run</artifactId>
      <version>1.3.1</version>
      <scope>test</scope>
    </dependency>
```

#### 2) Add a docker-run.properties file to src/test/resources

```properties

## start a Postgres container
postgres.version=9.6

postgres.dbName=test_db
postgres.dbUser=test_user
postgres.dbPassword=test
postgres.dbExtensions=hstore,pgcrypto

## start a ElasticSearch container
elastic.version=5.6.0

```

## What it does

When the Ebean container starts it finds and runs this plugin
which in turn uses `avaje docker-commands` to read docker-run.properties
and start the configured docker containers.

What is nice is that it will check the database, database user 
(and for Postgres extensions defined like hstore) are created.

This makes it nice and easy to run tests against the target database (rather than H2).

## Configuration options

Need to complete this ...
