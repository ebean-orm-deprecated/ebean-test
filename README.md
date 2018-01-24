# ebean-docker-run
Plugin that automatically starts docker containers (like Postgres, MySQL, SqlServer ElasticSearch etc). These containers are started and typically setup for testing by creating a database and user ready to run tests against.

## Prerequisite

You need docker installed locally.

## To use
#### 1) Add ebean-docker-run as a test scope dependency

```xml
    <dependency>
      <groupId>io.ebean</groupId>
      <artifactId>ebean-docker-run</artifactId>
      <version>1.4.1</version>
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

## also start a ElasticSearch container
elastic.version=5.6.0

```

#### 3) Ensure the DataSource config matches as desired

For example, edit src/test/resources/`test-ebean.properties` such that it matches the configuration of `docker-run.properties`

Example test-ebean.properties:
```properties
datasource.db.username=test_user
datasource.db.password=test
datasource.db.databaseUrl=jdbc:postgresql://localhost:6432/test_db
datasource.db.databaseDriver=org.postgresql.Driver
```


## What it does

When the Ebean container starts it finds and runs this plugin
which in turn uses https://github.com/avaje/docker-commands to read docker-run.properties
and start the configured docker containers.

What is nice is that it will check the database, database user 
(and for Postgres extensions defined like hstore) are created.

This makes it nice and easy to run tests against the target database (rather than H2).

## ebean_db system property

The `ebean_db` system property can be set which:
- Sets the default datasource Ebean will use
- If it matches a known container name like `postgres`, `mysql` or `sqlserver` then it will set `docker_run_with` such that only that container is started. 

In this way we can run tests against a specific database platform.  e.g.

```sh
mvn clean test -Debean_db=postgres
```
```sh
mvn clean test -Debean_db=sqlserver
```




## Configuration options

By default:
- The db name is `test_db`
- The db user is `test_user`
- The db password is `test`  (but for SqlServer it is `SqlS3rv#r`)

### Postgres

```properties
postgres.version=9.6
#postgres.port=6432
#postgres.dbName=test_db
#postgres.dbUser=test_user
#postgres.dbPassword=test
postgres.dbExtensions=hstore,pgcrypto
```

### MySql

```properties
mysql.version=5.7
#mysql.port=4306
#mysql.dbName=test_db
#mysql.dbUser=test_user
#mysql.dbPassword=test
```


### SqlServer

```properties
sqlserver.version=2017-CU2
#sqlserver.port=1433
#sqlserver.dbName=test_db
#sqlserver.dbUser=test_user
#sqlserver.dbPassword=SqlS3rv#r
```

### ElasticSearch

```properties
elastic.version=5.6.0
#elastic.port=9200
```
