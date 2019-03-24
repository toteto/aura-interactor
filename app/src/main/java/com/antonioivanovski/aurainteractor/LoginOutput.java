package com.antonioivanovski.aurainteractor;

import com.antonioivanovski.aura.AuraOff;
import com.antonioivanovski.aura.AuraOutput;

@AuraOutput
public interface LoginOutput {

  void successfulLogin(String message);

  void failedLogin(Exception e);

  @AuraOff
  void loginProgress(double progress);
}
