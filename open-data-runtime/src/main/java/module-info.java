import com.openelements.data.runtime.sql.h2.types.H2ByteArraySupport;
import com.openelements.data.runtime.sql.postgres.PostgresByteArraySupport;
import com.openelements.data.runtime.sql.types.impl.BinarySupport;
import com.openelements.data.runtime.sql.types.impl.ZonedDateTimeSupport;

module com.openelements.data.runtime {
    requires org.slf4j;
    requires com.google.gson;
    requires java.sql;
    requires static org.jspecify;

    exports com.openelements.data.runtime.api;
    exports com.openelements.data.runtime.api.types;
    exports com.openelements.data.runtime.sql.api;
    exports com.openelements.data.runtime.types to com.openelements.recordstore.server;
    exports com.openelements.data.runtime.data to com.openelements.recordstore.server;
    exports com.openelements.data.runtime.integration to com.openelements.recordstore.server;

    uses com.openelements.data.runtime.sql.types.SqlTypeSupport;
    uses com.openelements.data.runtime.api.DataSource;
    uses com.openelements.data.runtime.api.DataTypeProvider;

    provides com.openelements.data.runtime.sql.types.SqlTypeSupport with
            com.openelements.data.runtime.sql.types.impl.BigDecimalSupport,
            BinarySupport,
            com.openelements.data.runtime.sql.types.impl.BooleanSupport,
            H2ByteArraySupport,
            PostgresByteArraySupport,
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
            ZonedDateTimeSupport;

}