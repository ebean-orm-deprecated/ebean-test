# ebean-docker-run
Plugin that automatically starts docker containers (like Postgres, MySQL, ElasticSearch etc) typically for testing

## To use
- Add ebean-docker-run as a test scope dependency

```xml
    <dependency>
      <groupId>io.ebean</groupId>
      <artifactId>ebean-docker-run</artifactId>
      <version>1.1</version>
      <scope>test</scope>
    </dependency>
```

- Add a docker-run.properties file to src/test/resources

```properties
dbPlatform=postgres

# define db name, user etc
dbName=test_db
dbUser=test_user
dbPassword=test
```

## What it does

When the Ebean container starts it finds and runs this plugin
which in turn uses `avaje docker-commands` to read docker-run.properties
and start the configured docker containers.

What is nice is that it will check the database, database user 
(and for Postgres extensions defined like hstore) are created.

This makes it nice and easy to run tests against the target database (rather than H2).
