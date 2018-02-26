package io.ebean.docker.run;

import java.util.Properties;

interface PlatformSetup {

  boolean isInMemory();

  Properties setup(Config dbConfig);
}
