package com.openelements.data.internal;

import com.openelements.data.data.AttributeType;
import com.openelements.data.data.DataAttribute;
import com.openelements.data.data.DataType;
import com.openelements.data.data.I18nString;
import com.openelements.data.db.FileEntity;
import com.openelements.data.server.internal.HashUtil;
import java.util.List;

public class FileEntityDataTypeFactory {

    public static DataType<FileEntity> createDataType() {
        return new DataType<>(
                "FileEntity",
                "An entity that represents a file.",
                FileEntity.class,
                List.of(
                        new DataAttribute<>("name",
                                I18nString.of("Name"),
                                I18nString.of("The name of the file"),
                                AttributeType.STRING,
                                FileEntity::getName),
                        new DataAttribute<>("mediaType",
                                I18nString.of("Media Type"),
                                I18nString.of("The media type of the file"),
                                AttributeType.STRING,
                                e -> e.getContentType().getContentType()),
                        new DataAttribute<>("size",
                                I18nString.of("Size"),
                                I18nString.of("The size in bytes"),
                                AttributeType.NUMBER,
                                FileEntity::getContentSize),
                        new DataAttribute<>("hash",
                                I18nString.of("Hash"),
                                I18nString.of("The SHA-256 hash of the file"),
                                AttributeType.STRING,
                                e -> HashUtil.bytesToHex(e.getHash())),
                        new DataAttribute<>("url",
                                I18nString.of("URL"),
                                I18nString.of("The url to download the File"),
                                AttributeType.FILE_URL,
                                FileEntity::getId)
                )
        );
    }
}
