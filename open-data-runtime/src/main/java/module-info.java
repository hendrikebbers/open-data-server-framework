module com.openelements.data.runtime {

    requires com.openelements.data.api;

    uses com.openelements.data.runtime.DataAttributeTypeSupport;

    provides com.openelements.data.runtime.DataAttributeTypeSupport with
            com.openelements.data.runtime.impl.BooleanSupport;

}