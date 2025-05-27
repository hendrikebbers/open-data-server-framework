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
    exports com.openelements.data.runtime.h2;
    exports com.openelements.data.runtime.sql.types.impl;

    uses com.openelements.data.runtime.sql.types.SqlTypeSupport;

    provides com.openelements.data.runtime.sql.types.SqlTypeSupport with
            com.openelements.data.runtime.sql.types.impl.BigDecimalSupport,
            com.openelements.data.runtime.sql.types.impl.BinaryDataSupport,
            com.openelements.data.runtime.sql.types.impl.BooleanSupport,
            com.openelements.data.runtime.sql.types.impl.DoubleSupport,
            com.openelements.data.runtime.sql.types.impl.I18NSupport,
            com.openelements.data.runtime.sql.types.impl.IntegerSupport,
            com.openelements.data.runtime.sql.types.impl.LocalDateSupport,
            com.openelements.data.runtime.sql.types.impl.LocalDateTimeSupport,
            com.openelements.data.runtime.sql.types.impl.LongSupport,
            com.openelements.data.runtime.sql.types.impl.PrimitiveDoubleSupport,
            com.openelements.data.runtime.sql.types.impl.PrimitiveLongSupport,
            com.openelements.data.runtime.sql.types.impl.PrimitiveIntegerSupport,
            com.openelements.data.runtime.sql.types.impl.PrimitiveBooleanSupport,
            com.openelements.data.runtime.sql.types.impl.StringSupport,
            com.openelements.data.runtime.sql.types.impl.URISupport,
            com.openelements.data.runtime.sql.types.impl.UUIDSupport,
            com.openelements.data.runtime.sql.types.impl.ZonedDateTimeSupport;

}