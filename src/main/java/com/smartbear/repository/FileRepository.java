package com.smartbear.repository;

import com.smartbear.model.entity.File;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FileRepository extends MongoRepository<File, String> {
//    db.files.find( { $and: [ { tags: { $all: ["tag2", "tag3"] } }, { tags: { $nin: ["tag4" ] } } ] } );
    @Query("{ $and: [ { tags: { $all: ?0 } }, { tags: { $nin: ?1 } } ] }")
    List<File> findByTags(@Param("plusTags") Set<String> plusTags,
                          @Param("minusTags") Set<String> minusTags);
}
