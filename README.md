# scoped-viewmodel-compose

[![](https://jitpack.io/v/uk.co.conjure/scoped-viewmodel-compose.svg)](https://jitpack.io/#uk.co.conjure/scoped-viewmodel-compose)

Android library to provide ViewModels with a scope for Composables.

This library is currently in development and has not been tested in a production environment.

## Including the library

Add `jitpack.io` to your repositories in your **projects** `build.gradle` file.

```gradle
allprojects {
    repositories {
	    ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add scoped-viewmodel-compose to your dependencies of your module.

```gradle
dependencies {
  implementation 'uk.co.conjure:scoped-viewmodel-compose:TAG'
}
```

## Code Sample

Use `CreateScope` to create a scope for your ViewModel. The ViewModel will survive orientation
changes and automatically retrieved by the Composable again when reattached.
The ViewModel will be destroyed when the scope is destroyed.

```kotlin
@Composable
fun MyComposable() {
    CreateScope { vm: MyViewModel ->
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // User your vm here
        }
    }
}
```

Anywhere in your Composable tree you can retrieve the ViewModel from the scope after creating a
scope with `CreateScope`.

```kotlin
@Composable
fun ChildComposable() {
    val vm = scopedViewModel<MyViewModel>()
    // Your composable here ...
}
```

It is recommended to use scopedViewModel on a screen or "widget level".
This means that you should not use it within simple Composables like a button that you want to use
in different places.