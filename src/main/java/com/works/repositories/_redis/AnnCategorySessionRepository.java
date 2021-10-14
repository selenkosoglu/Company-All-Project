package com.works.repositories._redis;

import com.works.models._redis.AnnCategorySession;
import com.works.models._redis.SurveySession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@EnableRedisRepositories
public interface AnnCategorySessionRepository extends CrudRepository<AnnCategorySession, String> {

    @Query("select s from sessionanncategory s order by s.id")
    List<AnnCategorySession> findByOrderByIdAsc(Pageable pageable);

}
