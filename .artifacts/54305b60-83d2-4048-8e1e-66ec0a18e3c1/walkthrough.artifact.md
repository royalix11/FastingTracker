# Walkthrough - Emulator Performance Optimization

I have updated the configuration for the `medium_phone` emulator to improve its startup and runtime performance.

## Changes Made

### Emulator Configuration
Updated [config.ini](file:///C:/Users/royalix11/.android/avd/medium_phone.avd/config.ini):
- **GPU Mode**: Changed from `software` to `host`. This enables hardware acceleration, allowing the emulator to use your computer's GPU for rendering.
- **RAM Size**: Increased from `2048MB` to `4096MB`. This provides more overhead for the Android 36 system image to run smoothly.

## Next Steps for You

> [!IMPORTANT]
> To apply these changes, you must fully restart the emulator.

1.  **Close the emulator** if it is currently running (even if it's stuck).
2.  Open the **Device Manager** in Android Studio.
3.  Locate **Medium Phone**.
4.  Click the three dots (⋮) and select **Cold Boot Now**. This ensures the emulator starts fresh with the new hardware settings.
