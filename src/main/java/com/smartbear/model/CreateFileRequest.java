package com.smartbear.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CreateFileRequest {

    @JsonProperty("name")
    private final String name;

    @JsonProperty("tags")
    private final List<String> tags;
}
