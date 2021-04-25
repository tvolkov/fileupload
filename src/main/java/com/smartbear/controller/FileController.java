package com.smartbear.controller;

import com.smartbear.model.CreateFileRequest;
import com.smartbear.model.CreateFileResponse;
import com.smartbear.model.SearchFilesResponse;
import com.smartbear.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@Validated
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/file", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateFileResponse> createFile(@RequestBody CreateFileRequest createFileRequest){
        return ResponseEntity.ok(fileService.createFile(createFileRequest));
    }

    @GetMapping(value = "/files/{tag_search_query}/{page}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchFilesResponse> searchFiles(@PathVariable("tag_search_query") @Pattern(regexp = "[+\\-][a-zA-Z0-9]+") String tagSearchQuery,
                                                           @PathVariable("page") @Pattern(regexp = "\\d") int page){
        return ResponseEntity.ok(fileService.search(tagSearchQuery, page));
    }
}
