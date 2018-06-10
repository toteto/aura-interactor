package netcetera.com.aurainteractor;

import netcetera.com.aura.AuraExecutor;

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

  public static MyInteractor provideMainInteractor() {
    MyInteractor myInteractor = new MyInteractorImpl();
    return new AuraInteractor_MyInteractor(myInteractor, provideAuraExecutor());
  }

}
