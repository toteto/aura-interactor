package netcetera.com.aurainteractor;

public class MyInteractorImpl implements MyInteractor {

  @Override
  public void interact1(MyInput input, MyOutput myOutput) {
    myOutput.myMethod(input.getMessage());
  }

  @Override
  public void interact2(MyOutput myOutput) {
    myOutput.backgroundMyMethod("p1", "p2");
  }
}
