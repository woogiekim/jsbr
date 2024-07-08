package com.jsbr.repository.resolver;

import com.jsbr.repository.FieldSource;
import java.lang.reflect.Field;

public interface FieldSourceResolver {

    FieldSource resolve(Object entity, Field field);

}
