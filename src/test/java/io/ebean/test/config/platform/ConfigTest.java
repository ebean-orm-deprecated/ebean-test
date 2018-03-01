package io.ebean.test.config.platform;

import org.junit.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;


public class ConfigTest {

  @Test
  public void trimExtensions() {

    Config config = new Config("db", "db", "db", new Properties());

    assertThat(config.trimExtensions("a,b")).isEqualTo("a,b");
    assertThat(config.trimExtensions(" a , b ")).isEqualTo("a,b");
    assertThat(config.trimExtensions(" a , , b ")).isEqualTo("a,b");
  }
}
