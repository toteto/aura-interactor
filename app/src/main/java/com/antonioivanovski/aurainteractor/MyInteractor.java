package com.antonioivanovski.aurainteractor;

import com.antonioivanovski.aura.AuraInteractor;

@AuraInteractor
public interface MyInteractor {

  void interact1(MyInput input, MyOutput myOutput);

  void interact2(MyOutput myOutput);
}
