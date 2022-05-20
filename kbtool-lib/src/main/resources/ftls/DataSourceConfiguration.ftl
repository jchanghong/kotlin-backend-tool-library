package ${packageName};

import cn.hutool.db.DbUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

/**
* 数据库配置
*/
@Configuration
public class DataSourceConfiguration {

/**
*  第一个数据连接，默认优先级最高
* @return
*/
@Bean(name = "dataSourceFirst")
@Primary
@ConfigurationProperties(prefix = "spring.datasource.hikari")
public static DataSource dataSourceFirst() {
//这种方式的配置默认只满足spring的配置方式，如果使用其他数据连接（druid）,需要自己独立获取配置
return DataSourceBuilder.create().type(HikariDataSource.class)
.build();
}

/**
* 第二个数据源
* @return
*/
@Bean(name = "dataSourceSecond")
@ConfigurationProperties(prefix = "spring.datasource2.hikari")
public static DataSource dataSourceSecond() {
//        final EmbeddedDatabaseBuilder databaseBuilder = new EmbeddedDatabaseBuilder();
//        databaseBuilder.setType(EmbeddedDatabaseType.H2);
//        return databaseBuilder.build();
return DataSourceBuilder.create().type(HikariDataSource.class)
.build();
}
/**
* 第三个数据源
* @return
*/
@Bean(name = "dataSourceThird")
@ConfigurationProperties(prefix = "spring.datasource3.hikari")
public static DataSource dataSourceThird() {
//        final EmbeddedDatabaseBuilder databaseBuilder = new EmbeddedDatabaseBuilder();
//        databaseBuilder.setType(EmbeddedDatabaseType.H2);
//        return databaseBuilder.build();
return DataSourceBuilder.create().type(HikariDataSource.class)
.build();
}
}