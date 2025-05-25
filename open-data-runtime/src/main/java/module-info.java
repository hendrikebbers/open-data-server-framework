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
    exports com.openelements.data.runtime.sql.statement;

    uses SqlTypeSupport;

    provides SqlTypeSupport with
            com.openelements.data.runtime.sql.types.BooleanSupport,
            com.openelements.data.runtime.sql.types.StringSupport,
            com.openelements.data.runtime.sql.types.I18NSupport;

}