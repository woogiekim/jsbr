package com.jsbr.core.repository.support.resolver;

import com.jsbr.core.repository.support.FieldSource;
import java.lang.reflect.Field;

public interface FieldSourceResolver {

    FieldSource resolve(Object entity, Field field);

}
