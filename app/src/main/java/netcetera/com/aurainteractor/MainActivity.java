package netcetera.com.aurainteractor;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;


public class MainActivity extends Activity {
  private static final String LOG_TAG = MainActivity.class.getSimpleName();

  MyInteractor myInteractor;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    myInteractor = DependencyProvider.provideMainInteractor();
  }

  @Override
  protected void onResume() {
    super.onResume();
    MyInput myInput = new MyInput("onResume");
    myInteractor.interact1(myInput, myOutput);
    myInteractor.interact2(myOutput);
  }

  private final MyOutput myOutput = new MyOutput() {
    @Override
    public void myMethod(String message) {
      Log.d(LOG_TAG, "myMethod() called with: message = [" + message + "]");
      Log.d(LOG_TAG, "myMethod() called from main looper: " + (Looper.myLooper() == Looper.getMainLooper()));
    }

    @Override
    public void backgroundMyMethod(String p1, String p2) {
      Log.d(LOG_TAG, "backgroundMyMethod() called with: p1 = [" + p1 + "], p2 = [" + p2 + "]");
      Log.d(LOG_TAG, "backgroundMyMethod() called from main looper: " + (Looper.myLooper() == Looper.getMainLooper()));
    }
  };
}
