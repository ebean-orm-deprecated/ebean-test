package io.ebean.test.config;

import java.util.Properties;

class H2Setup implements PlatformSetup {

  @Override
  public Properties setup(Config config) {

    config.ddlMode("create");
    config.setUsername("sa");
    config.setPassword("");
    config.setUrl("jdbc:h2:mem:${databaseName}");
    config.setDriver("org.h2.Driver");
    config.datasourceDefaults();

    // return empty properties
    return new Properties();
  }

  @Override
  public boolean isInMemory() {
    return true;
  }
}
