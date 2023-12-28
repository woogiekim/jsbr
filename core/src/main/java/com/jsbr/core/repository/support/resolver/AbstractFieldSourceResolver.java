package com.jsbr.core.repository.support.resolver;

import static com.jsbr.common.support.Strings.toSnakeCase;

import com.jsbr.core.repository.support.FieldSource;
import java.lang.reflect.Field;
import org.apache.commons.lang3.reflect.FieldUtils;

public abstract class AbstractFieldSourceResolver implements FieldSourceResolver {

    protected abstract boolean support(Object entity, String fieldName, Object fieldValue, Field field);

    public abstract FieldSource resolveField(Object entity, String snakeFieldName, Object fieldValue, Field field);

    @Override
    public FieldSource resolve(Object entity, Field field) {
        try {
            var fieldName = field.getName();
            var fieldValue = FieldUtils.readField(entity, fieldName, true);

            if (!support(entity, fieldName, fieldValue, field)) {
                return FieldSource.empty();
            }

            return resolveField(entity, toSnakeCase(fieldName), fieldValue, field);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
