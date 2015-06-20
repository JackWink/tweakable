package com.jackwink.tweakable.annotations.processor;

import com.jackwink.tweakable.annotations.TwkBoolean;
import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceException;
import com.jackwink.tweakable.types.TweakableBoolean;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.SupportedSourceVersion;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;


/**
 * Created by jackwink on 6/20/15.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class TweakableValueAnnotationProcessor extends AbstractProcessor {

    @Override public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(TwkBoolean.class.getCanonicalName());
        return types;
    }

    @Override public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {

        for (Element element : env.getElementsAnnotatedWith(TwkBoolean.class)) {
            if (element.getKind() != ElementKind.FIELD) {
                throw new FailedToBuildPreferenceException("Not a field.");
            }

            TypeMirror type = element.asType();
            if (type.getKind() == TypeKind.BOOLEAN) {
                print("Element " + element + " is boolean!");
                Element clazz = element.getEnclosingElement();
                TweakableBoolean bool = TweakableBoolean.parse(clazz.getSimpleName().toString(),
                        element.getSimpleName().toString(),
                        element.getAnnotation(TwkBoolean.class));

                print("Parsed: " + bool.getCategory());
                print("Parsed: " + bool.getTitle());
            }

        }
        /*
        Map<TypeElement, BindingClass> targetClassMap = findAndParseTargets(env);

        for (Map.Entry<TypeElement, BindingClass> entry : targetClassMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            BindingClass bindingClass = entry.getValue();

            try {
                JavaFileObject jfo = filer.createSourceFile(bindingClass.getFqcn(), typeElement);
                Write writer = jfo.openWriter();
                writer.write(bindingClass.brewJava());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                error(typeElement, "Unable to write view binder for type %s: %s", typeElement,
                        e.getMessage());
            }
        }*/
        print("Elements: " + elements.size());

        return true;
    }

    private void print(String line) {
        System.out.print(line + "\n");
    }
}
