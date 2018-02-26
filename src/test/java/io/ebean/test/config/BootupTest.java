package io.ebean.test.config;

import org.junit.Test;


public class BootupTest {

  @Test
  public void bootup_all() {
    new Bootup().bootup();
  }

  @Test
  public void bootup_runWith() {

    System.setProperty("ebean_db", "sqlserver");

    new Bootup().bootup();
  }
}
