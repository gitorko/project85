package com.demo.project85;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;

public class MapperHelper<E, M> {
    public MapperHelper(Class<E> entityType, Class<M> modelType) {
        this.entityType = entityType;
        this.modelType = modelType;
        this.mapper = new ModelMapper();
    }

    public E toEntity(M model) {
        return mapper.map(model, entityType);
    }

    public M toModel(E entity) {
        return mapper.map(entity, modelType);
    }

    public Page<M> toPagedModel(Page<E> entities) {
        return entities.map(this::toModel);
    }

    public List<M> toListModel(List<E> source) {
        return source
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    Class<E> entityType;
    Class<M> modelType;
    ModelMapper mapper;
}
