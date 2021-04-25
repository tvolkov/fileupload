package com.smartbear.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class CreateFileRequest {

    @JsonProperty("name")
    private String name;

    @JsonProperty("tags")
    private List<String> tags;
}
