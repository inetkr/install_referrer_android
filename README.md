# install_referrer_android

[![](https://jitpack.io/v/inetkr/install_referrer_android.svg)](https://jitpack.io/#inetkr/install_referrer_android)

A lightweight Android library for retrieving the **Google Play Install Referrer**. Supports both **Kotlin** and **Java**, and is distributed via **JitPack**.

---

## 🚀 Installation

### 1. Add JitPack repository

#### Gradle 7+ / Android Gradle Plugin 8+

Edit your project's `settings.gradle`:

```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

#### Older Gradle versions (`android/build.gradle`)

```
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

---

### 2. Add the dependency

```
dependencies {
    implementation 'com.github.inetkr:install_referrer_android:0.0.2'
}
```

---

## 📌 Usage

### Kotlin Example

```kotlin
import com.inetkr.install_referrer.InstallReferrer

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        InstallReferrer.getInstance(this).initialize()
    }
}
```

---

### Java Example

```java
import com.inetkr.install_referrer.InstallReferrer

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InstallReferrer.getInstance(this).initialize()
    }
}
```

---

## 📚 API

### `InstallReferrer.getInstance(this).initialize()`

Retrieves install referrer information from the **Google Play Install Referrer API**.

Returns:

* Click timestamp
* Install timestamp

---

## 🧪 Requirements

* Android 6.0+
* Google Play Store installed
* Gradle 7+ (recommended)

---

## 📦 Release History

### 0.0.2

Initial release.

---

## 📄 License

MIT License © 2025 inetkr
