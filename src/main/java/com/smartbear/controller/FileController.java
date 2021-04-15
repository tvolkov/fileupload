package com.smartbear.controller;

import com.smartbear.model.CreateFileResponse;
import com.smartbear.model.CreateFileRequest;
import com.smartbear.model.SearchFilesResponse;
import com.smartbear.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController("/api/v1")
public class FileController {

    @Autowired
    private FileService fileService;

    @RequestMapping(path = "/file", method = RequestMethod.POST)
    public ResponseEntity<CreateFileResponse> createFile(@RequestBody CreateFileRequest createFileRequest){
        return ResponseEntity.ok(fileService.createFile(createFileRequest));
    }

    @RequestMapping(path = "/files/{tag_search_query}/{page}", method = RequestMethod.GET)
    public ResponseEntity<SearchFilesResponse> searchFiles(@PathVariable("tag_search_query") String tagSearchQuery,
                                                           @PathVariable("page") String page){
        return ResponseEntity.ok(fileService.search(tagSearchQuery, page));
    }
}
