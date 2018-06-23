dagger2-ktx
===============

Kotlin extension bridge library for [Dagger2](https://github.com/google/dagger) (proof-of-concept)

Read more here: [Kotlin extension function generation 🚀… here's why it's a game-changer for Annotation Processing.](https://medium.com/@blipinsk/kotlin-extension-methods-generation-15b5e6499dc8)

Usage
=====
*For a working implementation of this library see the `sample/` module.*

 1. Add to your module's `build.gradle`

     * for Android project:

         ```groovy
         android {
             sourceSets {
                 main.java.srcDirs += 'src/main/kotlin'
                 debug.java.srcDirs += 'src/debug/kotlin'
                 release.java.srcDirs += 'src/release/kotlin'
                 test.java.srcDirs += 'src/test/kotlin'

                 // For kapt stubs
                 main.java.srcDirs += [file("$buildDir/generated/source/kapt/main")]
                 debug.java.srcDirs += [file("$buildDir/generated/source/kapt/debug")]
                 release.java.srcDirs += [file("$buildDir/generated/source/kapt/release")]
                 test.java.srcDirs += [file("$buildDir/generated/source/kapt/test")]

                 // For kotlin code gen during kapt
                 main.java.srcDirs += [file("$buildDir/generated/source/kaptKotlin/main")]
                 debug.java.srcDirs += [file("$buildDir/generated/source/kaptKotlin/debug")]
                 release.java.srcDirs += [file("$buildDir/generated/source/kaptKotlin/release")]
                 test.java.srcDirs += [file("$buildDir/generated/source/kaptKotlin/test")]
             }
         }
         ```

     * for Java/Kotlin project:

         ```groovy
         apply plugin: 'idea'

         idea {
           module {
             sourceDirs += files(
                 'build/generated/source/kapt/main',
                 'build/generated/source/kaptKotlin/main',
                 'build/tmp/kapt/main/kotlinGenerated')
             generatedSourceDirs += files(
                 'build/generated/source/kapt/main',
                 'build/generated/source/kaptKotlin/main',
                 'build/tmp/kapt/main/kotlinGenerated')
           }
         }
         ```


 2. Instead of creating your component with

     ```java
     DaggerYourComponent.create()
     ```

     use:

     ```java
     Dagger.create(YourComponent::class)
     ```

 3. Enjoy no issues with imports of the `DaggerYourComponent`

Including In Your Project
-------------------------
Add in your `build.gradle`:
```xml
repositories {
    maven { url 'https://dl.bintray.com/blipinsk/maven/' }
}

dependencies {
    kapt "com.bartoszlipinski:dagger2-ktx-compiler:0.1.0"
    implementation "com.bartoszlipinski:dagger2-ktx:0.1.0"
}
```

Developed by
============
 * Bartosz Lipiński

License
=======

    Copyright 2018 Bartosz Lipiński
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.