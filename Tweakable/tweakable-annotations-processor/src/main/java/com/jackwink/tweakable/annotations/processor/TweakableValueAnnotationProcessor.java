package com.jackwink.tweakable.annotations.processor;

import com.jackwink.tweakable.annotations.TwkBoolean;
import com.jackwink.tweakable.annotations.TwkString;
import com.jackwink.tweakable.annotations.processor.generators.PreferencesGenerator;
import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceException;
import com.jackwink.tweakable.types.AbstractTweakableValue;
import com.jackwink.tweakable.types.TweakableBoolean;
import com.jackwink.tweakable.types.TweakableString;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.SupportedSourceVersion;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * Processes TweakableBooleans (and soon more!) to generate a class containing the app preferences
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class TweakableValueAnnotationProcessor extends AbstractProcessor {
    private static final String TAG = TweakableValueAnnotationProcessor.class.getSimpleName();

    private PreferencesGenerator mPreferenceGenerator;
    private boolean built = false;

    @Override public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(TwkBoolean.class.getCanonicalName());
        types.add(TwkString.class.getCanonicalName());
        return types;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mPreferenceGenerator = new PreferencesGenerator(processingEnv.getFiler());
        Log.init(processingEnv.getMessager());
    }

    @Override public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {
        if (built) {
            return true;
        }

        process(TwkBoolean.class, env);
        process(TwkString.class, env);
        mPreferenceGenerator.build();
        built = true;
        return true;
    }

    private void process(Class annotationType, RoundEnvironment env) {
        for (Element element : env.getElementsAnnotatedWith((Class<Annotation>)annotationType)) {
            if (!element.getKind().isField()) {
                throw new FailedToBuildPreferenceException(
                        "Only fields can be annotated with @" + annotationType.getName());
            }

            TypeElement enclosingClass = findEnclosingTypeElement(element);
            TypeMirror type = element.asType();

            AbstractTweakableValue value = null;
            if (type.getKind() == TypeKind.BOOLEAN
                    || type.toString().equals(Boolean.class.getName())) {
                value = TweakableBoolean.parse(enclosingClass.getQualifiedName().toString(),
                        element.getSimpleName().toString(),
                        element.getAnnotation(TwkBoolean.class));
            } else if (type.toString().equals(String.class.getName())) {
                value = TweakableString.parse(enclosingClass.getQualifiedName().toString(),
                        element.getSimpleName().toString(),
                        element.getAnnotation(TwkString.class));
            } else {
                throw new FailedToBuildPreferenceException("Unsupported type: " +
                        type.toString());
            }

            if (!mPreferenceGenerator.hasScreen(value.getScreen())) {
                mPreferenceGenerator.addScreen(value.getScreen());
            }

            if (!mPreferenceGenerator.hasCategory(value.getCategory(), value.getScreen())) {
                mPreferenceGenerator.addCategory(value.getCategory(), value.getScreen());
            }

            mPreferenceGenerator.addTweakableValue(value);
        }
    }

    public static TypeElement findEnclosingTypeElement(Element e) {
        while (e != null && !(e instanceof TypeElement)) {
            e = e.getEnclosingElement();
        }
        return TypeElement.class.cast(e);
    }

}
