package io.ebean.test.config;

import io.ebean.config.AutoConfigure;
import io.ebean.config.ServerConfig;
import io.ebean.config.properties.PropertiesLoader;
import io.ebean.test.config.platform.PlatformAutoConfig;
import io.ebean.test.config.who.WhoAutoConfig;

import java.util.Properties;

public class OnAutoConfigure implements AutoConfigure {

  @Override
  public void configure(ServerConfig serverConfig) {
    if (serverConfig.isDefaultServer() && RunOnceMarker.isRun()) {

      String db = System.getProperty("db");
      Properties properties = PropertiesLoader.load();

      setupPlatform(db, properties);
      setupWho(serverConfig, properties);
    }
  }

  /**
   * Setup some support for Who, Multi-Tenant and DB encryption.
   */
  private void setupWho(ServerConfig serverConfig, Properties properties) {
    new WhoAutoConfig(serverConfig, properties).run();
  }

  /**
   * Setup the platform for testing including docker as needed.
   */
  private void setupPlatform(String db, Properties properties) {
    new PlatformAutoConfig(db, properties).run();
  }
}
