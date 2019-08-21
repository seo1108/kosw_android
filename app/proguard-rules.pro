# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keepclassmembers enum * { *; }
#-keep public class kr.co.photointerior.kosw.rest.DefaultRestClient { *; }
#-keep public class kr.co.photointerior.kosw.rest.HttpClientHolder { *; }
#-keep public class kr.co.photointerior.kosw.rest.PersistentCookieStore { *; }
#-keep public class kr.co.photointerior.kosw.rest.SerializableHttpCookie { *; }

# Keep Crashlytics annotations
-keepattributes *Annotation*
# Keep file names/line numbers
-keepattributes SourceFile,LineNumberTable
# Keep custom exceptions
-keep public class * extends java.lang.Exception
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

-keep class com.ptechsolutions.android.authenticrecipe.core.** { *; }

-keepattributes *Annotation*
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }

-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
-dontwarn org.apache.commons.**
-dontwarn com.google.**
-dontwarn com.j256.ormlite**
-dontwarn org.apache.http**

-keepattributes SourceFile,LineNumberTable
-keep class com.j256.**
-keepclassmembers class com.j256.** { *; }
-keep enum com.j256.**
-keepclassmembers enum com.j256.** { *; }
-keep interface com.j256.**
-keepclassmembers interface com.j256.** { *; }


# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

-keepattributes SourceFile, LineNumberTable

# Google Map
-keep class com.google.** { *; }
-keep class com.android.** { *; }
-keep class android.support.** { *; }
-keep class android.arch.** { *; }

#-keep class com.google.android.gms.maps.** { *; }
#-keep interface com.google.android.gms.maps.** { *; }

-keep class org.apache.harmony.awt.** { *; }
-dontwarn org.apache.harmony.awt.**

-keep class com.github.siyamed.** { *; }
-dontwarn com.github.siyamed.**

-keep class com.squareup.picasso.** { *; }
-dontwarn com.squareup.picasso.**

-keep class com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

-keep class com.sun.mail.** { *; }
-dontwarn com.sun.mail.**

-keep class org.codehaus.mojo.** { *; }
-dontwarn org.codehaus.mojo.**

-keep class java.awt.datatransfer.** { *; }
-dontwarn java.awt.datatransfer.**

-keep class java.nio.file.** { *; }
-dontwarn java.nio.file.**

-keep class javax.mail.** { *; }
-dontwarn javax.mail.**

-keep class com.theartofdev.edmodo.** { *; }
-dontwarn com.theartofdev.edmodo.**

-keep class javax.activation.** { *; }
-dontwarn javax.activation.**

# Image zoom
-keep class it.sephiroth.android.library.imagezoom.** { *; }
-dontwarn it.sephiroth.android.library.imagezoom.**

#Magic Indicator for Pager
-keep class com.github.hackware1993.** { *; }
#-donwwarn com.github.hackware1993.**

# Kakao Talk
-keep class com.kakao.** { *; }
-dontwarn com.kakao.**

# Glide
-keep class com.bumptech.glide.** { *; }
-dontwarn com.bumptech.glide.**
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder

#Okhttp3
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

#Okio
-keep class okio.** { *; }
-dontwarn okio.**

#Retrofit
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

#ACRA
-keep class ch.acra.** { *; }
-dontwarn ch.acra.**
-keep class org.acra.** { *; }
-dontwarn org.acra.**

-dontskipnonpubliclibraryclassmembers

##Alt beacon
-keep class org.altbeacon.** { *; }
-keep interface org.altbeacon.** { *; }
-dontwarn org.altbeacon.**

##Shortcut Badger
-keep class me.leolin.shortcutbadger.**{ *; }
-dontwarn me.leolin.shortcutbadger.**

-keep class org.kxml2.**{ *; }
-dontwarn org.kxml2.**

-keep class org.hamcrest.**{ *; }
-dontwarn org.hamcrest.**


-keep class javax.**{ *; }
-dontwarn javax.**

-keep class com.squareup.**{ *; }
-dontwarn com.squareup.**

-keepattributes *Annotation*
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}