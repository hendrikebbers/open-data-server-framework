package com.openelements.data.runtime.test;

import com.openelements.data.api.types.I18nString;
import com.openelements.data.runtime.DataLoader;
import com.openelements.data.runtime.DataType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DataLoaderTest {

    record TestData(Boolean active, I18nString description) {
    }

    @Test
    void test() {
        //given
        DataLoader loader = new DataLoader();

        //when
        final DataType load = loader.load(TestData.class);

        //then
        Assertions.assertNotNull(load);
    }
}
