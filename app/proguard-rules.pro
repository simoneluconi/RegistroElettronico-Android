# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/luca/android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontwarn java.lang.invoke.*

-keepattributes InnerClasses

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepattributes Annotations

-keep class okhttp3.* { *; }
-keep interface okhttp3.* { *; }

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

-dontwarn okio.**

-dontwarn okhttp3.**

-dontwarn com.akexorcist.roundcornerprogressbar.**

-keepclassmembers class android.support.v7.graphics.drawable.DrawerArrowDrawable { public *; }

-keep class android.support.v7.widget.SearchView { *; }

-keep class com.sharpdroid.registroelettronico.Database.Entities** { *; }

#ABOUT-LIBRARIES
-keep class .R
-keep class **.R$* {
    <fields>;
}
-keepclasseswithmembers class **.R$* {
    public static final int define_*;
}