package com.openelements.data.sample.maven;

import com.openelements.data.data.AttributeType;
import com.openelements.data.data.DataAttribute;
import com.openelements.data.data.DataType;
import com.openelements.data.data.I18nString;
import java.time.YearMonth;
import java.util.List;

public class MavenPluginDownloadEntityDataTypeFactory {

    public static DataType<MavenPluginDownloadEntity> createDataType() {
        final DataAttribute<MavenPluginDownloadEntity, String> pluginAttribute = new DataAttribute<>("plugin",
                I18nString.of("Plugin"),
                I18nString.of("The name of the plugin"),
                AttributeType.STRING,
                MavenPluginDownloadEntity::getPlugin);
        final DataAttribute<MavenPluginDownloadEntity, YearMonth> timeAttribute = new DataAttribute<>("time",
                I18nString.of("Month"),
                I18nString.of("The month of the entry"),
                AttributeType.YEAR_MONTH,
                MavenPluginDownloadEntity::getTime);
        final DataAttribute<MavenPluginDownloadEntity, Integer> downloadAttribute = new DataAttribute<>("downloadCount",
                I18nString.of("Downloads"),
                I18nString.of("The download count"),
                AttributeType.NUMBER,
                MavenPluginDownloadEntity::getDownloadCount);
        return new DataType("mavenPluginDownload", "Maven Plugin downloads over time", MavenPluginDownloadEntity.class,
                List.of(pluginAttribute, timeAttribute, downloadAttribute));
    }
}
