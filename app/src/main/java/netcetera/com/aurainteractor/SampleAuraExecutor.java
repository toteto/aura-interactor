package netcetera.com.aurainteractor;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import netcetera.com.aura.AuraExecutor;

public class SampleAuraExecutor implements AuraExecutor {

  private final Executor backgroundExecutor;
  private final Handler foregroundExecutor;

  public SampleAuraExecutor() {
    this.backgroundExecutor = Executors.newSingleThreadExecutor();
    foregroundExecutor = new Handler(Looper.getMainLooper());
  }

  @Override
  public void runForeground(Runnable runnable) {
    foregroundExecutor.post(runnable);
  }

  @Override
  public void runBackground(Runnable runnable) {
    backgroundExecutor.execute(runnable);
  }
}
