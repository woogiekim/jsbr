package com.jsbr.repository.resolver;

import com.jsbr.repository.FieldSource;
import jakarta.persistence.DiscriminatorValue;
import java.lang.reflect.Field;

public class InheritanceFieldSourceResolver extends AbstractFieldSourceResolver {

    @Override
    protected boolean support(Object entity, String fieldName, Object fieldValue, Field field) {
        return entity.getClass().getAnnotation(DiscriminatorValue.class) != null;
    }

    @Override
    public FieldSource resolveField(Object entity, String snakeFieldName, Object fieldValue, Field field) {
        var discriminatorValue = entity.getClass().getAnnotation(DiscriminatorValue.class);

        return new FieldSource("dtype", discriminatorValue.value());
    }

}
