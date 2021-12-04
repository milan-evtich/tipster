package com.milan.tipster.convert;

import com.milan.tipster.util.DateUtils;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.time.LocalDate;

public class LocalDateToStringOrikaConverter extends BidirectionalConverter<String, LocalDate> {

    @Override
    public LocalDate convertTo(String source, Type<LocalDate> destinationType, MappingContext mappingContext) {
        return DateUtils.parse(source);
    }

    @Override
    public String convertFrom(LocalDate source, Type<String> destinationType, MappingContext mappingContext) {
        return DateUtils.format(source);
    }
}
