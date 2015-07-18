package com.jackwink.tweakable.annotations.processor.generators;

import com.jackwink.tweakable.PreferenceAnnotationProcessor;
import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceCategoryException;
import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceException;
import com.jackwink.tweakable.exceptions.FailedToBuildPreferenceScreenException;
import com.jackwink.tweakable.types.AbstractTweakableValue;
import com.jackwink.tweakable.types.TweakableBoolean;
import com.jackwink.tweakable.types.TweakableFloat;
import com.jackwink.tweakable.types.TweakableInteger;
import com.jackwink.tweakable.types.TweakableString;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;


import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import javax.lang.model.element.Modifier;
import javax.annotation.processing.Filer;


/**
 *  Creates a java class used to generate preferences
 */
public class PreferencesGenerator {
    private static TypeName INTERFACE_BUNDLE_TYPE = ParameterizedTypeName.get(
            ClassName.get("java.util", "Map"),
            ClassName.get("java.lang", "String"),
            ClassName.get("java.lang", "Object"));
    private static TypeName BUNDLE_TYPE = ParameterizedTypeName.get(
            ClassName.get("java.util", "LinkedHashMap"),
            ClassName.get("java.lang", "String"),
            ClassName.get("java.lang", "Object"));


    public static final String ROOT_SCREEN_KEY = "tweakable-values-root-screen";
    public static final String ROOT_CATEGORY_KEY = "tweakable-values-root-category";

    public static final String SCREEN_KEY_POSTFIX = "-screen";
    public static final String CATEGORY_KEY_POSTFIX = "-category";

    // Total number of tweakable values, just so it doesn't get senseless.
    private final static int MAX_TWEAKABLE_VALUES = 64;
    private final static int MAX_TWEAKABLE_CATEGORIES = 16;
    private final static int MAX_TWEAKABLE_SCREENS = 16;

    // List of preferences
    private static LinkedHashSet<AbstractTweakableValue> mPreferences =
            new LinkedHashSet<AbstractTweakableValue>(MAX_TWEAKABLE_VALUES);

    private static LinkedHashMap<String, String> mCategories =
            new LinkedHashMap<String, String>(MAX_TWEAKABLE_CATEGORIES);

    private static LinkedHashMap<String, String> mScreens =
            new LinkedHashMap<String, String>(MAX_TWEAKABLE_SCREENS);

    private static final String mScreenSetName = "mScreens";
    private static final String mCategorySetName = "mCategories";
    private static final String mPreferenceSetName = "mPreferences";

    private int mScreenCount = 0;
    private int mCategoryCount = 0;
    private int mPreferenceCount = 0;
    private int mStrArrayCount = 0;

    private Filer mFiler;

    public PreferencesGenerator(Filer filer) {
        mFiler = filer;
    }

    public void addTweakableValue(AbstractTweakableValue value) {
        mPreferences.add(value);
    }

    public void addScreen(String title) {
        mScreens.put(getScreenKey(title), title);
    }

    public boolean hasScreen(String key) {
        return getScreenKey(key).equals(ROOT_SCREEN_KEY) || mScreens.containsKey(getScreenKey(key));
    }

    public boolean hasCategory(String categoryName, String screenName) {
        // Need to explicitly check since mCategories will NOT contain the root screen info
        return categoryName == null || categoryName.isEmpty() ||
                categoryName.equals(ROOT_CATEGORY_KEY) ||
                mCategories.containsKey(getCategoryKey(categoryName, screenName));
    }

    public void addCategory(String categoryName, String screenName) {
        mCategories.put(getCategoryKey(categoryName, screenName), categoryName);
    }

    public void build() {
        TypeSpec.Builder preferences = TypeSpec.classBuilder("GeneratedPreferences")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ClassName.get(PreferenceAnnotationProcessor.class))
                .addMethod(createDeclaredScreensMethod())
                .addMethod(createDeclaredCategoriesMethod())
                .addMethod(createDeclaredPreferencesMethod());

        JavaFile javaFile = JavaFile.builder("com.jackwink.tweakable", preferences.build())
                .build();
        try {
            javaFile.writeTo(mFiler);
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    public MethodSpec createDeclaredScreensMethod() {
        ClassName hashSet = ClassName.get("java.util", "LinkedHashSet");
        TypeName returnSet = ParameterizedTypeName.get(ClassName.get("java.util", "Collection"),
                INTERFACE_BUNDLE_TYPE);
        TypeName declaredSet = ParameterizedTypeName.get(hashSet, INTERFACE_BUNDLE_TYPE);

        MethodSpec.Builder builder = MethodSpec.methodBuilder("getDeclaredScreens")
                .addModifiers(Modifier.PUBLIC)
                .returns(returnSet)
                .addStatement("$T " + mScreenSetName + " = new $T<>()", declaredSet, hashSet);

        for (String key : mScreens.keySet()) {
            writeScreenBundle(builder, key);
        }
        return builder.addStatement("return " + mScreenSetName).build();
    }

    public MethodSpec createDeclaredCategoriesMethod() {
        ClassName hashSet = ClassName.get("java.util", "LinkedHashSet");
        TypeName returnSet = ParameterizedTypeName.get(ClassName.get("java.util", "Collection"),
                INTERFACE_BUNDLE_TYPE);
        TypeName declaredSet = ParameterizedTypeName.get(hashSet, INTERFACE_BUNDLE_TYPE);
        MethodSpec.Builder builder = MethodSpec.methodBuilder("getDeclaredCategories")
                .addModifiers(Modifier.PUBLIC)
                .returns(returnSet)
                .addStatement("$T " + mCategorySetName + " = new $T<>()", declaredSet, hashSet);
        for (String key : mCategories.keySet()) {
            writeCategoryBundle(builder, mCategories, key);
        }
        return builder.addStatement("return " + mCategorySetName).build();
    }


    public MethodSpec createDeclaredPreferencesMethod() {
        ClassName hashSet = ClassName.get("java.util", "LinkedHashSet");
        TypeName returnSet = ParameterizedTypeName.get(ClassName.get("java.util", "Collection"),
                INTERFACE_BUNDLE_TYPE);
        TypeName declaredSet = ParameterizedTypeName.get(hashSet, INTERFACE_BUNDLE_TYPE);
        MethodSpec.Builder builder = MethodSpec.methodBuilder("getDeclaredPreferences")
                .addModifiers(Modifier.PUBLIC)
                .returns(returnSet)
                .addStatement("$T " + mPreferenceSetName + " = new $T<>()", declaredSet, hashSet);
        for (AbstractTweakableValue value : mPreferences) {
            writePreferenceBundle(builder, value);
        }
        return builder.addStatement("return " + mPreferenceSetName).build();
    }

    public void writeScreenBundle(MethodSpec.Builder builder, String screenKey) {
        String screenName = mScreens.get(screenKey);
        String bundleName = "screenBundle" + mScreenCount;
        String addString = bundleName + ".put($S, $S)";

        builder.addStatement("$T " + bundleName + " = new $T()", INTERFACE_BUNDLE_TYPE, BUNDLE_TYPE)
                .addStatement(addString, AbstractTweakableValue.BUNDLE_KEYATTR_KEY, screenKey)
                .addStatement(addString, AbstractTweakableValue.BUNDLE_TITLE_KEY, screenName)
                .addStatement(mScreenSetName + ".add(" + bundleName + ")");

        ++mScreenCount;
        if (mScreenCount > MAX_TWEAKABLE_SCREENS) {
            throw new FailedToBuildPreferenceScreenException(
                    "Too many preference screens declared!");
        }
    }


    public void writeCategoryBundle(MethodSpec.Builder builder,
                                    LinkedHashMap<String, String> categories, String categoryKey) {
        String categoryName = categories.get(categoryKey);

        String bundleName = "categoryBundle" + mCategoryCount;
        String addString = bundleName + ".put($S, $S)";

        builder.addStatement("$T " + bundleName + " = new $T()", INTERFACE_BUNDLE_TYPE, BUNDLE_TYPE)
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

    public void writePreferenceBundle(MethodSpec.Builder builder, AbstractTweakableValue value) {

        String bundleName = "preferenceBundle" + mPreferenceCount;
        String addString = bundleName + ".put($S, $S)";

        builder.addStatement("$T " + bundleName + " = new $T()", INTERFACE_BUNDLE_TYPE, BUNDLE_TYPE)
                .addStatement(addString, AbstractTweakableValue.BUNDLE_KEYATTR_KEY, value.getKey())
                .addStatement(addString, AbstractTweakableValue.BUNDLE_TITLE_KEY, value.getTitle())
                .addStatement(addString, AbstractTweakableValue.BUNDLE_SUMMARY_KEY,
                        value.getSummary())
                .addStatement(addString, AbstractTweakableValue.BUNDLE_CATEGORY_KEY,
                        getCategoryKey(value.getCategory(), value.getScreen()))
                .addStatement(addString, AbstractTweakableValue.BUNDLE_SCREEN_KEY,
                        getScreenKey(value.getScreen()));

        if (value.getType().equals(Boolean.class)) {
            builder.addStatement(addString, TweakableBoolean.BUNDLE_OFF_LABEL_KEY,
                    ((TweakableBoolean) value).getOffLabel())
                    .addStatement(addString, TweakableBoolean.BUNDLE_ON_LABEL_KEY,
                            ((TweakableBoolean) value).getOnLabel())
                    .addStatement(addString, TweakableBoolean.BUNDLE_OFF_SUMMARY_KEY,
                            ((TweakableBoolean) value).getOffSummary())
                    .addStatement(addString, TweakableBoolean.BUNDLE_ON_SUMMARY_KEY,
                            ((TweakableBoolean) value).getOnSummary());
        } else if (value.getType().equals(String.class)) {
            addStringArrayToBundle(builder, bundleName, TweakableString.BUNDLE_OPTIONS_KEY,
                    ((TweakableString) value).getOptions());
        } else if (value.getType().equals(Integer.class)) {
            builder.addStatement(bundleName + ".put($S, $L)",
                    TweakableInteger.BUNDLE_MAX_VALUE_KEY, ((TweakableInteger) value).getMaxValue());
            builder.addStatement(bundleName + ".put($S, $L)",
                    TweakableInteger.BUNDLE_MIN_VALUE_KEY, ((TweakableInteger) value).getMinValue());
        } else if (value.getType().equals(Float.class)) {
            builder.addStatement(bundleName + ".put($S, $Lf)",
                    TweakableFloat.BUNDLE_MAX_VALUE_KEY, ((TweakableFloat) value).getMaxValue());
            builder.addStatement(bundleName + ".put($S, $Lf)",
                    TweakableFloat.BUNDLE_MIN_VALUE_KEY, ((TweakableFloat) value).getMinValue());
        }

        builder.addStatement(mPreferenceSetName + ".add(" + bundleName + ")");

        ++mPreferenceCount;
        if (mPreferenceCount > MAX_TWEAKABLE_VALUES) {
            throw new FailedToBuildPreferenceException("Too many declared preferences!");
        }
    }

    private void addStringArrayToBundle(MethodSpec.Builder builder, String bundleName, String key,
                                        String[] array) {
        StringBuilder b = new StringBuilder("{ ");
        for (int i = 0; i < array.length; ++i) {
            b.append('"' + array[i] + '"');
            if (i < array.length - 1) {
                b.append(", ");
            }
        }
        String arrayName = "strArray" + mStrArrayCount;
        builder.addStatement("String[] " + arrayName + " = " + b.append("}").toString());
        builder.addStatement(bundleName + ".put($S, " + arrayName + ")", key);
        ++mStrArrayCount;
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

}
