package com.antonioivanovski.aurainteractor;

import com.antonioivanovski.aura.AuraExecutor;

public final class DependencyProvider {
  private static AuraExecutor auraExecutorInstance;

  private DependencyProvider() {
    //no instance
  }

  private static AuraExecutor provideAuraExecutor() {
    if (auraExecutorInstance == null) {
      auraExecutorInstance = new SampleAuraExecutor();
    }
    return auraExecutorInstance;
  }

  public static LoginInteractor provideLoginInteractor() {
    LoginInteractor loginInteractor = new LoginInteractorImpl();
    return new AuraInteractor_LoginInteractor(loginInteractor, provideAuraExecutor());
  }

}
