package io.ebean.test.config.platform;

import java.util.Properties;

interface PlatformSetup {

  boolean isInMemory();

  Properties setup(Config dbConfig);
}
