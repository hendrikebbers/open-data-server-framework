module com.openelements.data.api {
    requires org.jspecify;

    exports com.openelements.data.api;
    exports com.openelements.data.api.context;
    exports com.openelements.data.api.data;
    exports com.openelements.data.api.translation;
    exports com.openelements.data.api.types;

    uses com.openelements.data.api.DataSource;
    uses com.openelements.data.api.DataTypeProvider;
    uses com.openelements.data.api.TranslationProvider;
}