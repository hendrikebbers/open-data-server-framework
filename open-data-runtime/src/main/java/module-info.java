import com.openelements.data.runtime.sql.support.BooleanSupport;
import com.openelements.data.runtime.sql.support.I18NSupport;

module com.openelements.data.runtime {
    requires com.openelements.data.api;
    requires java.sql;
    requires org.jspecify;

    exports com.openelements.data.runtime;
    exports com.openelements.data.runtime.sql;
    exports com.openelements.data.runtime.spi;

    uses DataAttributeTypeSupport;

    provides DataAttributeTypeSupport with
            BooleanSupport,
            I18NSupport;

}