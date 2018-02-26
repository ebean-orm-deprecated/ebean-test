package io.ebean.test.config;

import java.util.Properties;

class OracleSetup implements PlatformSetup {

  @Override
  public Properties setup(Config config) {

    config.ddlMode("dropCreate");
    config.setDefaultPort(1521);
    config.setUsernameDefault();
    config.setPasswordDefault();
    config.setDatabaseName("XE");
    config.setUrl("jdbc:oracle:thin:@localhost:${port}:${databaseName}");
    config.setDriver("oracle.jdbc.driver.OracleDriver");
    config.datasourceDefaults();
    return dockerProperties(config);
  }

  private Properties dockerProperties(Config dbConfig) {

    if (!dbConfig.isUseDocker()) {
      return new Properties();
    }

    dbConfig.setDockerVersion("latest");
    return dbConfig.getDockerProperties();
  }

  @Override
  public boolean isInMemory() {
    return false;
  }

}
