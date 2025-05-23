import com.openelements.data.runtime.sql.support.BooleanSupport;
import com.openelements.data.runtime.sql.support.I18NSupport;
import com.openelements.data.runtime.sql.support.StringSupport;

module com.openelements.data.runtime {
    requires com.openelements.data.api;
    requires java.sql;
    requires org.jspecify;
    requires org.slf4j;

    exports com.openelements.data.runtime;
    exports com.openelements.data.runtime.sql;
    exports com.openelements.data.runtime.sql.repositories;
    exports com.openelements.data.runtime.sql.tables;

    uses com.openelements.data.runtime.sql.DataAttributeTypeSupport;

    provides com.openelements.data.runtime.sql.DataAttributeTypeSupport with
            BooleanSupport,
            StringSupport,
            I18NSupport;

}