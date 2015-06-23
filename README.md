#Tweakable

Feature flags and tweakable values for Android.

## Roadmap

v0.0.1

* ~~Generate preference views from bundle~~
* ~~Make root preferences + categories come before sub-screens~~
* ~~Parse annotations to generate bundle format for preference generation~~
* ~~Clean up bundle format~~
* ~~Allow sub-screens~~
* ~~Generate preferences at compile time~~
* Inject boolean & Boolean values from shared preferences

v0.0.2

* Inject int & Integer types
* Look in to possibility of generating XML at compile time

v0.0.3

* Inject Strings


## Progress

- 6/19: Initial bundle structure + preference generation working
- 6/20: Settings is generated at runtime, no injection yet -- might need to do build-time now
- 6/23: Preferences generated at compile time, still no injection, and Android Studio is not a fan.
- 6/23: Android studio has warmed up to the annotation processor
