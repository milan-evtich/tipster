package com.milan.tipster.convert;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class StringToEnumOrikaConverter extends BidirectionalConverter<String, Enum> {

    @Override
    public Enum convertTo(String source, Type<Enum> destinationType, MappingContext mappingContext) {
        return Enum.valueOf(destinationType.getRawType(), source);
    }

    @Override
    public String convertFrom(Enum source, Type<String> destinationType, MappingContext mappingContext) {
        return source.name();
    }

}