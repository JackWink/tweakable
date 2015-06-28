#Tweakable

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

        compile 'com.squareup:seismic:1.0.1'
    }

Once in your Android app, you can annotate any public static boolean or String field:

    @TwkBoolean(screen = "Subscreen 1", category = "Category 1", 
                title = "Feature 1", onSummary = "Feature 1 Enabled!", 
                offSummary = "Feature 1 Disabled", defaultsTo = true)
    public static boolean defaultsTo;

    @TwkString(screen = "Subscreen 1", Category = "Category 1", title = "Selectable String",
                options = {"Pick me!", "Or me!", "No, me!"}, defaultsTo = "Pick me!")
    public static String selectableString;
    
    @TwkString(screen = "Subscreen 1", Category = "Category 1", title = "Editable String",
                defaultsTo = "Edit Me!")
    public static String editableString;

Call `Tweakable.init(Context context)` when the app starts, then display an instance of the 
`TweaksFragment` and your preferences will have been generated.

When you call init, all the default values (or present values) will be injected statically, so
you can access them whenever. For example, if the above annotations were in a class called `Settings`,
accessing `Settings.selectableString` would contain "Pick me!" or the current value stored in 
shared preferences.

## Roadmap

v0.0.1

* ~~Generate preference views from bundle~~
* ~~Make root preferences + categories come before sub-screens~~
* ~~Parse annotations to generate bundle format for preference generation~~
* ~~Clean up bundle format~~
* ~~Allow sub-screens~~
* ~~Generate preferences at compile time~~
* ~~Inject boolean & Boolean values from shared preferences~~
* ~~Inject Strings~~
* ~~Inject int & Integer types~~
* Inject float types 
* Add documentation
* ~~Add usage samples~~

v0.0.2

* Inject String Arrays (multi-select)
* Support injection for non-static fields
* Actions (call static methods, or possibly non-static methods) 
* Look into generating XML instead of java
* Or look to generate more efficient java 

## Progress

- 6/19: Initial bundle structure + preference generation working
- 6/20: Settings is generated at runtime, no injection yet -- might need to do build-time now
- 6/23: Preferences generated at compile time, still no injection, and Android Studio is not a fan.
- 6/23: Android studio has warmed up to the annotation processor
- 6/23: Preferences are injected now, but only on opening/closing of 'TweaksFragment', need to fix. 
- 6/27: Preferences are injected on init, library supports strings and boolean types, preferences are 
        updated as they change.
- 6/27: Most dead/redundent code is removed, still room for improvement.
