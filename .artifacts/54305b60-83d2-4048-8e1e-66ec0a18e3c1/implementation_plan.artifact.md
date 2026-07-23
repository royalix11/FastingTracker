# Implementation Plan - Optimize Emulator Performance

The `medium_phone` emulator is likely taking a long time to start because it is currently configured to use **Software GPU rendering** (`hw.gpu.mode=software`). This forces the CPU to handle graphics, which is extremely slow.

Since hardware acceleration (WHPX) is available on your system, we should switch the GPU mode to `host` and potentially increase the allocated RAM for a smoother experience.

## Proposed Changes

### Emulator Configuration

#### [MODIFY] [config.ini](file:///C:/Users/royalix11/.android/avd/medium_phone.avd/config.ini)
- Change `hw.gpu.mode=software` to `hw.gpu.mode=host`.
- Increase `hw.ramSize` from `2048` to `4096` (optional but recommended).

## Verification Plan

### Manual Verification
1. I will apply the changes to the `config.ini` file.
2. I will then ask you to close any running instances of the `medium_phone` emulator.
3. You can then restart the emulator from the Android Studio Device Manager (using "Cold Boot Now" if it still feels slow).
