package com.openelements.data.server.internal.openapi;

import com.openelements.data.data.AttributeType;
import com.openelements.data.data.DataType;
import com.openelements.data.data.Language;
import com.openelements.data.db.AbstractEntity;
import com.openelements.data.server.internal.OpenDataDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import org.jspecify.annotations.NonNull;

public class OpenApiFactory {

    private final static String MEDIA_JSON = "application/json";

    private final static String MEDIA_PLAIN = "text/plain";

    @NonNull
    public static OpenAPI createOpenApi(@NonNull final Collection<OpenDataDefinition<?>> endpoints) {
        Objects.requireNonNull(endpoints, "endpoints is null");
        final OpenAPI openAPI = new OpenAPI();
        final License license = new License()
                .name("Data licence Germany - Zero - Version 2.0")
                .url("https://www.govdata.de/dl-de/zero-2-0")
                .identifier("DL-DE->Zero-2.0");
        final Info info = new Info()
                .title("Meine Minimal-API")
                .version("1.0")
                .description("Programmgenerierte OpenAPI ohne Framework")
                .license(license);
        final Paths paths = new Paths();
        endpoints.stream()
                .map(OpenApiFactory::createPaths)
                .flatMap(Set::stream)
                .forEach(path -> paths.addPathItem(path.path(), path.pathItem()));
        openAPI.info(info)
                .paths(paths);
        endpoints.stream()
                .map(endpoint -> endpoint.dataType())
                .map(OpenApiFactory::createSchema)
                .forEach(schema -> openAPI.schema(schema.getName(), schema));
        return openAPI;
    }

    @NonNull
    public static <E extends AbstractEntity> Set<OpenApiPath> createPaths(
            @NonNull final OpenDataDefinition<E> metadata) {
        return Set.of(createGetAllPath(metadata), createCountPath(metadata));
    }

    @NonNull
    public static <E extends AbstractEntity> OpenApiPath createGetAllPath(
            @NonNull final OpenDataDefinition<E> metadata) {
        Objects.requireNonNull(metadata, "metadata is null");
        final ObjectSchema dataSchema = createSchema(metadata.dataType());
        final ApiResponse successResponse = getSuccessResponse(MEDIA_JSON, new ArraySchema().items(dataSchema));
        final ApiResponses apiResponses = new ApiResponses()
                .addApiResponse("200", successResponse);
        final Operation getAllOperation = new Operation()
                .summary("Get all " + metadata.dataType().name())
                .description("Get all " + metadata.dataType().name())
                .responses(apiResponses);
        final PathItem pathItem = new PathItem().get(getAllOperation)
                .summary("Get all " + metadata.dataType().name())
                .description("Get all " + metadata.dataType().name());
        return new OpenApiPath("/api/" + metadata.pathName(), pathItem);
    }

    @NonNull
    public static <E extends AbstractEntity> OpenApiPath createCountPath(
            @NonNull final OpenDataDefinition<E> metadata) {
        Objects.requireNonNull(metadata, "metadata is null");
        final ApiResponse successResponse = getSuccessResponse(MEDIA_PLAIN, new IntegerSchema());
        final ApiResponses apiResponses = new ApiResponses()
                .addApiResponse("200", successResponse);
        final Operation getCountOperation = new Operation()
                .summary("Get count of " + metadata.dataType().name())
                .description("Get count of " + metadata.dataType().name())
                .responses(apiResponses);
        final PathItem pathItem = new PathItem().get(getCountOperation)
                .summary("Get count of " + metadata.dataType().name())
                .description("Get count of " + metadata.dataType().name());
        return new OpenApiPath("/api/" + metadata.pathName() + "/count", pathItem);
    }

    @NonNull
    public static ApiResponse getSuccessResponse(@NonNull final String mediaType, @NonNull final Schema<?> schema) {
        Objects.requireNonNull(mediaType, "mediaType is null");
        Objects.requireNonNull(schema, "schema is null");
        return new ApiResponse()
                .description("OK")
                .content(new io.swagger.v3.oas.models.media.Content()
                        .addMediaType(mediaType,
                                new io.swagger.v3.oas.models.media.MediaType()
                                        .schema(schema)));
    }

    @NonNull
    public static <E extends AbstractEntity> ObjectSchema createSchema(@NonNull final DataType<E> dataType) {
        Objects.requireNonNull(dataType, "dataType is null");
        final Language language = Language.EN;
        final ObjectSchema schema = new ObjectSchema();
        schema.setName(dataType.name());
        schema.setDescription(dataType.description());
        schema.setType("object");
        dataType.attributes().forEach(attribute -> {
            final String attributeName = attribute.identifier();
            final Schema<?> attributeSchema;
            if (attribute.type() == AttributeType.BOOLEAN) {
                attributeSchema = new BooleanSchema()
                        .description(attribute.description().resolve(language))
                        .type("boolean");
            } else if (attribute.type() == AttributeType.STRING) {
                attributeSchema = new StringSchema()
                        .description(attribute.description().resolve(language))
                        .type("string");
            } else if (attribute.type() == AttributeType.I18N_STRING) {
                attributeSchema = new StringSchema()
                        .description(attribute.description().resolve(language))
                        .type("string");
            } else if (attribute.type() == AttributeType.NUMBER) {
                attributeSchema = new NumberSchema()
                        .description(attribute.description().resolve(language))
                        .type("number");
            } else if (attribute.type() == AttributeType.DATE_TIME) {
                attributeSchema = new DateTimeSchema()
                        .description(attribute.description().resolve(language))
                        .type("string")
                        .format("date-time");
            } else if (attribute.type() == AttributeType.YEAR_MONTH) {
                attributeSchema = new DateTimeSchema()
                        .description(attribute.description().resolve(language))
                        .type("string")
                        .format("year-month");
            } else if (attribute.type() == AttributeType.FILE) {
                attributeSchema = new StringSchema()
                        .description(attribute.description().resolve(language))
                        .type("string");
            } else if (attribute.type() == AttributeType.FILE_URL) {
                attributeSchema = new StringSchema()
                        .description(attribute.description().resolve(language))
                        .type("string");
            } else {
                throw new IllegalArgumentException("Unsupported attribute type: " + attribute.type());
            }
            schema.addProperties(attributeName, attributeSchema);
        });
        return schema;
    }
}
