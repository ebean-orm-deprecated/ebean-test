package io.ebean.test.config.platform;

import java.util.Properties;

class HanaSetup implements PlatformSetup {

  @Override
  public Properties setup(Config config) {

    config.setDatabasePlatformName();

    config.ddlMode("dropCreate");
    config.setDefaultPort(39017);
    config.setUsernameDefault();
    config.setPassword("HXEHana1");
    config.setDatabaseName("HXE");
    config.setUrl("jdbc:sap://localhost:${port}/?databaseName=${databaseName}");
    config.setDriver("com.sap.db.jdbc.Driver");
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
  public boolean isLocal() {
    return false;
  }

  @Override
  public void setupExtraDbDataSource(Config config) {
    // not supported yet
  }
  
}
