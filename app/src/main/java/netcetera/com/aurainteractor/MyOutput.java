package netcetera.com.aurainteractor;

import netcetera.com.aura.AuraOff;
import netcetera.com.aura.AuraOutput;

@AuraOutput
public interface MyOutput {

  void myMethod(String message);

  @AuraOff
  void backgroundMyMethod(String p1, String p2);

}
