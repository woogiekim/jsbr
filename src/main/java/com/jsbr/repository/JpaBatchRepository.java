package com.jsbr.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface JpaBatchRepository<T, ID> extends JpaRepository<T, ID> {

    <S extends T> List<S> saveAllInBatch(Iterable<S> entities);

}
