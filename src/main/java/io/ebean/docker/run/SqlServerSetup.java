package io.ebean.docker.run;

import java.util.Properties;

class SqlServerSetup implements PlatformSetup {

  @Override
  public Properties setup(Config config) {

    config.ddlMode("dropCreate");
    config.setDefaultPort(1433);
    config.setUsernameDefault();
    config.setPassword("SqlS3rv#r");
    config.setUrl("jdbc:sqlserver://localhost:${port};databaseName=${databaseName}");
    config.setDriver("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    config.datasourceDefaults();

    return dockerProperties(config);
  }

  private Properties dockerProperties(Config dbConfig) {

    if (!dbConfig.isUseDocker()) {
      return new Properties();
    }

    dbConfig.setDockerVersion("2017-CE");
    return dbConfig.getDockerProperties();
  }

  @Override
  public boolean isInMemory() {
    return false;
  }

}
