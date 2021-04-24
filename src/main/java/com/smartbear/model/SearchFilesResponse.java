package com.smartbear.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SearchFilesResponse {

    @JsonProperty("total_records")
    private Integer totalRecords;

    @JsonProperty("related_tags")
    private List<RelatedTag> relatedTags;

    @JsonProperty("files")
    private List<File> files;

    @Data
    public static class File {
        @JsonProperty("uuid")
        private final String uuid;

        @JsonProperty("name")
        private final String name;
    }

    @Data
    public static class RelatedTag {
        @JsonProperty("tag")
        private final String tag;

        @JsonProperty("file_count")
        private final Long fileCount;
    }
}
