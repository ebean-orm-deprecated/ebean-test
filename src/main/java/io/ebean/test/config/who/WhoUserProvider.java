package io.ebean.test.config.who;

import io.ebean.config.CurrentUserProvider;
import io.ebean.test.UserContext;

class WhoUserProvider implements CurrentUserProvider {

  @Override
  public Object currentUser() {
    return UserContext.currentUserId();
  }
}
