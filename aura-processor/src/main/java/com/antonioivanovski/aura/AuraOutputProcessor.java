package com.antonioivanovski.aura;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
import javax.tools.Diagnostic;

/**
 * Annotation processor for processing {@link AuraOutput}.
 */
public class AuraOutputProcessor extends AbstractProcessor {
  private Messager messager;
  private Filer filer;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnvironment) {
    super.init(processingEnvironment);
    messager = processingEnvironment.getMessager();
    filer = processingEnvironment.getFiler();
  }

  @Override
  public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
    // Filter
    Set<? extends Element> allOutputs = roundEnvironment.getElementsAnnotatedWith(AuraOutput.class);
    Map<TypeElement, Set<ExecutableElement>> auraTargets = filterTargets(allOutputs);
    if (auraTargets.isEmpty()) {
      return false;
    }

    // Generate classes
    for (TypeElement auraInterface : auraTargets.keySet()) {
      String genName = AuraOutput.class.getSimpleName() + "_" + auraInterface.getSimpleName();
      ClassName interfaceClass = ClassName.get(auraInterface);

      TypeSpec.Builder classBuilder = TypeSpec.classBuilder(genName)
        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        .addSuperinterface(interfaceClass)
        .addField(TypeName.get(auraInterface.asType()), "output", Modifier.PRIVATE, Modifier.FINAL)
        .addField(AuraExecutor.class, "executor", Modifier.PRIVATE, Modifier.FINAL)
        .addMethod(createConstructor(auraInterface));

      for (Element element : auraInterface.getEnclosedElements()) {
        if (element.getKind() == ElementKind.METHOD) {
          classBuilder.addMethod(createAuraMethod(((ExecutableElement) element)));
        }
      }

      try {
        TypeSpec generatedClass = classBuilder.build();
        JavaFile.builder(interfaceClass.packageName(), generatedClass).build().writeTo(filer);
      } catch (IOException e) {
        messager.printMessage(Diagnostic.Kind.ERROR,
          "Failed to write generated class [" + genName + "]." + e.getMessage());
      }
    }

    return true;
  }

  private Map<TypeElement, Set<ExecutableElement>> filterTargets(Set<? extends Element> allOutputs) {
    Map<TypeElement, Set<ExecutableElement>> auraTargets = new LinkedHashMap<>();
    for (Element element : allOutputs) {
      if (element.getKind() != ElementKind.INTERFACE) {
        messager.printMessage(Diagnostic.Kind.ERROR, "AuraOutput can only be applied to interfaces.");
        return Collections.emptyMap();
      }

      TypeElement auraInterface = (TypeElement) element;

      List<? extends Element> enclosedElements = auraInterface.getEnclosedElements();
      Set<ExecutableElement> auraMethods = new LinkedHashSet<>();
      for (Element enclosedElement : enclosedElements) {
        if (enclosedElement.getKind() == ElementKind.METHOD) {
          auraMethods.add(((ExecutableElement) enclosedElement));
        }
      }

      auraTargets.put(auraInterface, auraMethods);
    }
    return auraTargets;
  }

  private MethodSpec createConstructor(TypeElement auraInterface) {
    return MethodSpec.constructorBuilder()
      .addParameter(TypeName.get(auraInterface.asType()), "output")
      .addParameter(AuraExecutor.class, "executor")
      .addStatement("this.$N = $N", "output", "output")
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


    if (method.getAnnotation(AuraOff.class) == null) {
      methodBuilder.addStatement("executor.runForeground($L)", generateAuraOutputInvocation(method));
    } else {
      methodBuilder.addStatement("output.$L($L)", method.getSimpleName().toString(), generateParamsForOutputInvocation(method));
    }

    return methodBuilder.build();
  }

  private TypeSpec generateAuraOutputInvocation(ExecutableElement method) {

    return TypeSpec.anonymousClassBuilder("")
      .addSuperinterface(Runnable.class)
      .addMethod(MethodSpec.methodBuilder("run")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .returns(TypeName.VOID)
        .addStatement("output.$L($L)", method.getSimpleName().toString(), generateParamsForOutputInvocation(method))
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
    return Collections.singleton(AuraOutput.class.getCanonicalName());
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }
}
