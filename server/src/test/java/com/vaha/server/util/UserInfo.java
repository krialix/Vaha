package com.vaha.server.util;

public class UserInfo {

  private final String email;
  private final String authDomain;
  private final String gaeUserId;
  private final boolean isAdmin;
  private final boolean isLoggedIn;

  private UserInfo(Builder builder) {
    email = builder.email;
    authDomain = builder.authDomain;
    gaeUserId = builder.gaeUserId;
    isAdmin = builder.isAdmin;
    isLoggedIn = builder.isLoggedIn;
  }

  /** Creates a new logged-in non-admin user instance. */
  public static UserInfo create(String email, String gaeUserId) {
    String authDomain = email.substring(email.indexOf('@') + 1);
    return newBuilder(email, authDomain, gaeUserId, false, true).build();
  }

  /** Creates a new logged-in admin user instance. */
  public static UserInfo createAdmin(String email, String gaeUserId) {
    String authDomain = email.substring(email.indexOf('@') + 1);
    return newBuilder(email, authDomain, gaeUserId, true, true).build();
  }

  /** Returns a logged-out user instance. */
  public static UserInfo loggedOut() {
    return newBuilder("", "", "", false, false).build();
  }

  public static Builder newBuilder(
      String email, String authDomain, String gaeUserId, boolean isAdmin, boolean isLoggedIn) {
    return new Builder(email, authDomain, gaeUserId, isAdmin, isLoggedIn);
  }

  public String getEmail() {
    return email;
  }

  public String getAuthDomain() {
    return authDomain;
  }

  public String getGaeUserId() {
    return gaeUserId;
  }

  public boolean isAdmin() {
    return isAdmin;
  }

  public boolean isLoggedIn() {
    return isLoggedIn;
  }

  public static final class Builder {
    private final String email;
    private final String authDomain;
    private final String gaeUserId;
    private final boolean isAdmin;
    private final boolean isLoggedIn;

    private Builder(
        String email, String authDomain, String gaeUserId, boolean isAdmin, boolean isLoggedIn) {
      this.email = email;
      this.authDomain = authDomain;
      this.gaeUserId = gaeUserId;
      this.isAdmin = isAdmin;
      this.isLoggedIn = isLoggedIn;
    }

    public UserInfo build() {
      return new UserInfo(this);
    }
  }
}
