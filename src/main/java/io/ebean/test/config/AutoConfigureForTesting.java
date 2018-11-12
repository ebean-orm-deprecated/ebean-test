package io.ebean.test.config;

import io.ebean.config.AutoConfigure;
import io.ebean.config.ServerConfig;
import io.ebean.test.config.platform.PlatformAutoConfig;
import io.ebean.test.config.provider.ProviderAutoConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    String testPlatform = serverConfig.getProperties().getProperty("ebean.test.platform");
    log.debug("automatic testing config - with ebean.test.platform:{} environment db:{} name:{} defaultServer:{}", testPlatform, environmentDb, serverConfig.getName(), serverConfig.isDefaultServer());

    if (RunOnceMarker.isRun()) {
      setupPlatform(environmentDb, serverConfig);
    }
  }

  @Override
  public void postConfigure(ServerConfig serverConfig) {
    log.trace("automatic testing config - postConfigure");
    setupProviders(serverConfig);
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
