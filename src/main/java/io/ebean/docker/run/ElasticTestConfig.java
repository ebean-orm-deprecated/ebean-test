package io.ebean.docker.run;

import org.avaje.docker.commands.ElasticConfig;
import org.avaje.docker.commands.ElasticContainer;

import java.util.Properties;

class ElasticTestConfig {

  private static final String[] DOCKER_PARAMS = {"containerName", "image", "internalPort", "startMode"};

  private final Properties config;

  private Properties dockerProperties = new Properties();

  ElasticTestConfig(Properties config) {
    this.config = config;
  }

  void run() {
    ElasticConfig elasticConfig = readConfig();
    if (elasticConfig != null) {
      new ElasticContainer(elasticConfig).start();
    }
  }

  ElasticConfig readConfig() {

    String version = read("version", null);
    if (version == null) {
      return null;
    }
    setElastic("version", version);
    setElastic("port", read("port", "9201"));
    for (String dockerParam : DOCKER_PARAMS) {
      String val = read(dockerParam, null);
      if (val != null) {
        setElastic(dockerParam, val);
      }
    }
    return new ElasticConfig(version, dockerProperties);
  }

  private String read(String key, String defaultValue) {
    String upperKey = Character.toUpperCase(key.charAt(0)) + key.substring(1);

    String val = config.getProperty("ebean.docstore.elastic" + upperKey, defaultValue);
    return config.getProperty("ebean.docstore.elastic." + key, val);
  }

  private void setElastic(String key, String val) {
    dockerProperties.setProperty("elastic." + key, val);
  }
}
