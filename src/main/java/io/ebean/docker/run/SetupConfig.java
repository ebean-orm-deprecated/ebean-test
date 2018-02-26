package io.ebean.docker.run;

import io.ebean.config.properties.PropertiesLoader;
import io.ebean.util.StringHelper;
import org.avaje.docker.container.ContainerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

class SetupConfig {

  private static final Logger log = LoggerFactory.getLogger(SetupConfig.class);

  /**
   * Known platforms we can setup locally or via docker container.
   */
  private static Map<String, PlatformSetup> KNOWN_PLATFORMS = new HashMap<>();
  static {
    KNOWN_PLATFORMS.put("h2", new H2Setup());
    KNOWN_PLATFORMS.put("sqlite", new H2Setup());
    KNOWN_PLATFORMS.put("postgres", new PostgresSetup());
    KNOWN_PLATFORMS.put("mysql", new MySqlSetup());
    KNOWN_PLATFORMS.put("sqlserver", new SqlServerSetup());
    KNOWN_PLATFORMS.put("oracle", new OracleSetup());
  }

  private String db;

  private String platform;

  private PlatformSetup platformSetup;

  private Properties properties;

  private String databaseName;

  SetupConfig() {
    this.db = System.getProperty("db");
    this.properties = PropertiesLoader.load();
  }

  /**
   * Run setting up for testing.
   */
  void run() {
    determineTestPlatform();
    if (isKnownPlatform()) {
      readDbName();
      setupForTesting();
    }
  }

  private void setupForTesting() {

    new ElasticTestConfig(properties).run();

    Properties dockerProperties = platformSetup.setup(new Config(db, platform, databaseName, properties));
    if (!dockerProperties.isEmpty()) {
      if (new Config(db, platform, databaseName, properties).isDebug()) {
        log.info("Docker properties: {}", dockerProperties);
      } else {
        log.debug("Docker properties: {}", dockerProperties);
      }
      // start the docker container with appropriate configuration
      new ContainerFactory(dockerProperties, platform).startContainers();
    }
  }

  private void readDbName() {
    databaseName = properties.getProperty("ebean.test.dbName");
    if (databaseName == null) {
      if (inMemoryDb()) {
        databaseName = "test_db";
      } else {
        throw new IllegalStateException("ebean.test.dbName is not set but required for testing configuration with platform " + platform);
      }
    }
  }

  private boolean inMemoryDb() {
    return platformSetup.isInMemory();
  }

  /**
   * Return true if we match a known platform and know how to set it up for testing (via docker usually).
   */
  private boolean isKnownPlatform() {
    if (platform == null) {
      return false;
    }
    this.platformSetup = KNOWN_PLATFORMS.get(platform);
    return platformSetup != null;
  }

  /**
   * Determine the platform we are going to use to run testing.
   */
  private void determineTestPlatform() {

    String testPlatforms = properties.getProperty("ebean.test.platforms", properties.getProperty("ebean.test.platform"));
    if (testPlatforms != null && !testPlatforms.isEmpty()) {
      if (db == null) {
        // just using the first platform
        platform = StringHelper.splitNames(testPlatforms)[0];
        db = "db";
      } else {
        // using command line system property to test alternate platform
        // and we expect db to match a platform name
        platform = db;
      }
    }
  }
}
