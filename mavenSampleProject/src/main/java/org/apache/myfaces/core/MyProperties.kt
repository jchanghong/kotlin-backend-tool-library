package org.apache.myfaces.core

import org.hibernate.validator.constraints.Length
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated

@ConstructorBinding
@ConfigurationProperties("my.service")
@Validated
public class MyProperties(@field:Length(min = 4) val test: String) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        check("test" == test)
    }
}
