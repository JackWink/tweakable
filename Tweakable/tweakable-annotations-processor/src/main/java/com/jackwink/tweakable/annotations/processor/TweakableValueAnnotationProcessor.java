package com.jackwink.tweakable.annotations.processor;

import com.jackwink.tweakable.annotations.TwkBoolean;
import com.jackwink.tweakable.annotations.processor.generators.PreferencesGenerator;
import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceException;
import com.jackwink.tweakable.types.AbstractTweakableValue;
import com.jackwink.tweakable.types.TweakableBoolean;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.SupportedSourceVersion;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;


/**
 * Created by jackwink on 6/20/15.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class TweakableValueAnnotationProcessor extends AbstractProcessor {
    private static final String TAG = TweakableValueAnnotationProcessor.class.getSimpleName();

    private PreferencesGenerator mPreferenceGenerator;
    private boolean built = false;

    @Override public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(TwkBoolean.class.getCanonicalName());
        return types;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mPreferenceGenerator = new PreferencesGenerator(processingEnv.getFiler());

    }

    @Override public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {
        if (built) {
            return true;
        }

        for (Element element : env.getElementsAnnotatedWith(TwkBoolean.class)) {
            if (element.getKind() != ElementKind.FIELD) {
                throw new FailedToBuildPreferenceException(
                        "Only fields can be annotated with @TwkBoolean");
            }

            TypeMirror type = element.asType();
            AbstractTweakableValue value = null;
            if (type.getKind() == TypeKind.BOOLEAN) {
                Element clazz = element.getEnclosingElement();
                value = TweakableBoolean.parse(clazz.getSimpleName().toString(),
                        element.getSimpleName().toString(),
                        element.getAnnotation(TwkBoolean.class));
            }

            if (!mPreferenceGenerator.hasScreen(value.getScreen())) {
                mPreferenceGenerator.addScreen(value.getScreen());
            }

            if (!mPreferenceGenerator.hasCategory(value.getCategory(), value.getScreen())) {
                mPreferenceGenerator.addCategory(value.getCategory(), value.getScreen());
            }

            mPreferenceGenerator.addTweakableValue(value);
        }

        mPreferenceGenerator.build();
        built = true;
        return true;
    }

}
