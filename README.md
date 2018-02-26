# ebean-test-config

Plugin that simplifies testing configuration for Ebean. This includes automatically starting docker containers (like Postgres, MySql, SqlServer, Oracle, ElasticSearch etc). 
These containers are started and typically setup for testing by creating a database and user ready to run tests against.

## Prerequisite

You need docker installed locally.

## To use
#### 1) Add ebean-test-config as a test scope dependency

```xml
    <dependency>
      <groupId>io.ebean.test</groupId>
      <artifactId>ebean-test-config</artifactId>
      <version>2.1.1</version>
      <scope>test</scope>
    </dependency>
```

#### 2) Add a application-test.yml file to src/test/resources

Example: Just using H2

```yml
ebean:
  test:
    platform: h2    
```


Example: Using Postgres via docker with defaults

```yml
ebean:
  test:
    platform: postgres
    dbName: test_myapp
    ddlMode: dropCreate # none | dropCreate | create | migrations    
```
Note that when we use Postgres, MySql, Sql Server, Oracle or DB2 we need to specify `dbName`. 
This should be a name that does not clash with other projects that might also test against
the same docker container.


## db system property

The `db` system property can be set which:
- Sets the default datasource Ebean will use
- If it matches a known container name like `postgres`, `mysql`, `oracle` or `sqlserver` then it will start an appropriate docker container to run the tests against. 

In this way we can run tests against a specific database platform.  e.g.

```sh
mvn clean test -Ddb=postgres
```
```sh
mvn clean test -Ddb=sqlserver
```
