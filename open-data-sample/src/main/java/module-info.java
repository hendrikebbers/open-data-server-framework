import com.openelements.data.api.DataTypesProvider;

module com.openelements.data.sample {
    requires com.openelements.data.runtime;
    requires com.openelements.data.server;
    requires java.sql;
    requires com.openelements.data.api;

    provides DataTypesProvider with com.openelements.data.sample.DataTypesProviderImpl;
}