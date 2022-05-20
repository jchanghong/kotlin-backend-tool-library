package ${packageName};

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.zaxxer.hikari.HikariDataSource;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;

/**
* 第二个数据源，jpa的相关配置
*/
@Configuration
@EntityScan(basePackages = "${entityNamePackage}")
//1、实体扫描
//2、实体管理ref
//3、事务管理
@EnableJpaRepositories(
basePackages = "${repositoryNamePackage}",
entityManagerFactoryRef = "thirdEntityManagerFactoryBean",
transactionManagerRef = "thirdTransactionManager")
@EnableTransactionManagement
public class JpaThirdConfiguration {

//第二个数据源，必须加Qualifier
@Autowired
@Qualifier("dataSourceThird")
private DataSource dataSource;

//jpa其他参数配置
@Autowired
private JpaProperties jpaProperties;

//实体管理工厂builder
@Autowired
private EntityManagerFactoryBuilder factoryBuilder;
@Autowired
private HibernateProperties hibernateProperties;

/**
* 配置第二个实体管理工厂的bean
* @return
*/
@Bean(name = "thirdEntityManagerFactoryBean")
public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
//        hibernate.dialect -> org.hibernate.dialect.PostgreSQL10Dialect
//        hibernate.dialect -> org.hibernate.dialect.H2Dialect
final Map
<String, Object> hibernateProperties = this.hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings());
hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
hibernateProperties.put("hibernate.hbm2ddl.auto", "update");
return factoryBuilder.dataSource(dataSource)
//这一行的目的是加入jpa的其他配置参数比如（ddl-auto: update等）
//当然这个参数配置可以在事务配置的时候也可以
.properties(hibernateProperties)
.packages("${entityNamePackage}")
.persistenceUnit("thirdPersistenceUnit")
.build();
}

/**
* EntityManager不过解释，用过jpa的应该都了解
* @return
*/
@Bean(name = "thirdEntityManager")
public EntityManager entityManager() {
return entityManagerFactoryBean().getObject().createEntityManager();
}

/**
* jpa事务管理
* @return
*/
@Bean(name = "thirdTransactionManager")
public JpaTransactionManager transactionManager() {
JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
jpaTransactionManager.setEntityManagerFactory(entityManagerFactoryBean().getObject());
return jpaTransactionManager;
}
}
