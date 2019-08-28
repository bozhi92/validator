package cn.bruk.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * @author fengbozhi
 * @date 2019-08-07
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Validators.class)
@Documented
public @interface Validator {
    
    ValidatorEnum type();
    String value() default "";
    String[] collections() default {};
}
