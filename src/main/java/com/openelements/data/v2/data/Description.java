package com.openelements.data.v2.data;

import com.openelements.data.data.Language;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface Description {

    String name() default "";

    String description() default "";

    Language language() default Language.EN;
}

