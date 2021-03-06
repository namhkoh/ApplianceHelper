# AI_AR_PAPER

## Appliance Helper Application

## File Structure

- The android project build and files can be found under the `DemoProjects` folder.
- The other files pertain to the Unity development files handling the Augmented Reality components.

## UI Variants

- The following application consists of 6 different UI variants.

### Variant 1

- On-screen instructions

### Variant 2

- Dialog-driven instructions

### Variant 3

- Dialog-driven instructions with images

### Variant 4

- AR-supported Dialog-driven instructions

### Variant 5

- AR-supported list view instructions

### Variant 6

- Teaching through Dialog-driven Instructions and virtual UI

## Composition

- The AR components are handled by the Vuforia SDK with Unity.
- The Dialog and Front-end components are integrated in Android Studios.

## TODO

- Add appropriate user logging statement in the codebase.
- Overall integration testing of application. 
    - Mock user flow required. 

## Running firebase analytics 
- Run this command to enable DebugView in Firebase console: 
    - `adb shell setprop debug.firebase.analytics.app com.kohdev.AR_AI_DEV` 
- Run these commands to enable live logging of events: 
    - `adb shell setprop log.tag.FA VERBOSE`
    - `adb shell setprop log.tag.FA-SVC VERBOSE`
    - `adb logcat -v time -s FA FA-SVC`
