package io.ebean.test.config;

import io.ebean.config.AutoConfigure;
import io.ebean.config.ServerConfig;
import io.ebean.datasource.DataSourceConfig;
import io.ebean.test.config.platform.PlatformAutoConfig;
import io.ebean.test.config.provider.ProviderAutoConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Automatically configure ServerConfig for testing purposes.
 * <p>
 * Can setup and execute docker based databases and other containers.
 * Can setup DataSource configuration (to match docker db setup).
 * Can setup a CurrentUserProvider and CurrentTenantProvider for testing.
 * Can setup a EncryptKeyManager for testing purposes with fixed key.
 */
public class AutoConfigureForTesting implements AutoConfigure {

  private static final Logger log = LoggerFactory.getLogger(AutoConfigureForTesting.class);

  /**
   * System property that can override the platform.  mvn clean test -Ddb=sqlserver
   */
  private final String environmentDb = System.getProperty("db");

  @Override
  public void preConfigure(ServerConfig serverConfig) {

    Properties properties = serverConfig.getProperties();
    if (isExtraServer(serverConfig, properties)) {
      log.debug("skip preConfigure on extra DB name:{}", serverConfig.getName());
      return;
    }

    String testPlatform = properties.getProperty("ebean.test.platform");
    log.debug("automatic testing config - with ebean.test.platform:{} environment db:{} name:{}", testPlatform, environmentDb, serverConfig.getName());

    if (RunOnceMarker.isRun()) {
      setupPlatform(environmentDb, serverConfig);
    }
  }

  @Override
  public void postConfigure(ServerConfig serverConfig) {
    log.trace("automatic testing config - postConfigure on name:{}", serverConfig.getName());
    if (isExtraServer(serverConfig, serverConfig.getProperties())) {
      setupExtraDataSourceIfNecessary(serverConfig);
    }
    setupProviders(serverConfig);
  }

  /**
   * Check if this is not the primary server and return true if that is the case.
   */
  private boolean isExtraServer(ServerConfig serverConfig, Properties properties) {
    String extraDb = properties.getProperty("ebean.test.extraDb");
    if (extraDb != null && extraDb.equals(serverConfig.getName())) {
      serverConfig.setDefaultServer(false);
      return true;
    }
    return false;
  }

  /**
   * Setup the DataSource on the extra database if necessary.
   */
  private void setupExtraDataSourceIfNecessary(ServerConfig serverConfig) {

    DataSourceConfig dataSourceConfig = serverConfig.getDataSourceConfig();
    if (dataSourceConfig == null || dataSourceConfig.getUsername() == null) {
      new PlatformAutoConfig(environmentDb, serverConfig)
        .configExtraDataSource();
    }
  }

  /**
   * Setup support for Who, Multi-Tenant and DB encryption if they are not already set.
   */
  private void setupProviders(ServerConfig serverConfig) {
    new ProviderAutoConfig(serverConfig).run();
  }

  /**
   * Setup the platform for testing including docker as needed and adjusting datasource config as needed.
   */
  private void setupPlatform(String db, ServerConfig serverConfig) {
    new PlatformAutoConfig(db, serverConfig).run();
  }
}
