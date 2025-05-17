module com.openelements.data.runtime {

    requires com.openelements.data.api;

    uses com.openelements.data.runtime.DataAttributeTypeSupport;

    provides com.openelements.data.runtime.DataAttributeTypeSupport with
            com.openelements.data.runtime.impl.PrimitiveIntegerSupport,
            com.openelements.data.runtime.impl.IntegerSupport,
            com.openelements.data.runtime.impl.PrimitiveBooleanSupport,
            com.openelements.data.runtime.impl.BooleanSupport,
            com.openelements.data.runtime.impl.CharSequenceSupport,
            com.openelements.data.runtime.impl.PrimitiveDoubleSupport,
            com.openelements.data.runtime.impl.DoubleSupport,
            com.openelements.data.runtime.impl.PrimitiveLongSupport,
            com.openelements.data.runtime.impl.LongSupport,
            com.openelements.data.runtime.impl.LocalDateSupport,
            com.openelements.data.runtime.impl.LocalTimeSupport,
            com.openelements.data.runtime.impl.LocalDateTimeSupport,
            com.openelements.data.runtime.impl.ZonedDateTimeSupport,
            com.openelements.data.runtime.impl.YearMonthSupport;

}