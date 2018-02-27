package io.ebean.test.config.who;

import io.ebean.config.CurrentTenantProvider;
import io.ebean.config.CurrentUserProvider;
import io.ebean.config.EncryptKeyManager;
import io.ebean.config.ServerConfig;

import java.util.Properties;

public class WhoAutoConfig {

  private final ServerConfig serverConfig;
  private final Properties properties;

  public WhoAutoConfig(ServerConfig serverConfig, Properties properties) {
    this.serverConfig = serverConfig;
    this.properties = properties;
  }

  public void run() {
    CurrentUserProvider provider = serverConfig.getCurrentUserProvider();
    if (provider == null) {
      serverConfig.setCurrentUserProvider(new WhoUserProvider());
    }

    CurrentTenantProvider tenantProvider = serverConfig.getCurrentTenantProvider();
    if (tenantProvider == null) {
      serverConfig.setCurrentTenantProvider(new WhoTenantProvider());
    }

    EncryptKeyManager keyManager = serverConfig.getEncryptKeyManager();
    if (keyManager == null) {
      // Must be 16 Chars for Oracle function
      String keyVal = properties.getProperty("ebean.test.encryptKey", "simple0123456789");
      serverConfig.setEncryptKeyManager(new FixedEncyptKeyManager(keyVal));
    }

  }
}
