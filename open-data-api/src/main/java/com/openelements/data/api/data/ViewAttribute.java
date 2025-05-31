package com.openelements.data.api.data;

public @interface ViewAttribute {

    Class<? extends Data> origin();

    String originAttribute() default "";

    String name() default "";

    int order() default -1;

    boolean visible() default true;
}
