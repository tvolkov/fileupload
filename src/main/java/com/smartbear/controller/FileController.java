package com.smartbear.controller;

import com.smartbear.model.CreateFileRequest;
import com.smartbear.model.CreateFileResponse;
import com.smartbear.model.SearchFilesResponse;
import com.smartbear.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@Validated
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/file", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateFileResponse> createFile(@RequestBody CreateFileRequest createFileRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(fileService.createFile(createFileRequest));
    }

    @GetMapping(value = "/files/{tag_search_query}/{page}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchFilesResponse> searchFilesPaginated(@PathVariable("tag_search_query") String tagSearchQuery,
                                                           @PathVariable("page") String page){
        return ResponseEntity.ok(fileService.search(tagSearchQuery, page));
    }

    @GetMapping(value = "/files/{tag_search_query}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchFilesResponse> searchFiles(@PathVariable("tag_search_query") String tagSearchQuery){
        return ResponseEntity.ok(fileService.search(tagSearchQuery));
    }
}