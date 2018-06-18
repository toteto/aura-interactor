package com.antonioivanovski.aurainteractor;

import com.antonioivanovski.aura.AuraOff;
import com.antonioivanovski.aura.AuraOutput;

@AuraOutput
public interface MyOutput {

  void myMethod(String message);

  @AuraOff
  void backgroundMyMethod(String p1, String p2);

}
