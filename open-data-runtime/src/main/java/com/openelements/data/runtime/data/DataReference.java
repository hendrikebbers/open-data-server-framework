package com.openelements.data.runtime.data;

import java.util.UUID;
import org.jspecify.annotations.NonNull;

public interface DataReference {

    @NonNull
    UUID id();
}
