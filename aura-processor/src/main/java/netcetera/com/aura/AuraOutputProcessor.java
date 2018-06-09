package netcetera.com.aura;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

/**
 * Processor responsible for generating Aura output classes.
 */
public class AuraOutputProcessor extends AbstractProcessor {
  private Messager messager;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnvironment) {
    super.init(processingEnvironment);
    messager = processingEnvironment.getMessager();
  }

  @Override
  public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
    Set<? extends Element> allOutputs = roundEnvironment.getElementsAnnotatedWith(AuraOutput.class);
    Map<TypeElement, Set<ExecutableElement>> auraTargets = filterTargets(allOutputs);
    if (auraTargets.isEmpty()) {
      return true;
    }

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
    }

    return false;
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
        boolean isMethod = enclosedElement.getKind() == ElementKind.METHOD;
        boolean isAuraOff = enclosedElement.getAnnotation(AuraOff.class) != null;
        if (isMethod && !isAuraOff) {
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
    final String methodName = method.getSimpleName().toString();
    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName)
      .addAnnotation(Override.class);

    for (VariableElement variableElement : method.getParameters()) {
      methodBuilder.addParameter(ParameterSpec.get(variableElement));
    }

    final String outputInvocationTemplate;
    if(method.getAnnotation(AuraOff.class) == null) {
      outputInvocationTemplate = "output.$L($L)";
    } else {
      outputInvocationTemplate = "$L($L)";
    }

    methodBuilder.addStatement(
      CodeBlock.builder()
        .add("executor.runForeground(new $T() {", Runnable.class)
        .add("$T", Override.class)
        .add("public void run() {")
        .addStatement(outputInvocationTemplate, methodName, generateParamsForOutputInvocation(method))
        .add("}") // Runnable.run close
        .add("})") // Runnable close, runForeground close
        .build());

     return methodBuilder.build();
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
}
