package netcetera.com.aurainteractor;

import netcetera.com.aura.AuraInteractor;

@AuraInteractor
public interface MainInteractor {

  void interact(MainInput input, MainOutput mainOutput);

  void interact(MainOutput mainOutput);
}
