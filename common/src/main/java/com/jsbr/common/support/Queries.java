package com.jsbr.common.support;

import static com.jsbr.common.support.Strings.lenientFormat;
import static org.apache.commons.lang3.StringUtils.containsAnyIgnoreCase;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Queries {

    public static String generateMultiInsertQuery(
            String tableName,
            String idName,
            Set<String> fieldSources,
            List<Map<String, Object>> sourceGroups
    ) {
        var builder = new StringBuilder();

        builder.append(lenientFormat("insert into {} ", tableName));

        var fieldNames = fieldSources.stream()
                                     .filter(source -> !source.equals(idName))
                                     .collect(Collectors.joining(",", "(", ")"));

        builder.append(fieldNames);
        builder.append(" values ");

        var fieldValues = generateValues(sourceGroups);

        builder.append(fieldValues);
        builder.append(" returning *");

        return builder.toString();
    }

    public static <S> String generateMultiUpdateQuery(
            String tableName,
            String idName,
            Set<String> fieldSources,
            List<Map<String, Object>> sourceGroups
    ) {
        var builder = new StringBuilder();

        builder.append(lenientFormat("update {} target set ", tableName));

        var fieldSetExtractor = (Function<String, String>) fieldName -> {
            var correctedFieldName = fieldName;

            if (containsAnyIgnoreCase(fieldName, "at", "time")) {
                correctedFieldName += "::::timestamp with time zone";
            }

            return lenientFormat("{} = source.{}", fieldName, correctedFieldName);
        };

        var fieldUpdateSets = fieldSources.stream()
                                          .filter(source -> !containsAnyIgnoreCase(source, idName, "save_time"))
                                          .map(fieldSetExtractor)
                                          .collect(Collectors.joining(", "));

        builder.append(fieldUpdateSets);

        var fieldNames = String.join(", ", fieldSources);
        var fieldValues = generateValues(sourceGroups);

        builder.append(lenientFormat(" from (values {}) as source ({}) ", fieldValues, fieldNames));
        builder.append(lenientFormat("where target.{} = source.{}", idName, idName));

        return builder.toString();
    }

    public static String generateValues(List<Map<String, Object>> sourceGroups) {
        return sourceGroups.stream()
                           .map(Map::values)
                           .map(values -> values
                                   .stream()
                                   .map(value -> {
                                       if (value == null) {
                                           return "null";
                                       }

                                       if (!(value instanceof Number) && !(value instanceof Boolean)) {
                                           return lenientFormat("'{}'", value.toString().replace("'", "''"));
                                       }

                                       return String.valueOf(value);
                                   })
                                   .collect(Collectors.joining(",", "(", ")"))
                           )
                           .collect(Collectors.joining(", "));
    }

}
