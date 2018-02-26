package io.ebean.test.config;

import java.util.Properties;

class MySqlSetup implements PlatformSetup {

  @Override
  public Properties setup(Config config) {

    int defaultPort = config.isUseDocker() ? 4306 : 3306;

    config.ddlMode("dropCreate");
    config.setDefaultPort(defaultPort);
    config.setUsernameDefault();
    config.setPasswordDefault();
    config.setUrl("jdbc:mysql://localhost:${port}/${databaseName}");
    config.setDriver("com.mysql.jdbc.Driver");
    config.datasourceDefaults();

    return dockerProperties(config);
  }

  private Properties dockerProperties(Config dbConfig) {

    if (!dbConfig.isUseDocker()) {
      return new Properties();
    }

    dbConfig.setDockerVersion("5.6");
    return dbConfig.getDockerProperties();
  }

  @Override
  public boolean isInMemory() {
    return false;
  }

}
