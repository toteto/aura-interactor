package com.antonioivanovski.aura;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

public class AuraInteractorProcessor extends AbstractProcessor {

  private Types typeUtils;
  private Elements elementUtils;
  private Messager messager;
  private Filer filer;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnvironment) {
    super.init(processingEnvironment);
    typeUtils = processingEnvironment.getTypeUtils();
    elementUtils = processingEnvironment.getElementUtils();
    messager = processingEnvironment.getMessager();
    filer = processingEnvironment.getFiler();
  }

  @Override
  public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
    Set<? extends Element> allTargets = roundEnvironment.getElementsAnnotatedWith(AuraInteractor.class);
    Set<TypeElement> auraTargets = new LinkedHashSet<>();
    for (Element element : allTargets) {
      if (element.getKind() != ElementKind.INTERFACE) {
        messager.printMessage(Diagnostic.Kind.NOTE,
          AuraInteractor.class.getSimpleName() + " can only be used on interfaces. Will not generate for [" + element
            .getSimpleName() + "]");
      }
      auraTargets.add(((TypeElement) element));
    }

    for (TypeElement auraInterface : auraTargets) {
      String genName = AuraInteractor.class.getSimpleName() + "_" + auraInterface.getSimpleName();
      ClassName interfaceClass = ClassName.get(auraInterface);

      TypeSpec.Builder classBuilder = TypeSpec.classBuilder(genName)
        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        .addSuperinterface(interfaceClass)
        .addField(TypeName.get(auraInterface.asType()), "interactor", Modifier.PRIVATE, Modifier.FINAL)
        .addField(AuraExecutor.class, "executor", Modifier.PRIVATE, Modifier.FINAL)
        .addMethod(createConstructor(auraInterface));

      for (Element element : auraInterface.getEnclosedElements()) {
        if (element.getKind() == ElementKind.METHOD) {
          classBuilder.addMethod(createAuraMethod(((ExecutableElement) element)));
        }
      }

      try {
        JavaFile.builder(interfaceClass.packageName(), classBuilder.build()).build().writeTo(filer);
      } catch (IOException e) {
        messager.printMessage(Diagnostic.Kind.ERROR,
          "Failed to write generated class [" + genName + "]." + e.getMessage());
      }
    }
    return true;
  }

  private MethodSpec createConstructor(TypeElement auraInterface) {
    return MethodSpec.constructorBuilder()
      .addParameter(TypeName.get(auraInterface.asType()), "interactor")
      .addParameter(AuraExecutor.class, "executor")
      .addStatement("this.$N = $N", "interactor", "interactor")
      .addStatement("this.$N = $N", "executor", "executor")
      .build();
  }

  private MethodSpec createAuraMethod(ExecutableElement method) {
    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(method.getSimpleName().toString())
      .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
      .addAnnotation(Override.class);

    for (VariableElement variableElement : method.getParameters()) {
      methodBuilder.addParameter(ParameterSpec.get(variableElement).toBuilder().addModifiers(Modifier.FINAL).build());
    }

    methodBuilder.addStatement("executor.runBackground($L)", generateInteractorInvocationStatement(method));

    return methodBuilder.build();
  }

  private TypeSpec generateInteractorInvocationStatement(ExecutableElement method) {
    List<? extends VariableElement> parameters = method.getParameters();
    VariableElement outputParam = parameters.get(parameters.size() - 1);
    Element outputElement = typeUtils.asElement(outputParam.asType());
    String outputPackage = elementUtils.getPackageOf(outputElement).getQualifiedName().toString();
    ClassName outputClass = ClassName.get(outputPackage,
      AuraOutput.class.getSimpleName() + "_" + outputElement.getSimpleName());


    String paramsForOutputInvocation = generateParamsForOutputInvocation(method).replace(outputParam.getSimpleName(),
      "auraOutput");
    messager.printMessage(Diagnostic.Kind.NOTE, paramsForOutputInvocation);

    return TypeSpec.anonymousClassBuilder("")
      .addSuperinterface(Runnable.class)
      .addMethod(MethodSpec.methodBuilder("run")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .returns(TypeName.VOID)
        .addStatement("final $T auraOutput = new $T($N, executor)", outputClass, outputClass, outputParam.getSimpleName())
        .addStatement("interactor.$L($L)", method.getSimpleName().toString(), paramsForOutputInvocation)
        .build())
      .build();
  }

  private String generateParamsForOutputInvocation(ExecutableElement method) {
    List<? extends VariableElement> parameters = method.getParameters();
    if (parameters.isEmpty()) {
      return "";
    }

    StringBuilder stringBuilder = new StringBuilder();
    for (VariableElement variableElement : parameters) {
      stringBuilder.append(variableElement);
      stringBuilder.append(", ");
    }

    String result = stringBuilder.toString();
    return result.substring(0, result.length() - 2);
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Collections.singleton(AuraInteractor.class.getCanonicalName());
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return super.getSupportedSourceVersion();
  }
}
