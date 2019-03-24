[![Build Status](https://travis-ci.org/toteto/aura-interactor.svg?branch=master)](https://travis-ci.org/toteto/aura-interactor)
[![Bintray](https://img.shields.io/bintray/v/toteto/maven/Aura-Interactor.svg)](https://bintray.com/toteto/maven/Aura-Interactor)
# aura-interactor
Inspired (read: scared) by the idea of the repetative task of implementing interactor (Use Case in Clean Architecture) wappers for the purpuse of background execution, I wanted to learn a bit more about code generation and annotation processing in Java so i set out to do this project.

## Usage
Annotate your interactor with `@AuraInteractor`
```java
@AuraInteractor
public interface LoginInteractor {

  void login(LoginCredentials input, LoginOutput loginOutput);

  void guestLogin(LoginOutput loginOutput);
}
```

This will generate:
```java
public final class AuraInteractor_LoginInteractor implements LoginInteractor {
  private final LoginInteractor interactor;

  private final AuraExecutor executor;

  AuraInteractor_LoginInteractor(LoginInteractor interactor, AuraExecutor executor) {
    this.interactor = interactor;
    this.executor = executor;
  }

  @Override
  public final void login(final LoginCredentials input, final LoginOutput loginOutput) {
    executor.runBackground(new Runnable() {
      @Override
      public void run() {
        final AuraOutput_LoginOutput auraOutput = new AuraOutput_LoginOutput(loginOutput, executor);
        interactor.login(input, auraOutput);
      }
    });
  }

  @Override
  public final void guestLogin(final LoginOutput loginOutput) {
    executor.runBackground(new Runnable() {
      @Override
      public void run() {
        final AuraOutput_LoginOutput auraOutput = new AuraOutput_LoginOutput(loginOutput, executor);
        interactor.guestLogin(auraOutput);
      }
    });
  }
}
```

Annotate the output(s) of the interactor with `@AuraOutput`. To invoke output methods on background annotete them with `@AuraOff`
```java
@AuraOutput
public interface LoginOutput {

  void successfulLogin(String message);

  void failedLogin(Exception e);

  @AuraOff
  void loginProgress(double progress);
}
```

This will generate:
```java
public final class AuraOutput_LoginOutput implements LoginOutput {
  private final LoginOutput output;

  private final AuraExecutor executor;

  AuraOutput_LoginOutput(LoginOutput output, AuraExecutor executor) {
    this.output = output;
    this.executor = executor;
  }

  @Override
  public final void successfulLogin(final String message) {
    executor.runForeground(new Runnable() {
      @Override
      public void run() {
        output.successfulLogin(message);
      }
    });
  }

  @Override
  public final void failedLogin(final Exception e) {
    executor.runForeground(new Runnable() {
      @Override
      public void run() {
        output.failedLogin(e);
      }
    });
  }

  @Override
  public final void loginProgress(final double progress) {
    output.loginProgress(progress);
  }
}
```

To use the generated interactors
```java
private static AuraExecutor provideAuraExecutor() {
  // return your executor however you defined it
}

public static LoginInteractor provideLoginInteractor() {
  LoginInteractor loginInteractor = new LoginInteractorImpl();
  return new AuraInteractor_LoginInteractor(loginInteractor, provideAuraExecutor());
}
```
```java
loginInteractor = DependencyProvider.provideLoginInteractor();
loginInteractor.login(loginCredentials, loginOuput);
```

Look at the full sample in 'app' module.

## Download
No stable release available yet.
