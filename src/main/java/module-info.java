module com.openelements.data {
    requires jakarta.persistence;
    requires com.google.gson;
    requires io.helidon.webserver.cors;
    requires io.helidon.webserver;
    requires org.jspecify;
    requires org.slf4j;
    requires org.hibernate.orm.core;
    requires io.helidon.webclient;

    exports com.openelements.data.db to org.hibernate.orm.core;
    opens com.openelements.data.db to org.hibernate.orm.core;
    opens com.openelements.data.sample to org.hibernate.orm.core;
    opens com.openelements.data.sample.pullrequest to org.hibernate.orm.core;
    opens com.openelements.data.provider to org.hibernate.orm.core;
    opens com.openelements.data.sample.employee to org.hibernate.orm.core;
}