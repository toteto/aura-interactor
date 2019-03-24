package com.antonioivanovski.aurainteractor;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class LoginCredentials {

  public static LoginCredentials create(String username, String password) {
    return new AutoValue_LoginCredentials(username, password);
  }

  public abstract String username();

  public abstract String password();
}
