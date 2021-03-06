package com.jackwink.tweakable.annotations.processor;

import com.jackwink.tweakable.annotations.TwkBoolean;
import com.jackwink.tweakable.annotations.TwkInteger;
import com.jackwink.tweakable.annotations.TwkString;
import com.jackwink.tweakable.annotations.TwkFloat;
import com.jackwink.tweakable.annotations.TwkAction;
import com.jackwink.tweakable.annotations.processor.generators.PreferencesGenerator;
import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceException;
import com.jackwink.tweakable.types.AbstractTweakableValue;
import com.jackwink.tweakable.types.TweakableAction;
import com.jackwink.tweakable.types.TweakableBoolean;
import com.jackwink.tweakable.types.TweakableFloat;
import com.jackwink.tweakable.types.TweakableInteger;
import com.jackwink.tweakable.types.TweakableString;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import com.google.auto.service.AutoService;

/**
 * Processes 'Twk' annotations to generate a class containing the app preferences
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@AutoService(Processor.class)
public class TweakableValueAnnotationProcessor extends AbstractProcessor {
    private static final String TAG = TweakableValueAnnotationProcessor.class.getSimpleName();

    private PreferencesGenerator mPreferenceGenerator;
    private boolean built = false;

    /**
     * When adding a type to this set, make sure you add a handler to
     * {@link #process(Class, RoundEnvironment)}. Still a WIP to clean that up.
     */
    private static final Set<Class<? extends Annotation>> mAnnotationSet = new LinkedHashSet<>(5);
    static {
        mAnnotationSet.add(TwkBoolean.class);
        mAnnotationSet.add(TwkString.class);
        mAnnotationSet.add(TwkInteger.class);
        mAnnotationSet.add(TwkFloat.class);
        mAnnotationSet.add(TwkAction.class);
    }

    @Override public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<String>();
        for (Class type : mAnnotationSet) {
            types.add(type.getCanonicalName());
        }
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

        for (Class type : mAnnotationSet) {
            process(type, env);
        }

        mPreferenceGenerator.build();
        built = true;
        return true;
    }

    private void process(Class annotationType, RoundEnvironment env) {
        for (Element element : env.getElementsAnnotatedWith((Class<Annotation>) annotationType)) {
            if (!element.getKind().isField() && !annotationType.equals(TwkAction.class)) {
                throw new FailedToBuildPreferenceException(
                        "Only fields can be annotated with @" + annotationType.getName());
            }

            TypeElement enclosingClass = findEnclosingTypeElement(element);
            TypeMirror type = element.asType();

            AbstractTweakableValue value = null;
            if (annotationType.equals(TwkBoolean.class)) {
                value = TweakableBoolean.parse(enclosingClass.getQualifiedName().toString(),
                        element.getSimpleName().toString(),
                        element.getAnnotation(TwkBoolean.class));
            } else if (annotationType.equals(TwkString.class)) {
                value = TweakableString.parse(enclosingClass.getQualifiedName().toString(),
                        element.getSimpleName().toString(),
                        element.getAnnotation(TwkString.class));
            } else if (annotationType.equals(TwkInteger.class)) {
                value = TweakableInteger.parse(enclosingClass.getQualifiedName().toString(),
                        element.getSimpleName().toString(),
                        element.getAnnotation(TwkInteger.class));
            } else if (annotationType.equals(TwkFloat.class)) {
                value = TweakableFloat.parse(enclosingClass.getQualifiedName().toString(),
                        element.getSimpleName().toString(),
                        element.getAnnotation(TwkFloat.class));
            } else if (annotationType.equals(TwkAction.class)) {
                value = TweakableAction.parse(enclosingClass.getQualifiedName().toString(),
                        element.getSimpleName().toString(),
                        element.getAnnotation(TwkAction.class));
            } else {
                throw new FailedToBuildPreferenceException(
                        "Unsupported type: " + type.toString());
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
