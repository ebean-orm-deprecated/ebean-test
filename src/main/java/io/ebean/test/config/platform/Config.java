package io.ebean.test.config.platform;

import io.ebean.config.properties.PropertiesLoader;
import io.ebean.util.StringHelper;

import java.util.Properties;

/**
 * Config for a database / datasource with associated DDL mode and Docker configuration.
 */
class Config {

  /**
   * Common optional docker parameters that we just transfer to docker properties.
   */
  private static final String[] DOCKER_PARAMS = {"containerName", "image", "internalPort", "startMode", "stopMode", "maxReadyAttempts", "tmpfs", "dbAdminUser", "dbAdminPassword"};

  private final String db;
  private final String platform;

  private String databaseName;

  private final Properties properties;

  private int port;

  private String url;
  private String driver;
  private String username;
  private String password;

  private final Properties dockerProperties = new Properties();

  Config(String db, String platform, String databaseName, Properties properties) {
    this.db = db;
    this.platform = platform;
    this.databaseName = databaseName;
    this.properties = properties;
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
      throw new IllegalStateException("No ebean.test.ddlMode set?  Expect one of dropCreate, create, none or migrations.");
    }
    switch (ddlMode.toLowerCase()) {
      case "none": {
        break;
      }
      case "migrations": {
        setIfRequired("ebean.migration.run", "true");
        break;
      }
      case "create": {
        setIfRequired("ebean.ddl.generate", "true");
        setIfRequired("ebean.ddl.run", "true");
        setIfRequired("ebean.ddl.createOnly", "true");
        break;
      }
      case "dropcreate": {
        setIfRequired("ebean.ddl.generate", "true");
        setIfRequired("ebean.ddl.run", "true");
        break;
      }
      default:
        throw new IllegalStateException("Unknown ebean.test.ddlMode [" + ddlMode + "] expecting one of dropCreate, create, none or migrations.");
    }
  }

  private void setIfRequired(String fullKey, String value) {
    String existingValue = properties.getProperty(fullKey);
    if (existingValue == null) {
      PropertiesLoader.setProperty(fullKey, value);
    }
  }

  void datasourceDefaults() {
    // default username to databaseName
    if (username == null) {
      throw new IllegalStateException("username not set?");
    }
    if (password == null) {
      throw new IllegalStateException("password not set?");
    }
    datasourceProperty("username", username);
    datasourceProperty("password", password);
    datasourceProperty("url", url);
    String driverClass = datasourceProperty("driver", driver);
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

    PropertiesLoader.setProperty(dsKey, val);
  }

  void setUrl(String urlPattern) {

    String val = getPlatformKey("url", urlPattern);
    val = StringHelper.replaceString(val, "${port}", String.valueOf(port));
    val = StringHelper.replaceString(val, "${databaseName}", databaseName);
    this.url = val;
  }

  void setDriver(String driver) {
    this.driver = getPlatformKey("driver", driver);
  }

  public void setPasswordDefault() {
    setPassword("test");
  }

  /**
   * Set the username to default to database name.
   */
  void setUsernameDefault() {
    this.username = getPlatformKey("username", getPlatformKey("databaseName", databaseName));
  }

  void setPassword(String password) {
    this.password = getPlatformKey("password", password);
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

}
