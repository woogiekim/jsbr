package com.jsbr.repository;

import com.jsbr.repository.resolver.DefaultFieldSourceResolver;
import com.jsbr.repository.resolver.InheritanceFieldSourceResolver;
import com.jsbr.repository.resolver.RelationFieldSourceResolver;
import jakarta.persistence.EntityManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import static com.jsbr.support.Queries.generateMultiInsertQuery;
import static com.jsbr.support.Queries.generateMultiUpdateQuery;
import static com.jsbr.support.Strings.toSnakeCase;

public class SimpleJpaBatchRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements
        JpaBatchRepository<T, ID>, JpaRepository<T, ID> {

    private final JpaEntityInformation<T, ?> entityInformation;
    private final EntityManager entityManager;

    private final FieldSourcesGenerator fieldSourcesGenerator;

    public SimpleJpaBatchRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);

        this.entityInformation = entityInformation;
        this.entityManager = entityManager;

        this.fieldSourcesGenerator = new DefaultFieldSourcesGenerator(List.of(
                new DefaultFieldSourceResolver(), new RelationFieldSourceResolver(), new InheritanceFieldSourceResolver()
        ));
    }

    @Override
    public <S extends T> List<S> saveAllInBatch(Iterable<S> entities) {
        var fieldNames = (Set<String>) null;
        var insertSourceGroup = new HashMap<S, FieldSources>();
        var updateSourceGroup = new HashMap<S, FieldSources>();

        var clazz = (Class<?>) null;
        var idName = (String) null;

        for (var entity : entities) {
            if (clazz == null) {
                clazz = entity.getClass();
            }

            if (entityInformation.getIdAttribute() != null && idName == null) {
                idName = entityInformation.getIdAttribute().getName();
            }

            var fieldSources = fieldSourcesGenerator.generate(entity);

            if (entityInformation.isNew(entity)) {
                fieldSources.remove(idName);

                insertSourceGroup.put(entity, fieldSources);
            } else {
                updateSourceGroup.put(entity, fieldSources);
            }

            if (fieldNames == null) {
                fieldNames = fieldSources.toNameSet();
            }
        }

        var tableName = toSnakeCase(entityInformation.getEntityName());

        var result = new ArrayList<S>();

        if (!insertSourceGroup.isEmpty()) {
            var insertSourceGroups = insertSourceGroup.values().stream().map(FieldSources::toMap).toList();
            var multiInsertQuery = generateMultiInsertQuery(tableName, idName, fieldNames, insertSourceGroups);

            var insertResults = entityManager.createNativeQuery(multiInsertQuery, clazz).getResultList();

            //noinspection unchecked
            result.addAll(insertResults);
        }

        if (!updateSourceGroup.isEmpty()) {
            var updateSourceGroups = updateSourceGroup.values().stream().map(FieldSources::toMap).toList();
            var multiUpdateQuery = generateMultiUpdateQuery(tableName, idName, fieldNames, updateSourceGroups);

            entityManager.createNativeQuery(multiUpdateQuery).executeUpdate();

            result.addAll(updateSourceGroup.keySet());
        }

        return result;
    }

}
