package com.antonioivanovski.aurainteractor;

import com.antonioivanovski.aura.AuraInteractor;

@AuraInteractor
public interface LoginInteractor {

  void login(LoginCredentials input, LoginOutput loginOutput);

  void guestLogin(LoginOutput loginOutput);
}
