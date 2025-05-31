module com.openelements.data.runtime {
    requires com.openelements.data.api;
    requires static org.jspecify;
    requires org.slf4j;
    requires com.google.gson;
    requires java.sql;

    exports com.openelements.data.runtime.sql;
    exports com.openelements.data.runtime.sql.repositories;
    exports com.openelements.data.runtime.sql.tables;
    exports com.openelements.data.runtime.data;
    exports com.openelements.data.runtime.sql.types;
    exports com.openelements.data.runtime.sql.statement;
    exports com.openelements.data.runtime.h2;
    exports com.openelements.data.runtime.sql.types.impl;
    exports com.openelements.data.runtime;
    exports com.openelements.data.runtime.types;

    uses com.openelements.data.runtime.sql.types.SqlTypeSupport;

    uses com.openelements.data.runtime.DataSource;
    uses com.openelements.data.runtime.DataTypeProvider;

    provides com.openelements.data.runtime.sql.types.SqlTypeSupport with
            com.openelements.data.runtime.sql.types.impl.BigDecimalSupport,
            com.openelements.data.runtime.sql.types.impl.BinaryDataSupport,
            com.openelements.data.runtime.sql.types.impl.BooleanSupport,
            com.openelements.data.runtime.sql.types.impl.ByteArraySupport,
            com.openelements.data.runtime.sql.types.impl.ClassSupport,
            com.openelements.data.runtime.sql.types.impl.DoubleSupport,
            com.openelements.data.runtime.sql.types.impl.EnumSupport,
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
            com.openelements.data.runtime.sql.types.impl.StringSetSupport,
            com.openelements.data.runtime.sql.types.impl.URISupport,
            com.openelements.data.runtime.sql.types.impl.UUIDSupport,
            com.openelements.data.runtime.sql.types.impl.YearMonthSupport,
            com.openelements.data.runtime.sql.types.impl.ZonedDateTimeSupport;

}