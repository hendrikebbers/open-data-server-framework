package com.openelements.data.openapi;

import com.openelements.data.data.AttributeType;
import com.openelements.data.data.DataType;
import com.openelements.data.server.DataEndpointMetadata;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
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
import java.util.Objects;
import java.util.Set;
import org.jspecify.annotations.NonNull;

public class OpenApiFactory {

    private final static String MEDIA_JSON = "application/json";

    private final static String MEDIA_PLAIN = "text/plain";

    @NonNull
    public static OpenAPI createOpenApi(@NonNull final Set<DataEndpointMetadata<?>> endpoints) {
        Objects.requireNonNull(endpoints, "endpoints is null");
        final OpenAPI openAPI = new OpenAPI();
        openAPI.info(new Info()
                .title("Meine Minimal-API")
                .version("1.0")
                .description("Programmgenerierte OpenAPI ohne Framework"));

        final Paths paths = new Paths();
        endpoints.stream()
                .map(OpenApiFactory::createPaths)
                .flatMap(Set::stream)
                .forEach(path -> paths.addPathItem(path.path(), path.pathItem()));
        openAPI.paths(paths);
        return openAPI;
    }

    @NonNull
    public static <T> Set<OpenApiPath> createPaths(@NonNull final DataEndpointMetadata<T> metadata) {
        return Set.of(createGetAllPath(metadata), createCountPath(metadata));
    }

    @NonNull
    public static <T> OpenApiPath createGetAllPath(@NonNull final DataEndpointMetadata<T> metadata) {
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
        return new OpenApiPath("/" + metadata.path(), pathItem);
    }

    @NonNull
    public static <T> OpenApiPath createCountPath(@NonNull final DataEndpointMetadata<T> metadata) {
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
        return new OpenApiPath("/" + metadata.path() + "/count", pathItem);
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
    public static <T> ObjectSchema createSchema(@NonNull final DataType<T> dataType) {
        Objects.requireNonNull(dataType, "dataType is null");
        final ObjectSchema schema = new ObjectSchema();
        schema.setName(dataType.name());
        schema.setDescription(dataType.description());
        dataType.attributes().forEach(attribute -> {
            final String attributeName = attribute.name();
            final Schema<?> attributeSchema;
            if (attribute.type() == AttributeType.BOOLEAN) {
                attributeSchema = new BooleanSchema().description(attribute.description());
            } else if (attribute.type() == AttributeType.STRING) {
                attributeSchema = new StringSchema().description(attribute.description());
            } else if (attribute.type() == AttributeType.I18N_STRING) {
                attributeSchema = new StringSchema().description(attribute.description());
            } else if (attribute.type() == AttributeType.NUMBER) {
                attributeSchema = new NumberSchema().description(attribute.description());
            } else if (attribute.type() == AttributeType.DATE_TIME) {
                attributeSchema = new DateTimeSchema().description(attribute.description());
            } else if (attribute.type() == AttributeType.FILE) {
                attributeSchema = new StringSchema().description(attribute.description());
            } else {
                throw new IllegalArgumentException("Unsupported attribute type: " + attribute.type());
            }
            schema.addProperties(attributeName, attributeSchema);
        });
        return schema;
    }
}
