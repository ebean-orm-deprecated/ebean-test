package io.ebean.docker.run;

import io.ebeaninternal.api.SpiContainerBootup;
import org.avaje.docker.container.AutoStart;

/**
 * Invokes docker commands AutoStart.
 *
 * Effectively looks for docker-run.properties and automatically starts appropriate docker containers
 * (for Postgres, ElasticSearch etc) based on the configuration.
 */
public class Bootup implements SpiContainerBootup {

  /**
   * Start docker containers as configured in docker-run.properties.
   */
  public void bootup() {

    AutoStart.run();
  }
}

