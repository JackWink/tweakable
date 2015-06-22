package com.jackwink.tweakable.annotations.processor;

import android.os.Bundle;

import com.jackwink.tweakable.annotations.TwkBoolean;
import com.jackwink.tweakable.controls.TweaksFragment;
import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceCategoryException;
import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceException;
import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceScreenException;
import com.jackwink.tweakable.types.AbstractTweakableValue;
import com.jackwink.tweakable.types.TweakableBoolean;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
    private static final String TAG = TweakableValueAnnotationProcessor.class.getSimpleName();

    private final String classPackage = null;
    private Filer filer;

    public static final String ROOT_SCREEN_TITLE = "Tweakable Values";
    public static final String ROOT_SCREEN_KEY = "tweakable-values-root-screen";
    public static final String ROOT_CATEGORY_KEY = "tweakable-values-root-category";

    private static final String mScreenSetName = "mScreens";
    private static final String mCategorySetName = "mCategories";
    private static final String mPreferenceSetName = "mPreferences";


    public static final String SCREEN_KEY_POSTFIX = "-screen";
    public static final String CATEGORY_KEY_POSTFIX = "-category";


    private static TypeName BUNDLE_TYPE = ClassName.get("android.os", "Bundle");


    // Total number of tweakable values, just so it doesn't get senseless.
    private final static int MAX_TWEAKABLE_VALUES = 64;
    private final static int MAX_TWEAKABLE_CATEGORIES = 16;
    private final static int MAX_TWEAKABLE_SCREENS = 16;

    private int mScreenCount = 0;
    private int mCategoryCount = 0;
    private int mPreferenceCount = 0;

    // List of preferences
    private static LinkedHashSet<AbstractTweakableValue> mPreferences =
            new LinkedHashSet<>(MAX_TWEAKABLE_VALUES);

    private static LinkedHashMap<String, String> mCategories =
            new LinkedHashMap<>(MAX_TWEAKABLE_CATEGORIES);

    private static LinkedHashMap<String, String> mScreens =
            new LinkedHashMap<>(MAX_TWEAKABLE_SCREENS);

    @Override public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(TwkBoolean.class.getCanonicalName());
        return types;
    }


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
    }

    @Override public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {

        for (Element element : env.getElementsAnnotatedWith(TwkBoolean.class)) {
            if (element.getKind() != ElementKind.FIELD) {
                throw new FailedToBuildPreferenceException("Not a field.");
            }

            TypeMirror type = element.asType();
            AbstractTweakableValue value = null;
            if (type.getKind() == TypeKind.BOOLEAN) {
                print("Element " + element + " is boolean!");
                Element clazz = element.getEnclosingElement();
                value = TweakableBoolean.parse(clazz.getSimpleName().toString(),
                        element.getSimpleName().toString(),
                        element.getAnnotation(TwkBoolean.class));
            }

            if (!isScreenCreated(value)) {
                createScreenBundle(value);
            }


            if (!isCategoryCreated(value)) {
                createCategoryBundle(value);
            }

            mPreferences.add(value);
            print("created preference: " + value.getTitle());
        }

        print("Done processing..");
        generatePreferences();


        return true;
    }

    public void generatePreferences() {

        TypeSpec.Builder preferences = TypeSpec.classBuilder("GeneratedPreferences")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ClassName.get(TweaksFragment.PreferenceAnnotationProcessor.class))
                .addMethod(writeScreenMethod())
                .addMethod(writeCategoryMethod())
                .addMethod(writePreferenceMethod());

        JavaFile javaFile = JavaFile.builder("com.jackwink.tweakable", preferences.build())
                .build();
        try {
            javaFile.writeTo(filer);
            //javaFile.writeTo(System.out);
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    public MethodSpec writeScreenMethod() {
        ClassName hashSet = ClassName.get("java.util", "LinkedHashSet");
        TypeName returnSet = ParameterizedTypeName.get(ClassName.get("java.util", "Collection"),
                BUNDLE_TYPE);
        TypeName declaredSet = ParameterizedTypeName.get(hashSet, BUNDLE_TYPE);

        MethodSpec.Builder builder = MethodSpec.methodBuilder("getDeclaredScreens")
                .addModifiers(Modifier.PUBLIC)
                .returns(returnSet)
                .addStatement("$T " + mScreenSetName + " = new $T<>()", declaredSet, hashSet);

        for (String key : mScreens.keySet()) {
            writeScreenBundle(builder, key);
        }

        return builder.addStatement("return " + mScreenSetName).build();
    }

    public void writeScreenBundle(MethodSpec.Builder builder, String screenKey) {
        String screenName = mScreens.get(screenKey);
        String bundleName = "screenBundle" + mScreenCount;
        String addString = bundleName + ".putString($S, $S)";

        builder.addStatement("$T " + bundleName + " = new $T()", BUNDLE_TYPE, BUNDLE_TYPE)
                .addStatement(addString, AbstractTweakableValue.BUNDLE_KEYATTR_KEY, screenKey)
                .addStatement(addString, AbstractTweakableValue.BUNDLE_TITLE_KEY, screenName)
                .addStatement(mScreenSetName + ".add(" + bundleName + ")");

        ++mScreenCount;
        if (mScreenCount > MAX_TWEAKABLE_SCREENS) {
            throw new FailedToBuildPreferenceScreenException(
                    "Too many preference screens declared!");
        }
    }

    private boolean isCategoryCreated(AbstractTweakableValue value) {
        if (value.getCategory() == null || value.getCategory().isEmpty()) {
            return true;
        }
        return mCategories.containsKey(getCategoryKey(value.getCategory(), value.getScreen()));
    }

    private void createCategoryBundle(AbstractTweakableValue value) {
        String key = getCategoryKey(value.getCategory(), value.getScreen());
        print("created category: " + value.getCategory() + " (" + key + ")");
        mCategories.put(key, value.getCategory());
    }

    private String getCategoryKey(String categoryName, String screenName) {
        String screenKey = getScreenKey(screenName);
        if (categoryName == null || categoryName.isEmpty() || categoryName.equals(ROOT_CATEGORY_KEY)) {
            return screenKey + "." + ROOT_CATEGORY_KEY;
        } else {
            return screenKey + "." + normalize(categoryName) + CATEGORY_KEY_POSTFIX;
        }
    }

    private String getScreenKey(String screenName) {
        if (screenName == null || screenName.isEmpty() || screenName.equals(ROOT_SCREEN_KEY)) {
            return ROOT_SCREEN_KEY;
        }
        return normalize(screenName) + SCREEN_KEY_POSTFIX;
    }

    private String normalize(String str) {
        return str.replace(' ', '-').replace('.', '-').toLowerCase();
    }

    private boolean isScreenCreated(AbstractTweakableValue value) {
        if (value.getScreen() == null || value.getScreen().isEmpty()) {
            return true;
        }
        return mScreens.containsKey(getScreenKey(value.getScreen()));
    }

    private void createScreenBundle(AbstractTweakableValue value) {
        String screenKey = getScreenKey(value.getScreen());
        print("created screen: " + value.getScreen() + " (" + screenKey + ")");
        mScreens.put(screenKey, value.getScreen());
    }

    public MethodSpec writeCategoryMethod() {
        ClassName hashSet = ClassName.get("java.util", "LinkedHashSet");
        TypeName returnSet = ParameterizedTypeName.get(ClassName.get("java.util", "Collection"),
                BUNDLE_TYPE);
        TypeName declaredSet = ParameterizedTypeName.get(hashSet, BUNDLE_TYPE);
        MethodSpec.Builder builder = MethodSpec.methodBuilder("getDeclaredCategories")
                .addModifiers(Modifier.PUBLIC)
                .returns(returnSet)
                .addStatement("$T " + mCategorySetName + " = new $T<>()", declaredSet, hashSet);
        for (String key : mCategories.keySet()) {
            writeCategoryBundle(builder, key);
        }
        return builder.addStatement("return " + mCategorySetName).build();
    }

    public void writeCategoryBundle(MethodSpec.Builder builder, String categoryKey) {
        String categoryName = mCategories.get(categoryKey);

        String bundleName = "categoryBundle" + mCategoryCount;
        String addString = bundleName + ".putString($S, $S)";

        builder.addStatement("$T " + bundleName + " = new $T()", BUNDLE_TYPE, BUNDLE_TYPE)
                .addStatement(addString, AbstractTweakableValue.BUNDLE_KEYATTR_KEY, categoryKey)
                .addStatement(addString, AbstractTweakableValue.BUNDLE_TITLE_KEY, categoryName)
                .addStatement(addString, AbstractTweakableValue.BUNDLE_SCREEN_KEY,
                        categoryKey.split("\\.")[0])
                .addStatement(mCategorySetName + ".add(" + bundleName + ")");

        ++mCategoryCount;
        if (mCategoryCount > MAX_TWEAKABLE_CATEGORIES) {
            throw new FailedToBuildPreferenceCategoryException(
                    "Too many declared preference categories!");
        }
    }


    public MethodSpec writePreferenceMethod() {
        ClassName hashSet = ClassName.get("java.util", "LinkedHashSet");
        TypeName returnSet = ParameterizedTypeName.get(ClassName.get("java.util", "Collection"),
                BUNDLE_TYPE);
        TypeName declaredSet = ParameterizedTypeName.get(hashSet, BUNDLE_TYPE);
        MethodSpec.Builder builder = MethodSpec.methodBuilder("getDeclaredPreferences")
                .addModifiers(Modifier.PUBLIC)
                .returns(returnSet)
                .addStatement("$T " + mPreferenceSetName + " = new $T<>()", declaredSet, hashSet);
        for (AbstractTweakableValue value : mPreferences) {
            writePreferenceBundle(builder, value);
        }
        return builder.addStatement("return " + mPreferenceSetName).build();
    }

    public void writePreferenceBundle(MethodSpec.Builder builder, AbstractTweakableValue value) {

        String bundleName = "preferenceBundle" + mPreferenceCount;
        String addString = bundleName + ".putString($S, $S)";

        builder.addStatement("$T " + bundleName + " = new $T()", BUNDLE_TYPE, BUNDLE_TYPE)
                .addStatement(addString, AbstractTweakableValue.BUNDLE_KEYATTR_KEY, value.getKey())
                .addStatement(addString, AbstractTweakableValue.BUNDLE_TITLE_KEY, value.getTitle())
                .addStatement(addString, AbstractTweakableValue.BUNDLE_SUMMARY_KEY,
                        value.getSummary())
                .addStatement(addString, AbstractTweakableValue.BUNDLE_CATEGORY_KEY,
                        getCategoryKey(value.getCategory(), value.getScreen()))

                .addStatement(addString, AbstractTweakableValue.BUNDLE_SCREEN_KEY,
                        getScreenKey(value.getScreen()))
                .addStatement(addString, AbstractTweakableValue.BUNDLE_TYPEINFO_KEY,
                        value.getType().getName());

        if (value.getType().equals(boolean.class)) {
            builder.addStatement(addString, TweakableBoolean.BUNDLE_OFF_LABEL_KEY,
                    ((TweakableBoolean) value).getOffLabel())
                    .addStatement(addString, TweakableBoolean.BUNDLE_ON_LABEL_KEY,
                            ((TweakableBoolean) value).getOnLabel())
                    .addStatement(addString, TweakableBoolean.BUNDLE_OFF_SUMMARY_KEY,
                            ((TweakableBoolean) value).getOffSummary())
                    .addStatement(addString, TweakableBoolean.BUNDLE_ON_SUMMARY_KEY,
                            ((TweakableBoolean) value).getOnSummary())
                    .addStatement(bundleName + ".putBoolean($S, $L)",
                            TweakableBoolean.BUNDLE_DEFAULT_VALUE_KEY,
                            ((TweakableBoolean) value).getDefaultValue());
        }

        builder.addStatement(mPreferenceSetName + ".add(" + bundleName + ")");

        ++mPreferenceCount;
        if (mPreferenceCount > MAX_TWEAKABLE_VALUES) {
            throw new FailedToBuildPreferenceException("Too many declared preferences!");
        }
    }

    private void print(String line) {
        System.out.print(line + "\n");
    }
}
