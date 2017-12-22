# RxContacts
Android library to get contacts using RxJava

**Current version: 1.1.4**

# Setup
To use this library your minSdkVersion must be >= 16.

In the build.gradle of your project add:

```gradle
repositories {
    maven {
       url 'https://dl.bintray.com/ulrichraab/maven'
    }
}
```

In the build.gradle of your app module add:

```gradle
dependencies {
    compile 'de.ulrichraab:rx-contacts:1.1.4'
}
```

# Example

```java
RxContacts.fetch(context)
          .subscribe(new Observer<Contact>() {
             @Override
             public void onCompleted() {
                // Loading done
             }
             @Override
             public void onError (Throwable e) {
                // Handle error
             }
             @Override
             public void onNext(Contact contact) {
                // Use the contact
             }
          });
```
