package com.antonioivanovski.aurainteractor;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity implements LoginOutput {

  private static final String LOG_TAG = MainActivity.class.getSimpleName();

  @BindView(R.id.input_username)
  EditText inputUsername;

  @BindView(R.id.input_password)
  EditText inputPassword;

  @BindView(R.id.progress_bar)
  ProgressBar progressBar;

  LoginInteractor loginInteractor;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    loginInteractor = DependencyProvider.provideLoginInteractor();
  }

  @OnClick(R.id.button_login_with_credentials)
  void loginWithCredentials() {
    String username = inputUsername.getText().toString();
    String password = inputPassword.getText().toString();
    LoginCredentials loginCredentials = LoginCredentials.create(username, password);

    progressBar.setVisibility(View.VISIBLE);
    loginInteractor.login(loginCredentials, this);
  }

  @OnClick(R.id.button_login_as_guest)
  void loginAsGuest() {
    progressBar.setVisibility(View.VISIBLE);
    loginInteractor.guestLogin(this);
  }

  @Override
  public void successfulLogin(String message) {
    progressBar.setVisibility(View.GONE);
    Log.d(LOG_TAG, message);
  }

  @Override
  public void failedLogin(Exception e) {
    progressBar.setVisibility(View.GONE);
    Log.w(LOG_TAG, "Login failed.", e);
  }

  @Override
  public void loginProgress(double progress) {
    progressBar.setProgress((int)(progress * 100));
    Log.v(LOG_TAG, Double.toString(progress * 100));
  }
}
