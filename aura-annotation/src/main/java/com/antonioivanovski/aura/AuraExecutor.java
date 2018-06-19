package com.antonioivanovski.aura;

/**
 * Contract that makes possible to execute {@link AuraInteractor} in the background and {@link AuraOutput} in the
 * foreground.
 */
public interface AuraExecutor {

  /**
   * Execute the provided {@link Runnable} on the foreground thread.
   *
   * @param runnable {@link Runnable}
   */
  void runForeground(Runnable runnable);

  /**
   * Execute the provided {@link Runnable} in background thread.
   *
   * @param runnable {@link Runnable}
   */
  void runBackground(Runnable runnable);
}
