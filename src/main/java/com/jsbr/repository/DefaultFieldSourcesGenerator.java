package com.jsbr.repository;

import com.jsbr.repository.resolver.FieldSourceResolver;
import jakarta.persistence.Column;
import java.util.List;
import org.apache.commons.lang3.reflect.FieldUtils;

public class DefaultFieldSourcesGenerator implements FieldSourcesGenerator {

    private final List<FieldSourceResolver> resolvers;

    public DefaultFieldSourcesGenerator(List<FieldSourceResolver> resolvers) {
        this.resolvers = resolvers;
    }

    @Override
    public FieldSources generate(Object entity) {
        var sources = FieldSources.newInstance();

        for (var field : FieldUtils.getFieldsListWithAnnotation(entity.getClass(), Column.class)) {
            for (FieldSourceResolver resolver : resolvers) {
                var source = resolver.resolve(entity, field);

                if (source.isEmpty()) {
                    continue;
                }

                sources.add(source);
            }
        }

        return sources;
    }

}
