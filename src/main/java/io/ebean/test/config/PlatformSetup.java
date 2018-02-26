package io.ebean.test.config;

import java.util.Properties;

interface PlatformSetup {

  boolean isInMemory();

  Properties setup(Config dbConfig);
}
