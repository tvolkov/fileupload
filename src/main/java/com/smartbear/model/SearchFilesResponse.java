package com.smartbear.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

public class SearchFilesResponse {

    @JsonProperty("total_records")
    private Integer totalRecords;

    @JsonProperty("related_tags")
    private List<RelatedTag> relatedTags;

    @JsonProperty("files")
    private List<File> files;

    @Data
    static class File {
        @JsonProperty("uuid")
        private String uuid;

        @JsonProperty("name")
        private String name;
    }

    @Data
    static class RelatedTag {
        @JsonProperty("tag")
        private String tag;

        @JsonProperty("file_count")
        private Integer fileCount;
    }
}
