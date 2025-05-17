module com.openelements.data.api {
    requires org.jspecify;

    exports com.openelements.data.api;
    exports com.openelements.data.api.context;
    exports com.openelements.data.api.data;
    exports com.openelements.data.api.translation;
    exports com.openelements.data.api.types;

    uses com.openelements.data.api.DataSourceProvider;
    uses com.openelements.data.api.DataTypesProvider;
    uses com.openelements.data.api.TranslationProvider;
}