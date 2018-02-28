package io.ebean.test.config.platform;

import java.util.Properties;

interface PlatformSetup {

  boolean isLocal();

  Properties setup(Config dbConfig);
}
