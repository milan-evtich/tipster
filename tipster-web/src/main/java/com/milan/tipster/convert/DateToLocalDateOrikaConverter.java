package com.milan.tipster.convert;

import com.milan.tipster.util.DateTimeUtils;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.time.LocalDate;
import java.util.Date;

public class DateToLocalDateOrikaConverter extends BidirectionalConverter<Date, LocalDate> {

    @Override
    public LocalDate convertTo(Date date, Type<LocalDate> type, MappingContext mappingContext) {
        return DateTimeUtils.convertDateToLocalDate(date);
    }

    @Override
    public Date convertFrom(LocalDate localDate, Type<Date> type, MappingContext mappingContext) {
        return DateTimeUtils.convertLocalDateToDate(localDate);
    }

}
