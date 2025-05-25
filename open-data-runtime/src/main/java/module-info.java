module com.openelements.data.runtime {
    requires com.openelements.data.api;
    requires java.sql;
    requires org.jspecify;
    requires org.slf4j;

    exports com.openelements.data.runtime.sql;
    exports com.openelements.data.runtime.sql.repositories;
    exports com.openelements.data.runtime.sql.tables;
    exports com.openelements.data.runtime.data;
    exports com.openelements.data.runtime.sql.types;
    exports com.openelements.data.runtime.sql.support;
    exports com.openelements.data.runtime.sql.statement;

    uses com.openelements.data.runtime.sql.support.DataAttributeTypeSupport;

    provides com.openelements.data.runtime.sql.support.DataAttributeTypeSupport with
            com.openelements.data.runtime.sql.support.BooleanSupport,
            com.openelements.data.runtime.sql.support.StringSupport,
            com.openelements.data.runtime.sql.support.I18NSupport;

}