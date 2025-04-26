package com.openelements.data.sample.maven;

import com.openelements.data.provider.DataProviderContext;
import com.openelements.data.provider.EntityUpdatesProvider;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

public class MavenCoreDownloadEntityProvider implements EntityUpdatesProvider<MavenCoreDownloadEntity> {

    @Override
    public Set<MavenCoreDownloadEntity> loadUpdatedData(DataProviderContext context) {
        Set<MavenCoreDownloadEntity> result = new HashSet<>();
        List<Year> years = IntStream.range(2022, Year.now().plusYears(1).getValue())
                .mapToObj(v -> Year.of(v))
                .toList();
        List<Month> months = Arrays.asList(Month.values());
        List<YearMonth> yearMonths = new ArrayList<>();
        years.forEach(year -> {
            months.forEach(month -> yearMonths.add(YearMonth.of(year.getValue(), month.getValue())));
        });
        yearMonths.forEach(yearMonth -> {
            final String filename =
                    "apache-maven-stats-" + yearMonth.getYear() + "-" + yearMonth.getMonthValue() + ".csv";
            final URL fileUrl = getClass().getResource(filename);
            if (fileUrl != null) {
                try (final InputStream inputStream = fileUrl.openStream()) {
                    final CSVFormat format = CSVFormat.DEFAULT;
                    final CSVParser parser = CSVParser.parse(inputStream, Charset.defaultCharset(), format);
                    parser.stream()
                            .forEach(rec -> {
                                MavenCoreDownloadEntity entity = new MavenCoreDownloadEntity();
                                entity.setTime(yearMonth);
                                entity.setVersion(rec.get(0));
                                entity.setDownloadCount(Integer.parseInt(rec.get(1)));
                                result.add(entity);
                            });
                } catch (final Exception e) {
                    throw new RuntimeException("Error in fetching maven statistics", e);
                }
            }

        });
        return Collections.unmodifiableSet(result);
    }

}
