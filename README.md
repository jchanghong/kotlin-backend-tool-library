# kotlin-backend-tool-library
A kotlin backend development tool library
# 轻松的将kotlin加入现有的java项目
## kotlin是java生态中最好用的库！！！
Spring Initializr默认生成的kotlin项目只能在kotlin代码中调用java。不能互相调用。
# 项目使用(引入maven parent)
配置kotlin所有插件，格式化插件，findBug插件等只需要将
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
    <artifactId>kbtool-lib</artifactId>
    <version>2.7.0</version>
</parent>
```
就可以在已有的项目里面随意用kotlin或者用java。不影响现有java代码。

# 项目使用(引入kbtool-lib打包java后端开发常用类库)
通过加入如下依赖,就可以把后端常用库全部引入，
比如guava，hutool，common系列，spring-mvc,validation,json常用库，http常用库等等
```xml
<dependency>
    <groupId>com.github.jchanghong</groupId>
    <artifactId>kbtool-lib</artifactId>
    <version>2.7.0</version>
</dependency>
```
并将工作中常用的工具类，独立出来，方便多项目引入，比如下面这些代码：
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
