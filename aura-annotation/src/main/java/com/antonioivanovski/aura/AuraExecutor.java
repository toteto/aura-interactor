package com.antonioivanovski.aura;

public interface AuraExecutor {

  void runForeground(Runnable runnable);

  void runBackground(Runnable runnable);
}
