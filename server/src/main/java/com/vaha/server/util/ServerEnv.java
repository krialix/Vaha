package com.vaha.server.util;

import com.google.appengine.api.utils.SystemProperty;

public final class ServerEnv {

  public static boolean isDev() {
    return SystemProperty.environment.value() == SystemProperty.Environment.Value.Development;
  }

  public static boolean isTest() {
    return SystemProperty.environment.value() == null;
  }
}
