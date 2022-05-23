# kotlin-backend-tool-library
A kotlin backend development tool library,mainly includes common kotlin extensions for daily projects
# Easily add kotlin to existing Java projects
# 轻松将kotlin加入现有的java项目
### kotlin是java生态中最好用的库！！！
Spring Initializr默认生成的kotlin项目只能在kotlin代码中调用java。不能互相调用。
由于kotlin和java的良好互操作性，可以在任何项目中加入kotlin代码，不影响现有的java代码。

# 如何使用？
## 1.引入maven parent
配置kotlin所有插件，还包括格式化插件，findBug插件等常用插件只需要将
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.0</version>
</parent>
```
替换成：
```xml
<parent>
    <groupId>com.github.jchanghong</groupId>
    <artifactId>kbtool-parent</artifactId>
    <version>2.7.0</version>
</parent>
```

## 2.加入kbtool-lib依赖(如果只是想增加kotlin插件配置，不需要引入)
通过加入如下依赖,就可以把后端常用库全部引入，
比如guava，hutool，common系列，spring-mvc,validation,json常用库，http，retry常用库等等
```xml
<dependency>
    <groupId>com.github.jchanghong</groupId>
    <artifactId>kbtool-lib</artifactId>
    <version>2.7.0</version>
</dependency>
```
并将工作中常用的工具类，独立出来，方便多项目引入，比如下面这些代码（会持续更新）：
```kotlin
 val date = "2022-05-05 00:00:00".toDateJdk7OrNull()
    println(date.toStrOrNow())
    println(date.toLocalDateTime().toStrOrNow())
    println(date.toJsonStr())
    println("hello".toUnderlineCase())
    println("hello".upperFirst())
    println("hello".toCamelCase())
    RetryHelper.submitByRetry4Times(aCallable)
    RetryHelper.submitByRetryNTimes(aCallable)
    HttpHelper.postJsonStringSyn("https://","{}")
    val abean="{}".jsonToObject<ABean>()
```
kbtool-parent默认加入
---
1. kotlin-reflect
2. kotlin-stdlib-jdk8
3. lombok
4. spring-boot-starter-test
---
kbtool-lib默认加入以下依赖
---
1. guava-retrying 重试
1. hutool-all 工具库
1. okhttp 工具库
1. kotlinx-coroutines
1. guava 工具库
1. cglib
1. json-path
1. commons-fileupload
1. commons-net
1. commons-io
1. commons-text
1. springfox swagger接口
1. fastjson
1. spring-boot-starter-json
1. spring-boot-starter-validation
1. spring-boot-starter-web
1. jsoup
1. ojdbc8
1. postgresql 可以去掉换mysql等
1. hibernate-types-52 jpa插件库
1. jackson-datatype-* json额外的类型库
---
