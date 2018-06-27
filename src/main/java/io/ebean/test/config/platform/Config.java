package io.ebean.test.config.platform;

import io.ebean.config.ServerConfig;
import io.ebean.util.StringHelper;
import org.avaje.datasource.DataSourceConfig;

import java.util.Properties;

/**
 * Config for a database / datasource with associated DDL mode and Docker configuration.
 */
class Config {

  /**
   * Common optional docker parameters that we just transfer to docker properties.
   */
  private static final String[] DOCKER_PARAMS = {"containerName", "image", "internalPort", "startMode", "stopMode", "shutdown", "maxReadyAttempts", "tmpfs", "dbAdminUser", "dbAdminPassword"};

  private static final String DDL_MODE_OPTIONS = "dropCreate, create, none, migration, createOnly or migrationDropCreate";

  private final String db;
  private final String platform;

  private String databaseName;

  private final Properties properties;

  private int port;

  private String url;
  private String driver;
  private String schema;
  private String username;
  private String password;

  private final ServerConfig serverConfig;

  private boolean containerDropCreate;

  private final Properties dockerProperties = new Properties();

  Config(String db, String platform, String databaseName, ServerConfig serverConfig) {
    this.db = db;
    this.platform = platform;
    this.databaseName = databaseName;
    this.serverConfig = serverConfig;
    this.properties = serverConfig.getProperties();
  }

  void setDefaultPort(int defaultPort) {
    String val = getPlatformKey("port", null);
    if (val != null) {
      port = Integer.parseInt(val);
    } else {
      port = defaultPort;
    }
  }

  void ddlMode(String defaultMode) {
    String ddlMode = properties.getProperty("ebean.test.ddlMode", defaultMode);
    if (ddlMode == null) {
      throw new IllegalStateException("No ebean.test.ddlMode set?  Expect one of " + DDL_MODE_OPTIONS);
    }
    switch (ddlMode.toLowerCase()) {
      case "none": {
        disableMigrationRun();
        break;
      }
      case "migrationdropcreate":
      case "migrationsdropcreate": {
        setMigrationRun();
        containerDropCreate = true;
        break;
      }
      case "migration":
      case "migrations": {
        setMigrationRun();
        break;
      }
      case "createonly": {
        setCreate();
        break;
      }
      case "create": {
        containerDropCreate = true;
        setCreate();
        break;
      }
      case "dropcreate": {
        setDropCreate();
        break;
      }
      default:
        throw new IllegalStateException("Unknown ebean.test.ddlMode [" + ddlMode + "] expecting one of " + DDL_MODE_OPTIONS);
    }
  }

  private void setCreate() {
    setDropCreate();
    serverConfig.setDdlCreateOnly(true);
    setDdlProperty("createOnly");
  }

  private void setDropCreate() {
    disableMigrationRun();
    serverConfig.setDdlGenerate(true);
    serverConfig.setDdlRun(true);
    setDdlProperty("generate");
    setDdlProperty("run");
  }

  private void setMigrationRun() {
    serverConfig.getMigrationConfig().setRunMigration(true);
    setProperty("ebean." + db + ".migration.run", "true");
  }

  private void disableMigrationRun() {
    System.setProperty("ddl.migration.run", "false");
  }

  /**
   * Override the dataSource property.
   */
  private void setDdlProperty(String key) {
    setProperty("ebean." + db + ".ddl." + key, "true");
  }

  void datasourceDefaults() {
    // default username to databaseName
    if (username == null) {
      throw new IllegalStateException("username not set?");
    }
    if (password == null) {
      throw new IllegalStateException("password not set?");
    }

    DataSourceConfig ds = new DataSourceConfig();
    ds.setUsername(datasourceProperty("username", username));
    ds.setPassword(datasourceProperty("password", password));
    ds.setUrl(datasourceProperty("url", url));

    String driverClass = datasourceProperty("driver", driver);
    ds.setDriver(driverClass);
    serverConfig.setDataSourceConfig(ds);

    if (driverClass != null) {
      try {
        Class.forName(driverClass);
      } catch (ClassNotFoundException e) {
        throw new IllegalStateException("JDBC Driver " + driverClass + " does not appear to be in the classpath?");
      }
    }
  }

  /**
   * Override the dataSource property.
   */
  String datasourceProperty(String key, String defaultValue) {

    String val = getPlatformKey(key, defaultValue);
    setProperty("datasource." + db + "." + key, val);
    return val;
  }

  private void setProperty(String dsKey, String val) {
    properties.setProperty(dsKey, val);
  }

  void setUrl(String urlPattern) {

    String val = getPlatformKey("url", urlPattern);
    val = StringHelper.replaceString(val, "${port}", String.valueOf(port));
    val = StringHelper.replaceString(val, "${databaseName}", databaseName);
    this.url = val;
  }

  /**
   * Append to the connection URL.
   */
  void urlAppend(String dbSchemaSuffix) {
    this.url += dbSchemaSuffix;
  }

  void setDriver(String driver) {
    this.driver = getPlatformKey("driver", driver);
  }

  void setPasswordDefault() {
    setPassword("test");
  }

  private String deriveDbSchema() {
    String dbSchema = properties.getProperty("ebean.dbSchema", serverConfig.getDbSchema());
    dbSchema = properties.getProperty("ebean.test.dbSchema", dbSchema);
    return getPlatformKey("schema", dbSchema);
  }

  /**
   * Set the username to default to database name.
   */
  void setUsernameDefault() {
    this.schema = deriveDbSchema();
    String defaultValue = schema != null ? schema : getPlatformKey("databaseName", this.databaseName);
    this.username = getKey("username", getKey("dbUser", defaultValue));
  }

  String getUsername() {
    return username;
  }

  String getSchema() {
    return schema;
  }

  void setPassword(String password) {
    this.password = getKey("password", getKey("dbPassword", password));
  }

  void setUsername(String username) {
    this.username = getPlatformKey("username", username);
  }

  void setDatabaseName(String databaseName) {
    this.databaseName = getPlatformKey("databaseName", databaseName);
  }

  boolean isUseDocker() {
    String val = getPlatformKey("useDocker", properties.getProperty("ebean.test.useDocker"));
    return val == null || !val.equalsIgnoreCase("false");
  }

  void setDockerVersion(String version) {
    String val = getPlatformKey("version", version);
    dockerProperties.setProperty(dockerKey("version"), val);

    if (containerDropCreate) {
      dockerProperties.setProperty(dockerKey("startMode"), "dropCreate");
    }
    String mode = properties.getProperty("ebean.test.containerMode");
    if (mode != null) {
      dockerProperties.setProperty(dockerKey("startMode"), mode);
    }
    initDockerProperties();
  }

  void setDbExtensions(String defaultValue) {
    // ebean.test.postgres.extensions=hstore,pgcrypto
    String val = getPlatformKey("extensions", defaultValue);
    if (val != null) {
      dockerProperties.setProperty(dockerKey("dbExtensions"), trimExtensions(val));
    }
  }

  String trimExtensions(String val) {
    val = val.replaceAll(" ", "");
    val = val.replaceAll(",,", ",");
    return val;
  }

  private String getPlatformKey(String key, String defaultValue) {
    return properties.getProperty("ebean.test." + platform + "." + key, defaultValue);
  }

  private String getKey(String key, String defaultValue) {
    defaultValue = properties.getProperty("ebean.test." + key, defaultValue);
    return properties.getProperty("ebean.test." + platform + "." + key, defaultValue);
  }

  private void initDockerProperties() {

    dockerProperties.setProperty(dockerKey("port"), String.valueOf(port));
    dockerProperties.setProperty(dockerKey("dbName"), databaseName);
    dockerProperties.setProperty(dockerKey("dbUser"), username);
    dockerProperties.setProperty(dockerKey("dbPassword"), password);
    dockerProperties.setProperty(dockerKey("url"), url);
    dockerProperties.setProperty(dockerKey("driver"), driver);

    setDockerOptionalParameters();
  }

  private void setDockerOptionalParameters() {

    // check for shutdown mode on all containers
    String mode = properties.getProperty("ebean.test.shutdown");
    if (mode != null) {
      dockerProperties.setProperty(dockerKey("shutdown"), mode);
    }
    for (String key : DOCKER_PARAMS) {
      String val = getPlatformKey(key, null);
      val = properties.getProperty("docker." + platform + "." + key, val);
      if (val != null) {
        dockerProperties.setProperty(dockerKey(key), val);
      }
    }
  }

  private String dockerKey(String key) {
    return platform + "." + key;
  }

  Properties getDockerProperties() {
    return dockerProperties;
  }

  /**
   * Pretty much only for SqlServer as we have the 2 platforms we need to choose from.
   */
  void setDatabasePlatformName() {
    String databasePlatformName = getPlatformKey("databasePlatformName", null);
    if (databasePlatformName != null) {
      setProperty("ebean." + db + ".databasePlatformName", databasePlatformName);
    }
  }
}
