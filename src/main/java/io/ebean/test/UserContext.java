package io.ebean.test;

public class UserContext {

  private static final UserContextThreadLocal local = new UserContextThreadLocal();

  private Object userId;
  private Object tenantId;

  private UserContext() {
  }

  public static Object currentUserId() {
    return local.get().userId;
  }

  public static Object currentTenantId() {
    return local.get().tenantId;
  }

  public static void setUserId(Object userId) {
    local.get().userId = userId;
  }

  public static void setTenantId(Object tenantId) {
    local.get().tenantId = tenantId;
  }

  public static void reset() {
    local.remove();
  }


  public static void set(Object userId, String tenantId) {
    UserContext userContext = local.get();
    userContext.userId = userId;
    userContext.tenantId = tenantId;
  }


  private static class UserContextThreadLocal extends ThreadLocal<UserContext> {

    @Override
    protected UserContext initialValue() {
      return new UserContext();
    }
  }
}
