package com.milan.tipster.convert;

import com.milan.tipster.util.DateTimeUtils;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.time.Duration;

public class DurationToStringOrikaConverter extends BidirectionalConverter<String, Duration> {

    @Override
    public Duration convertTo(String source, Type<Duration> destinationType, MappingContext mappingContext) {
        return DateTimeUtils.parseDuration(source);
    }

    @Override
    public String convertFrom(Duration source, Type<String> destinationType, MappingContext mappingContext) {
        return DateTimeUtils.formatDuration(source);
    }
}
