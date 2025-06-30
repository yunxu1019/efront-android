"# efront-android" 
已配置如下加载源，可根据当地网络自行修改
* 文件 gradle/wrapper/gradle-wrapper.properties
```properties
distributionUrl=https\://mirrors.cloud.tencent.com/gradle/gradle-8.14.2-all.zip
```
* 文件 build.gradle
```groovy
buildscript{
    // ...
    repositories{
        // ...
        maven { url = 'https://maven.aliyun.com/repository/google' }
        mavenCentral()
    }
}
```
配置项有
```
JAVA_HOME
path %java_home%\bin
```

编译直接使用 `gradlew build`