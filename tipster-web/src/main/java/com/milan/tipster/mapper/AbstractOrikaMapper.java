package com.milan.tipster.mapper;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.nonNull;
import static org.springframework.core.GenericTypeResolver.resolveTypeArguments;

public abstract class AbstractOrikaMapper<D, E> extends ConfigurableMapper {

    private final MapperFactory mapperFactory;
    private final Class<E> eClass;
    private final Class<D> dClass;

    public AbstractOrikaMapper(MapperFactory mapperFactory) {
        this.mapperFactory = mapperFactory;
        Class<?>[] classes = resolveTypeArguments(getClass(), AbstractOrikaMapper.class);
        this.dClass = (Class<D>) Objects.requireNonNull(classes)[0];
        this.eClass = (Class<E>) classes[1];
    }

    public D toDto(E entity) {
        return mapperFactory.getMapperFacade()
                .map(entity, dClass);
    }

    public Optional<D> toDto(Optional<E> entityOptional) {
        if (entityOptional.isPresent()) {
            E entity = entityOptional.get();
            return Optional.of(toDto(entity));
        } else {
            return Optional.empty();
        }
    }

    public E toEntity(D dto) {
        return mapperFactory.getMapperFacade()
                .map(dto, eClass);
    }

    public E toEntity(D dto, @NotNull Map<Object, Object> contextParamsMap) {
        MappingContext context = new MappingContext(contextParamsMap);
        return mapperFactory.getMapperFacade()
                .map(dto, eClass, context);
    }

    public List<D> toDtoList(Collection<E> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }
        return mapperFactory.getMapperFacade()
                .mapAsList(entities, dClass);
    }

    public List<D> toDtoListSorted(Collection<E> entities, @Nullable Comparator<? super D> comparator) {
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }
        List<D> dtos = mapperFactory.getMapperFacade()
                .mapAsList(entities, dClass);
        if (nonNull(comparator)) {
            dtos.sort(comparator);
        }
        return dtos;
    }

    public List<E> toEntityList(Collection<D> dtos) {
        if (CollectionUtils.isEmpty(dtos)) {
            return Collections.emptyList();
        }
        return mapperFactory.getMapperFacade()
                .mapAsList(dtos, eClass);
    }

    public Page<D> toDtoPage(Page<E> entityPage) {
        if (Objects.isNull(entityPage) || entityPage.isEmpty()) {
            return Page.empty();
        }
        return entityPage.map(this::toDto);
    }

    @Override
    public <S, D> D map(S sourceObject, Class<D> destinationClass) {
        return mapperFactory.getMapperFacade().map(sourceObject, destinationClass);
    }

    @Override
    public <S, D> void map(S sourceObject, D destinationObject) {
        mapperFactory.getMapperFacade().map(sourceObject, destinationObject);
    }

    @Override
    public <S, D> void map(S sourceObject, D destinationObject, MappingContext context) {
        mapperFactory.getMapperFacade().map(sourceObject, destinationObject, context);
    }

    @Override
    public <S, D> D map(S sourceObject, Class<D> destinationClass, MappingContext context) {
        return mapperFactory.getMapperFacade().map(sourceObject, destinationClass, context);
    }

    @Override
    public <S, D> List<D> mapAsList(Iterable<S> source, Class<D> destinationClass) {
        return mapperFactory.getMapperFacade().mapAsList(source, destinationClass);
    }

    @Override
    public <S, D> List<D> mapAsList(Iterable<S> source, Class<D> destinationClass, MappingContext context) {
        return mapperFactory.getMapperFacade().mapAsList(source, destinationClass, context);
    }

    @Override
    public <S, D> Set<D> mapAsSet(Iterable<S> source, Class<D> destinationClass) {
        return mapperFactory.getMapperFacade().mapAsSet(source, destinationClass);
    }

    @Override
    public <S, D> Set<D> mapAsSet(Iterable<S> source, Class<D> destinationClass, MappingContext context) {
        return mapperFactory.getMapperFacade().mapAsSet(source, destinationClass, context);
    }

    protected MapperFactory getMapperFactory() {
        return mapperFactory;
    }

}
