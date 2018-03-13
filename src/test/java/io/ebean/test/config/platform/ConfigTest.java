package io.ebean.test.config.platform;

import io.ebean.config.ServerConfig;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class ConfigTest {

  @Test
  public void trimExtensions() {

    Config config = new Config("db", "db", "db", new ServerConfig());

    assertThat(config.trimExtensions("a,b")).isEqualTo("a,b");
    assertThat(config.trimExtensions(" a , b ")).isEqualTo("a,b");
    assertThat(config.trimExtensions(" a , , b ")).isEqualTo("a,b");
  }
}
