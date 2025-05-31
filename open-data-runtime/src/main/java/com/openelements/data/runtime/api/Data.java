package com.openelements.data.runtime.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface Data {

    String name() default "";

    boolean visible() default true;

    boolean publiclyAvailable() default true;

    boolean isVirtual() default false;
}
