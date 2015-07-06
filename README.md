#Tweakable
[![Build Status](https://travis-ci.org/JackWink/tweakable.svg?branch=master)](https://travis-ci.org/JackWink/tweakable)

Feature flags, tweakable values, and actions for Android.

See `Tweakable/tweakabledemo` for a full usage example, below is a quick usage guide:

## Known Issues / Untested cases

* Untested: Using with other preference resources defined in app 

## Usage

Here's a sample gradle file including the project locally:

#### Gradle

    buildscript {
        repositories {
            jcenter()
        }
        dependencies {
            classpath 'com.android.tools.build:gradle:1.2.3'
            classpath 'com.neenbedankt.gradle.plugins:android-apt:1.4'
        }
    }

    allprojects {
        repositories {
            jcenter()
        }
    }
    
    apply plugin: 'com.android.application'
    apply plugin: 'android-apt'

    android {
        compileSdkVersion 22
        buildToolsVersion "22.0.1"

        defaultConfig {
            applicationId "com.jackwink.tweakabledemo"
            minSdkVersion 15
            targetSdkVersion 22
        }
    }

    dependencies {
        compile fileTree(dir: 'libs', include: ['*.jar'])
        compile 'com.android.support:appcompat-v7:22.2.0'

        compile project(':tweakable-annotations')
        apt project(':tweakable-annotations-processor')
    }

#### Setup

Call `Tweakable.init(Context context)` when the app starts to inject the present values (or 
default values) from shared preferences to static fields. Tweakable will start
listening for the phone to shake and show a preferences screen to the user.

#### Field Annotations - Tweakable Values

Once in your Android app, you can annotate any public static boolean, string, integer or float field:

    @TwkBoolean(screen = "Subscreen 1", category = "Category 1", 
                title = "Feature 1", onSummary = "Feature 1 Enabled!", 
                offSummary = "Feature 1 Disabled")
    public static boolean defaultsTo = true;

    @TwkString(category = "Category 1", title = "Selectable String",
                options = {"Pick me!", "Or me!", "No, me!"})
    public static String selectableString = "Pick me!";
    
    @TwkString(category = "Category 1", title = "Editable String")
    public static String editableString = "Edit me!";
    
    @TwkInteger(category = "Integer Category", title = "Pick a number", 
                minValue = 0, maxValue = 100)
    public static int someInteger = 50;

    @TwkFloat(category = "Float Category", title = "Slide me", 
                minValue = 0f, maxValue = 10f)
    public static float someFloat = 2.5f;

These values will be updated any time the user changes them in shared preferences. The saved value from shared preferences
will be restored any time `Tweakable.init(Context)` is called... however this function should only be called once in the 
app's lifecycle.

#### Method Annotations - Actions

Actions are supported! Annotate any public static method with `TwkAction` and the user will be able to 
call that method from the tweak menu.

    @TwkAction(category = "Actions", title = "Load seed data")
    public static void loadSeedData() {
        // stub: Load data
        // assuming App.getContext is a static reference to a Context
        Toast.makeText(App.getContext(), "Seed data loaded!", Toast.LENGTH_LONG).show();
    }

Actions support the `screen`, `category`, and `title` attributes.

## Thanks / Attributions

* [Tweaks](https://github.com/facebook/Tweaks) for the initial inspiration. 
* [Number picker](https://github.com/vanniktech/VNTNumberPickerPreference) taken and modified, thanks Vanniktech/Niklas Baudy!
* [Seismic](https://github.com/square/seismic) for shake detection
