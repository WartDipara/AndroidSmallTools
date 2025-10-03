# EasyTools - Android Permission Request Library

`EasyTools` is a simple and easy-to-use Android permission request SDK. It supports requesting multiple permissions at once and provides callbacks for authorization results.

## Features

- Support requesting multiple permissions simultaneously
- Simple and clear callback interface
- Based on Fragment, no need to manage Activity lifecycle
- Customizable callback for handling authorization results
- Support usage in both Activity and Fragment

## Getting Started

### 1. Integration

Add `EasyTools` as a module to your Android project and include it in your `settings.gradle`:

```kotlin
include(":EasyTools")
```

Add the dependency in your app module's `build.gradle.kts`:

```kotlin
implementation(project(":EasyTools"))
```

### 2. Usage

#### Requesting Permissions

The `EasyPermissionUtil` utility class supports usage in both Activity and Fragment.

##### Requesting permissions in Activity or Fragment

```java
EasyPermissionUtil.requestPermissions(this, (allGranted, deniedList) -> {
    if (allGranted) {
        // All permissions granted
    } else {
        // deniedList contains the denied permissions
    }
}, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE);
```

#### Callback Interface

`PermissionCallback` interface:

```java
void onResult(boolean allGranted, List<String> deniedPermissions);
```

- `allGranted`: whether all permissions are granted
- `deniedPermissions`: list of denied permissions

---

More to come...
