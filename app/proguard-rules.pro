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

-dontwarn android.databinding.**
-keep class android.databinding.** { *; }

-dontwarn okio.**
-dontwarn retrofit2.Platform$Java8
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

-dontwarn javax.xml.bind.**
-keep class javax.xml.bind.** { *; }

-dontwarn com.prelimtek.android.customcomponents
-keep class com.prelimtek.android.customcomponents.** { *; }

-dontwarn com.google.common.base.**
#-keep class com.google.common.base.** {*;}
-dontwarn com.google.errorprone.annotations.**
#-keep class com.google.errorprone.annotations.** {*;}
-dontwarn com.google.j2objc.annotations.**
#-keep class com.google.j2objc.annotations.** { *; }
-dontwarn java.lang.ClassValue
#-keep class java.lang.ClassValue { *; }
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
#-keep class org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement { *; }
-dontwarn com.fasterxml.jackson.databind.ext.**

-dontwarn org.bitcoinj.store.LevelDBBlockStore
-dontwarn org.bitcoinj.store.LevelDBFullPrunedBlockStore
-dontwarn org.bitcoinj.store.LevelDBFullPrunedBlockStore**
-dontwarn org.bitcoinj.store.WindowsMMapHack
-dontwarn sawtooth.sdk.messaging.SendReceiveThread**
-dontwarn io.jsonwebtoken.impl.crypto.EllipticCurveProvider
-dontwarn org.slf4j.LoggerFactory
-dontwarn org.slf4j.MDC
-dontwarn org.slf4j.impl.StaticMDCBinder
-dontwarn org.slf4j.MarkerFactory
-dontwarn org.slf4j.impl.StaticMarkerBinder
