module com.openelements.data {
    requires jakarta.persistence;
    requires com.google.gson;
    requires io.helidon.webserver.cors;
    requires org.jspecify;
    requires org.slf4j;
    requires org.hibernate.orm.core;
    requires io.helidon.webclient;
    requires io.swagger.v3.oas.models;
    requires com.fasterxml.jackson.databind;
    requires io.helidon.webserver.staticcontent;
    requires io.helidon.webserver;
    requires org.kohsuke.github.api;
    requires io.github.cdimascio.dotenv.java;

    exports com.openelements.data.db to org.hibernate.orm.core;
    exports com.openelements.data.db.internal to org.hibernate.orm.core;
    opens com.openelements.data.db to org.hibernate.orm.core;
    opens com.openelements.data.sample to org.hibernate.orm.core;
    opens com.openelements.data.sample.pullrequest to org.hibernate.orm.core;
    opens com.openelements.data.provider to org.hibernate.orm.core;
    opens com.openelements.data.sample.employee to org.hibernate.orm.core;
    opens com.openelements.data.provider.db to org.hibernate.orm.core;
    opens com.openelements.data.db.internal to org.hibernate.orm.core;
    opens com.openelements.data.data.db to org.hibernate.orm.core;
}