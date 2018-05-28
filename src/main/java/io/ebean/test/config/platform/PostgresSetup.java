package io.ebean.test.config.platform;

import java.util.Properties;

class PostgresSetup implements PlatformSetup {

  @Override
  public Properties setup(Config config) {

    int defaultPort = config.isUseDocker() ? 6432 : 5432;

    config.ddlMode("dropCreate");
    config.setDefaultPort(defaultPort);
    config.setUsernameDefault();
    config.setPasswordDefault();
    config.setUrl("jdbc:postgresql://localhost:${port}/${databaseName}");
    config.setDriver("org.postgresql.Driver");
    config.datasourceDefaults();

    return dockerProperties(config);
  }

  private Properties dockerProperties(Config config) {

    if (!config.isUseDocker()) {
      return new Properties();
    }

    config.setDockerVersion("10");
    config.setDbExtensions("hstore,pgcrypto");
    return config.getDockerProperties();
  }

  @Override
  public boolean isLocal() {
    return false;
  }

}
