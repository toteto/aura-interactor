package netcetera.com.aurainteractor;

public class MainInteractorImpl implements MainInteractor {

  @Override
  public void interact(MainInput input, MainOutput mainOutput) {
    mainOutput.showMessage(input.getMessage());
  }

  @Override
  public void interact(MainOutput mainOutput) {
    mainOutput.noAuraMethod("p1", "p2");
  }
}
