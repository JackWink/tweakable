#Tweakable
[![Build Status](https://travis-ci.org/JackWink/tweakable.svg?branch=master)](https://travis-ci.org/JackWink/tweakable)

Feature flags and tweakable values for Android.

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

Call `Tweakable.init(Context context)` when the app starts and then shake your phone to bring up the
tweakable value menu.

When you call init, all the default values (or present values) will be injected statically, so
you can access them whenever. For example, if the above annotations were in a class called `Settings`,
accessing `Settings.selectableString` would contain "Pick me!" or the current value stored in 
shared preferences.

## Roadmap

v0.0.1 - POC

* ~~Generate preference views from bundle~~
* ~~Make root preferences + categories come before sub-screens~~
* ~~Parse annotations to generate bundle format for preference generation~~
* ~~Clean up bundle format~~
* ~~Allow sub-screens~~
* ~~Generate preferences at compile time~~
* ~~Inject boolean & Boolean values from shared preferences~~
* ~~Inject Strings~~
* ~~Inject int & Integer types~~
* ~~Inject float types~~
* Inject double types
* Actions (call static methods)
* ~~Add usage samples~~
* Add documentation
* Release?

v0.0.2 - Stability

* Minor refactors to make extensibility easier
* Increase test coverage
* Investigate/Implement small Java performance wins
    * Cache bundles rather than generating each time
    * Generate preference builders instead of bundles?
* Investigate performance of XML vs Java generation

v0.0.3 - Features

* Inject String Arrays (multi-select)
* Support injection for non-static fields
* XML support?

## Thanks / Attributions

* [Tweaks](https://github.com/facebook/Tweaks) for the initial inspiration. 
* [Number picker](https://github.com/vanniktech/VNTNumberPickerPreference) taken and modified, thanks Vanniktech/Niklas Baudy!
* [Seismic](https://github.com/square/seismic) for shake detection
