package com.smartbear.repository;

import com.smartbear.model.entity.File;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FileRepository extends MongoRepository<File, String> {
//    db.files.find( { $and: [ { tags: { $all: ["tag2", "tag3"] } }, { tags: { $nin: ["tag4" ] } } ] } );
    @Query("{ $and: [ { tags: { $all: ?0 } }, { tags: { $nin: ?1 } } ] }")
    List<File> findFilesByTags(Set<String> plusTags, Set<String> minusTags);

    @Query("{ tags: { $in: ?0 } }")
    List<File> findFilesByInclusionTags(Set<String> inclusionTags);

    @Query("{ tags: { $nin: ?0 } }")
    List<File> getFilesByExclusionTags(Set<String> exclusionTags);
}
