package com.antonioivanovski.aurainteractor;

import java.security.SecureRandom;
import java.util.Random;

public class LoginInteractorImpl implements LoginInteractor {

  @Override
  public void login(LoginCredentials input, LoginOutput loginOutput) {
    mockLogin(loginOutput);
  }

  @Override
  public void guestLogin(LoginOutput loginOutput) {
    mockLogin(loginOutput);
  }

  private void mockLogin(LoginOutput loginOutput) {
    Random random = new SecureRandom();
    try {
      for (int i = 0; i <= 100; i++) {
        loginOutput.loginProgress(i / 100d);
        Thread.sleep(random.nextInt(300));
      }
      loginOutput.successfulLogin("Successful login");
    } catch (InterruptedException e) {
      loginOutput.failedLogin(e);
    }
  }
}
