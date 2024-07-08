package com.jsbr.repository.resolver;

import com.jsbr.repository.FieldSource;
import jakarta.persistence.Entity;
import java.lang.reflect.Field;

public class DefaultFieldSourceResolver extends AbstractFieldSourceResolver {

    @Override
    protected boolean support(Object entity, String fieldName, Object fieldValue, Field field) {
        if (fieldValue == null) {
            return true;
        }

        return fieldValue.getClass().getAnnotation(Entity.class) == null;
    }

    @Override
    public FieldSource resolveField(Object entity, String snakeFieldName, Object fieldValue, Field field) {
        return new FieldSource(snakeFieldName, fieldValue);
    }

}
