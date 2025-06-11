package com.openelements.recordstore.server.internal.openapi;

import com.openelements.data.runtime.data.DataType;
import com.openelements.data.runtime.types.ByteArray;
import com.openelements.recordstore.server.internal.PathResolver;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.ByteArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.media.UUIDSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenApiFactory {

    private final Logger log = LoggerFactory.getLogger(OpenApiFactory.class);

    private final PathResolver pathResolver;

    private final Set<DataType<?>> dataTypes;

    public OpenApiFactory(PathResolver pathResolver, Set<DataType<?>> dataTypes) {
        this.pathResolver = pathResolver;
        this.dataTypes = dataTypes;
    }

    public OpenAPI create() {
        OpenAPI openAPI = new OpenAPI();

        Info info = new Info()
                .title("Record Store API")
                .version("0.1.0")
                .description("A simple way to store and load records");
        openAPI.info(info);

        SecurityScheme bearerAuthScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
        Components components = new Components().addSecuritySchemes("bearerAuth", bearerAuthScheme);
        openAPI.components(components);

        final Tag recordStoreApiTag = new Tag().name("Record Store API")
                .description("API endpoint of the Record Store");
        final Tag recordDataTag = new Tag().name("Record Data")
                .description("API endpoints to access custom data types");
        openAPI.addTagsItem(recordStoreApiTag);
        openAPI.addTagsItem(recordDataTag);

        final Paths paths = new Paths();
        dataTypes.forEach(dataType -> {
            Schema<?> dataSchema = new Schema<>().type("object");
            dataType.attributes().forEach(attribute -> {
                String attributeName = attribute.name();
                Schema<?> attributeSchema;
                if (attribute.type().equals(Integer.class)) {
                    attributeSchema = new IntegerSchema();
                } else if (attribute.type().equals(Integer.TYPE)) {
                    attributeSchema = new IntegerSchema();
                } else if (attribute.type().equals(Long.class)) {
                    attributeSchema = new NumberSchema();
                } else if (attribute.type().equals(Long.TYPE)) {
                    attributeSchema = new NumberSchema();
                } else if (attribute.type().equals(Double.class)) {
                    attributeSchema = new NumberSchema();
                } else if (attribute.type().equals(Double.TYPE)) {
                    attributeSchema = new NumberSchema();
                } else if (attribute.type().equals(Boolean.class)) {
                    attributeSchema = new BooleanSchema();
                } else if (attribute.type().equals(Boolean.TYPE)) {
                    attributeSchema = new BooleanSchema();
                } else if (attribute.type().equals(String.class)) {
                    attributeSchema = new StringSchema();
                } else if (attribute.type().equals(BigDecimal.class)) {
                    attributeSchema = new NumberSchema();
                } else if (attribute.type() instanceof Class<?> clazz && clazz.isEnum()) {
                    attributeSchema = new EnumSchema((Class<? extends Enum>) clazz);
                } else if (attribute.type().equals(URI.class)) {
                    attributeSchema = new StringSchema()
                            .format("uri")
                            .example("https://example.com");
                } else if (attribute.type().equals(ZonedDateTime.class)) {
                    attributeSchema = new StringSchema()
                            .format("date-time")
                            .example("2023-10-01T12:00:00Z");
                } else if (attribute.type().equals(LocalDate.class)) {
                    attributeSchema = new StringSchema()
                            .format("date")
                            .example("2023-10-01");
                } else if (attribute.type().equals(UUID.class)) {
                    attributeSchema = new UUIDSchema();
                } else if (attribute.type().equals(Class.class)) {
                    attributeSchema = new StringSchema()
                            .format("class")
                            .description("Fully qualified class name of the data type")
                            .example("com.example.MyDataType");
                } else if (attribute.type().equals(ByteArray.class)) {
                    attributeSchema = new ByteArraySchema()
                            .format("binary")
                            .description("Binary data in base64 encoding");
                } else if (attribute.type().equals(YearMonth.class)) {
                    attributeSchema = new StringSchema()
                            .format("date")
                            .pattern("YYYY-MM")
                            .example("2023-10");
                } else {
                    log.warn("Unsupported attribute type: " + attribute.type());
                    attributeSchema = new Schema<>();
                }
                if (attribute.required()) {
                    attributeSchema.nullable(false);
                } else {
                    attributeSchema.nullable(true);
                }
                dataSchema.addProperty(attributeName, attributeSchema);
            });
            Schema<Object> arrayOfData = new ArraySchema()
                    .items(dataSchema)
                    .description("Array of " + dataType.name() + " objects");

            final PathItem getCountPathItem = getPathItemForGetCount(dataType, recordStoreApiTag,
                    recordDataTag);
            String getCountPath = pathResolver.resolveGetCountPath(dataType.dataClass());
            paths.addPathItem(getCountPath, getCountPathItem);


            final PathItem getAllPathItem = getPathItemForGetAll(dataType, arrayOfData, recordStoreApiTag,
                    recordDataTag);
            String getAllPath = pathResolver.resolveGetAllPath(dataType.dataClass());
            paths.addPathItem(getAllPath, getAllPathItem);

            final PathItem getAllWithPaginationPathItem = getPathItemForGetAllWithPagination(dataType, arrayOfData,
                    recordStoreApiTag,
                    recordDataTag);
            String getAllWithPaginationPath = pathResolver.resolveGetAllWithPaginationPathBase(dataType.dataClass());
            paths.addPathItem(getAllWithPaginationPath, getAllWithPaginationPathItem);
        });
        openAPI.paths(paths);
        return openAPI;
    }

    private static PathItem getPathItemForGetAll(DataType<?> dataType, Schema<Object> arrayOfData,
            Tag recordStoreApiTag,
            Tag recordDataTag) {
        ApiResponse okResponse = new ApiResponse()
                .description("Successful response with array of " + dataType.name())
                .content(new Content()
                        .addMediaType("application/json",
                                new MediaType().schema(arrayOfData)));

        ApiResponses apiResponses = new ApiResponses()
                .addApiResponse("200", okResponse);

        Operation operation = new Operation()
                .summary("Get all " + dataType.name())
                .operationId("getAll" + dataType.name())
                .description("Retrieves all records of type " + dataType.name())
                .responses(apiResponses);

        if (dataType.api()) {
            operation.addTagsItem(recordStoreApiTag.getName());
        } else {
            operation.addTagsItem(recordDataTag.getName());
        }
        if (!dataType.publiclyAvailable()) {
            operation.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
        }

        PathItem pathItem = new PathItem()
                .get(operation);
        return pathItem;
    }

    private static PathItem getPathItemForGetCount(DataType<?> dataType, Tag recordStoreApiTag,
            Tag recordDataTag) {
        ApiResponse okResponse = new ApiResponse()
                .description("Successful response with array of " + dataType.name())
                .content(new Content()
                        .addMediaType("application/json",
                                new MediaType().schema(new IntegerSchema())));

        ApiResponses apiResponses = new ApiResponses()
                .addApiResponse("200", okResponse);

        Operation operation = new Operation()
                .summary("Get count of " + dataType.name())
                .operationId("getCount" + dataType.name())
                .description("Retrieves count of all records of type " + dataType.name())
                .responses(apiResponses);

        if (dataType.api()) {
            operation.addTagsItem(recordStoreApiTag.getName());
        } else {
            operation.addTagsItem(recordDataTag.getName());
        }
        if (!dataType.publiclyAvailable()) {
            operation.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
        }

        PathItem pathItem = new PathItem()
                .get(operation);
        return pathItem;
    }

    private static PathItem getPathItemForGetAllWithPagination(DataType<?> dataType, Schema<Object> arrayOfData,
            Tag recordStoreApiTag,
            Tag recordDataTag) {

        Parameter pageParam = new Parameter()
                .name("page")
                .in("query")
                .required(false)
                .schema(new IntegerSchema().minimum(new BigDecimal(1)))
                .description("Page number (starting from 0)");

        Parameter pageSizeParam = new Parameter()
                .name("pageSize")
                .in("query")
                .required(false)
                .schema(new IntegerSchema().minimum(new BigDecimal(1)))
                .description("Number of items per page");

        ObjectSchema schema = new ObjectSchema();
        schema.addProperty("content", arrayOfData)
                .addProperty("nextPage", new StringSchema())
                .description("URL to the next page of results, if available")
                .nullable(true);

        ApiResponse okResponse = new ApiResponse()
                .description("Successful response with array of " + dataType.name())
                .content(new Content()
                        .addMediaType("application/json",
                                new MediaType().schema(schema)));

        ApiResponses apiResponses = new ApiResponses()
                .addApiResponse("200", okResponse);

        Operation operation = new Operation()
                .summary("Get all " + dataType.name())
                .operationId("getAll" + dataType.name())
                .description("Retrieves all records of type " + dataType.name())
                .addParametersItem(pageParam)
                .addParametersItem(pageSizeParam)
                .responses(apiResponses);

        if (dataType.api()) {
            operation.addTagsItem(recordStoreApiTag.getName());
        } else {
            operation.addTagsItem(recordDataTag.getName());
        }
        if (!dataType.publiclyAvailable()) {
            operation.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
        }

        PathItem pathItem = new PathItem()
                .get(operation);
        return pathItem;
    }
}
