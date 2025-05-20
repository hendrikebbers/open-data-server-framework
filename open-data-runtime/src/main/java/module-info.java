module com.openelements.data.runtime {
    requires com.openelements.data.api;
    requires java.sql;
    requires org.jspecify;

    exports com.openelements.data.runtime;

    uses com.openelements.data.runtime.DataAttributeTypeSupport;

    provides com.openelements.data.runtime.DataAttributeTypeSupport with
            com.openelements.data.runtime.impl.BooleanSupport,
            com.openelements.data.runtime.impl.I18NSupport;

}