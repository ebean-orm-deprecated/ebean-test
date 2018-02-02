package io.ebean.docker.run;

import io.ebeaninternal.api.SpiContainerBootup;
import org.avaje.docker.container.AutoStart;

import java.util.Arrays;
import java.util.List;

/**
 * Invokes docker commands AutoStart.
 *
 * Effectively looks for docker-run.properties and automatically starts appropriate docker containers
 * (for Postgres, ElasticSearch etc) based on the configuration.
 */
public class Bootup implements SpiContainerBootup {

  /**
   * "Docker Run" Known DB containers.
   */
  private static List<String> knownContainers = Arrays.asList("postgres", "mysql", "sqlserver", "oracle");

  /**
   * Start docker containers as configured in docker-run.properties.
   */
  public void bootup() {

    setDockerRunWith();
    AutoStart.run();
  }

  private void setDockerRunWith() {

    String runWith = System.getProperty("docker_run_with");
    if (runWith == null) {
      runWith = System.getProperty("db");
      if (isKnownDockerName(runWith)) {
        // set docker run specific system property such that
        // it only starts the container we want to run with
        System.setProperty("docker_run_with", runWith);
      }
    }
  }

  /**
   * Return true if the ebean_db name is a known docker container name (of docker-run).
   */
  private boolean isKnownDockerName(String runWith) {
    return runWith != null && knownContainers.contains(runWith);
  }
}

