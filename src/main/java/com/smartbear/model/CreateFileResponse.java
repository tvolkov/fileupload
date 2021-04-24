package com.smartbear.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateFileResponse {
    @JsonProperty("uuid")
    private final String uuid;

}
