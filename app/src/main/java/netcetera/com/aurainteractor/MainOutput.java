package netcetera.com.aurainteractor;

import netcetera.com.aura.AuraOff;
import netcetera.com.aura.AuraOutput;

@AuraOutput
public interface MainOutput {

  void showMessage(String message);

  @AuraOff
  void noAuraMethod(String p1, String p2);

}
