package com.milan.tipster.config;

import com.milan.tipster.convert.DateToLocalDateOrikaConverter;
import com.milan.tipster.convert.DurationToStringOrikaConverter;
import com.milan.tipster.convert.LocalDateTimeToStringOrikaConverter;
import com.milan.tipster.convert.LocalDateToStringOrikaConverter;
import com.milan.tipster.convert.StringToEnumOrikaConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MapperConfiguration {

    /**
     * Настройка фабрики для маперов
     */
    @Bean("globalMapperFactory")
    @Primary
    public MapperFactory globalMapperFactory() {
        DefaultMapperFactory mapperFactory = new DefaultMapperFactory.Builder()
                .mapNulls(false)
                .build();
        this.registerConverters(mapperFactory);
        return mapperFactory;
    }

    @Bean
    public MapperFacade mapperFacade() {
        return globalMapperFactory().getMapperFacade();
    }

    /**
     * Настройка конвертеров
     */
    private void registerConverters(DefaultMapperFactory mapperFactory) {
        mapperFactory.getConverterFactory().registerConverter(new LocalDateTimeToStringOrikaConverter());
        mapperFactory.getConverterFactory().registerConverter(new LocalDateToStringOrikaConverter());
        mapperFactory.getConverterFactory().registerConverter(new StringToEnumOrikaConverter());
        mapperFactory.getConverterFactory().registerConverter(new DateToLocalDateOrikaConverter());
        mapperFactory.getConverterFactory().registerConverter(new DurationToStringOrikaConverter());
    }

}
