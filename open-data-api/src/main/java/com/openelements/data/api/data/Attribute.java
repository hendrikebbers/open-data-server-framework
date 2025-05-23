package com.openelements.data.api.data;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.RECORD_COMPONENT})
@Documented
public @interface Attribute {

    String name() default "";

    int order() default -1;

    boolean partOfIdentifier() default false;

    boolean required() default false;
}
