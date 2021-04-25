package com.smartbear.model.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@Document(collection = "files")
@Builder
public class File {

    @Id
    private String id;
    private String uuid;
    private String name;
    private Set<String> tags;
}
