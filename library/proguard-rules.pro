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


#-----------处理第三方依赖库---------

# Gson
-keep class com.google.gson.** {*;}
-keep class com.google.**{*;}
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.qiancheng.carsmangersystem.**{*;}
# Application classes that will be serialized/deserialized over Gson 下面替换成自己的实体类
-keep class com.xxx.xxx.bean.** { *; }


# 实体类
-keep class com.junfa.base.entity.**{*;}
-keep class com.junfa.base.ui.scan.**{*;}
#kotlinx
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory{}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler{}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler{}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory{}

#-keep class kotlinx.**{
#*;
#}


