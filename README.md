# EasyTools - Android 權限申請工具庫

`EasyTools` 是一個簡單易用的 Android 權限申請 SDK，支持多種權限一次性申請，並通過回調獲取授權結果。

## 功能特點

- 支持多個權限同時申請
- 回調接口簡單明瞭
- 基於 Fragment 實現，無需關心 Activity 生命週期
- 支持自定義回調處理授權結果
- 同時支持在 Activity 和 Fragment 中調用

## 快速開始

### 1. 集成方式

將 `EasyTools` 作為 module 添加到你的 Android 項目中，並在 `settings.gradle` 中 include：

```kotlin
include(":EasyTools")
```

在你的 app module 的 `build.gradle.kts` 添加依賴：

```kotlin
implementation(project(":EasyTools"))
```

### 2. 使用方法

#### 權限申請

`EasyPermissionUtil` 工具類支持在 Activity 和 Fragment 中調用。

##### 在 Activity 和 Fragment 中申請權限

```java
EasyPermissionUtil.requestPermissions(this, (allGranted, deniedList) -> {
    if (allGranted) {
        // 所有權限已授權
    } else {
        // deniedList 包含被拒絕的權限
    }
}, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE);
```

#### 回調接口說明

`PermissionCallback` 回調接口：

```java
void onResult(boolean allGranted, List<String> deniedPermissions);
```

- `allGranted`：是否全部授權
- `deniedPermissions`：被拒絕的權限列表

---

## EasyBoostBall

一個自定義加速球的view控件

### 使用方法

和使用其他view一樣的方法，透過在xml文件裏面調用它，

並透過設置監聽器來使用它

有兩個方法提供：

1. `setProgressWithAnimation(float)` 帶動畫的

2. `setProgress(float)` 不帶動畫的

傳入的值是最大值，比如傳入1就表示從0到1f，也就是0-100%。推薦取值為[0,1]

---

後續的其餘功能還待開發中...
