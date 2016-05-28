# RxContacts
Android library to get contacts using RxJava

# Setup
To use this library your minSdkVersion must be >= 15.

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
    compile 'de.ulrichraab:rx-contacts:1.0.2'
}
```

# Example

```java
RxContacts.with(context)
          .requestContacts()
          .subscribe(new Observer<List<Contact>>() {
             @Override
             public void onCompleted() {
                // Loading done
             }
             @Override
             public void onError (Throwable e) {
                // Handle error
             }
             @Override
             public void onNext(List<Contact> contacts) {
                // Use the contacts
             }
          });
```