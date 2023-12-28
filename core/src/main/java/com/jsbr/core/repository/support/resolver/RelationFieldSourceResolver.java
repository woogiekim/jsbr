package com.jsbr.core.repository.support.resolver;

import static com.jsbr.common.support.Strings.toSnakeCase;

import com.jsbr.core.repository.support.FieldSource;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Predicate;
import org.apache.commons.lang3.reflect.FieldUtils;

public class RelationFieldSourceResolver extends AbstractFieldSourceResolver {

    @Override
    protected boolean support(Object entity, String fieldName, Object fieldValue, Field field) {
        return fieldValue != null && fieldValue.getClass().getAnnotation(Entity.class) != null;
    }

    @Override
    public FieldSource resolveField(Object entity, String snakeFieldName, Object fieldValue, Field field) {
        try {
            var predicateId = (Predicate<Field>) it -> it.getAnnotation(Id.class) != null;

            var relationField = Arrays.stream(fieldValue.getClass().getDeclaredFields())
                                      .filter(predicateId)
                                      .findFirst()
                                      .orElseGet(() ->
                                              Arrays.stream(fieldValue.getClass().getSuperclass().getDeclaredFields())
                                                    .filter(predicateId)
                                                    .findFirst()
                                                    .orElseThrow()
                                      );
            var relationFieldName = toSnakeCase(snakeFieldName + "_" + relationField.getName());

            return new FieldSource(relationFieldName, FieldUtils.readField(fieldValue, relationField.getName(), true));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
