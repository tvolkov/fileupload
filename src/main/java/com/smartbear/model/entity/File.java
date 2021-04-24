package com.smartbear.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Set;

@Data
public class File {

    @Id
    private String id;
    private String uuid;
    private String name;
    private Set<String> tags;
}
