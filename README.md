The example has been tested on Android 33
It use only native code.

The dependcies are:
implementation("com.google.accompanist:accompanist-permissions:0.34.0")
implementation("androidx.documentfile:documentfile:1.0.1")

For version below 33, the code to implement permissions is to be done (but should be a piece of cake if you managed to not blow your brain out trying to work out the files operations of Android API).
This version of the code was implemented for version 28 and above, so unsure of how it work below (and above).

The manifest has seen some change:
- *Permissions*
```
    <!--    Permissions-->
    <!--Read external storage for import of data base-->
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
        android:maxSdkVersion="32" />
    <!--Write external storage for export/backup of data base-->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        android:maxSdkVersion="32"
        tools:ignore="ScopedStorage" />
```
- *<application ...* & *<activity ...*
```
  android:grantUriPermissions="true"
```

- *<intent*
```
      <action android:name="android.content.action.DOCUMENTS_PROVIDER" />
```
The permissions are unecessary for 33 and above as they are handled directly by the remember function.
Concerning *android:grantUriPermissions="true"* it is here so that other app can read your file. I don't understand the in and out but it seems to work. I put it in *application* and *activity* because it works that way, I don't know more.

The `contentResolver` is always needed to work with `DocumentFile` so to access it, the best is to use the `ApplicationContext` from what I gathered. It is, allegdly, in part with the Android way of doing things (if there was ever anyway).
