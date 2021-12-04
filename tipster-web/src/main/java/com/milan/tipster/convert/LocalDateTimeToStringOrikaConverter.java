package com.milan.tipster.convert;

import com.milan.tipster.util.DateTimeUtils;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.time.LocalDateTime;

public class LocalDateTimeToStringOrikaConverter extends BidirectionalConverter<String, LocalDateTime> {

    @Override
    public LocalDateTime convertTo(String s, Type<LocalDateTime> type, MappingContext mappingContext) {
        return DateTimeUtils.parse(s);
    }

    @Override
    public String convertFrom(LocalDateTime localDateTime, Type<String> type, MappingContext mappingContext) {
        return DateTimeUtils.format(localDateTime);
    }

}
