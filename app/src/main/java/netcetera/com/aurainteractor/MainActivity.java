package netcetera.com.aurainteractor;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


public class MainActivity extends Activity {
  private static final String LOG_TAG = MainActivity.class.getSimpleName();

  MainInteractor mainInteractor;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mainInteractor = DependencyProvider.provideMainInteractor();
  }

  @Override
  protected void onResume() {
    super.onResume();
    MainInput mainInput = new MainInput("onResume");
    mainInteractor.interact(mainInput, mainOutput);
    mainInteractor.interact(mainOutput);
  }

  private final MainOutput mainOutput = new MainOutput() {
    @Override
    public void showMessage(String message) {
      Log.d(LOG_TAG, "showMessage() called with: message = [" + message + "]");
      Log.d(LOG_TAG, "showMessage() called from main looper: " + (Looper.myLooper() == Looper.getMainLooper()));
    }

    @Override
    public void noAuraMethod(String p1, String p2) {
      Log.d(LOG_TAG, "noAuraMethod() called with: p1 = [" + p1 + "], p2 = [" + p2 + "]");
      Log.d(LOG_TAG, "noAuraMethod() called from main looper: " + (Looper.myLooper() == Looper.getMainLooper()));
    }
  };
}
