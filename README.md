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

Once in your Android app, you can annotate any public static boolean field:

    @TwkBoolean(screen = "Subscreen 1", category = "Category 1", title = "Enable Cool Feature",
                onSummary = "Cool feature enabled!", offSummary = "Cool feature disabled",
                defaultsTo = true)
    public static boolean defaultsTo;

Call `Tweakable.init(Context context)` when the app starts, then display an instance of the 
`TweaksFragment` and your preferences will have been generated.

## Roadmap

v0.0.1

* ~~Generate preference views from bundle~~
* ~~Make root preferences + categories come before sub-screens~~
* ~~Parse annotations to generate bundle format for preference generation~~
* ~~Clean up bundle format~~
* ~~Allow sub-screens~~
* ~~Generate preferences at compile time~~
* ~~Inject boolean & Boolean values from shared preferences~~
* "Massive" cleanup, codebase is small
* Decide on 'PreferenceStore' class
* Add documentation
* Add usage samples 

v0.0.2

* Inject int & Integer types
* Look at generating XML prefs at compile time instead of java

v0.0.3

* Inject Strings


## Progress

- 6/19: Initial bundle structure + preference generation working
- 6/20: Settings is generated at runtime, no injection yet -- might need to do build-time now
- 6/23: Preferences generated at compile time, still no injection, and Android Studio is not a fan.
- 6/23: Android studio has warmed up to the annotation processor
- 6/23: Preferences are injected now, but only on opening/closing of 'TweaksFragment', need to fix. 
