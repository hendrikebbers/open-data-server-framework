package com.openelements.data.sample.maven;

import com.openelements.data.data.AttributeType;
import com.openelements.data.data.DataAttribute;
import com.openelements.data.data.DataType;
import com.openelements.data.data.I18nString;
import java.time.YearMonth;
import java.util.List;

public class MavenCoreDownloadEntityDataTypeFactory {

    public static DataType<MavenCoreDownloadEntity> createDataType() {
        final DataAttribute<MavenCoreDownloadEntity, String> versionAttribute = new DataAttribute<>("version",
                I18nString.of("Version"),
                I18nString.of("The Maven Core version"),
                AttributeType.STRING,
                MavenCoreDownloadEntity::getVersion);
        final DataAttribute<MavenCoreDownloadEntity, YearMonth> timeAttribute = new DataAttribute<>("time",
                I18nString.of("Month"),
                I18nString.of("The month of the entry"),
                AttributeType.YEAR_MONTH,
                MavenCoreDownloadEntity::getTime);
        final DataAttribute<MavenCoreDownloadEntity, Integer> downloadAttribute = new DataAttribute<>("downloadCount",
                I18nString.of("Downloads"),
                I18nString.of("The download count"),
                AttributeType.NUMBER,
                MavenCoreDownloadEntity::getDownloadCount);
        return new DataType("mavenCoreDownload", "Maven Core downloads over time", MavenCoreDownloadEntity.class,
                List.of(versionAttribute, timeAttribute, downloadAttribute));
    }
}