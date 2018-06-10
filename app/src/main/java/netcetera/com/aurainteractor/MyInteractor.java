package netcetera.com.aurainteractor;

import netcetera.com.aura.AuraInteractor;

@AuraInteractor
public interface MyInteractor {

  void interact1(MyInput input, MyOutput myOutput);

  void interact2(MyOutput myOutput);
}
