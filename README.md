[![Build Status](https://travis-ci.org/toteto/aura-interactor.svg?branch=master)](https://travis-ci.org/toteto/aura-interactor)
[![Bintray](https://img.shields.io/bintray/v/toteto/maven/Aura-Interactor.svg)](https://bintray.com/toteto/maven/Aura-Interactor)
# aura-interactor
Inspired (read: scared) by the idea of the repetative task of implementing interactor (Use Case in Clean Architecture) wappers for the purpuse of background execution, I wanted to learn a bit more about code generation and annotation processing in Java so i set out to do this project.

## Usage
Annotate your interactor with `@AuraInteractor`
```java
@AuraInteractor
public interface MyInteractor {

  void interact1(MyInput input, MyOutput output);

  void anyInteractName(AnyAnnotatedAuraOutput output);
  
  // void interact3(MyOutput output);
  // ...
}
```

Annotate the output(s) of the interactor with `@AuraOutput`. To invoke output methods on background annotete them with `@AuraOff`
```java
@AuraOutput
public interface MyOutput {

  void myMethod(String message);

  @AuraOff
  void backgroundMyMethod(String message);

}
```

To use the generated interactors
```java
private static AuraExecutor provideAuraExecutor() {
  // return your executor however you defined it
}

public static MyInteractor provideMyInteractor() {
  MyInteractor myInteractor = new MyInteractorImpl(); // your original interactor
  return new AuraInteractor_MyInteractor(myInteractor, provideAuraExecutor());
}
```
```java
MyInteractor interactor = Dependencies.provideMyInteractor();
interactor.interact1(input, output);
```


Look at the full sample in 'app' module.

## Download
No stable release available yet.
