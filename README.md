# kotlin-backend-tool-library
A kotlin backend development tool library
# 轻松的将kotlin加入现有的java项目
Spring Initializr默认的kotlin项目只能在kotlin代码中调用java。不能互相调用。
只需要将
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

# 引入依赖
通过加入如下依赖,就可以把后端常用库全部引入，
比如guava，hutool，common系列，spring-mvc,validation,json常用库，http常用库等等
```xml
<dependency>
    <groupId>com.github.jchanghong</groupId>
    <artifactId>kbtool-lib</artifactId>
    <version>2.7.0</version>
</dependency>
```
并将工作中常用的有些工具类，抽象出来，比如时间你可以像下面这样用：
```kotlin
 val date = "2022-05-05 00:00:00".toDateJdk7OrNull()
    println(date.toStrOrNow())
    println(date.toLocalDateTime().toStrOrNow())
    println(date.toJsonStr())
```