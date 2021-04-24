package com.smartbear.service;

import com.smartbear.model.CreateFileRequest;
import com.smartbear.model.CreateFileResponse;
import com.smartbear.model.SearchFilesResponse;
import com.smartbear.model.entity.File;
import com.smartbear.repository.FileRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {
    
    private final FileRepository fileRepository;

    public CreateFileResponse createFile(CreateFileRequest createFileRequest) {
        File file = newFile(createFileRequest);
        fileRepository.save(file);
        return new CreateFileResponse(file.getUuid());
    }

    private File newFile(CreateFileRequest createFileRequest) {
        File file = new File();
        file.setName(createFileRequest.getName());
        file.setUuid(UUID.randomUUID().toString());
        file.setTags(new HashSet<>(createFileRequest.getTags()));
        return file;
    }

    public SearchFilesResponse search(String tagSearchQuery, String page) {
//todo add validation for serach query
        Map<Boolean, Set<String>> collect = Arrays.stream(tagSearchQuery.split(" ")).collect(Collectors.partitioningBy(s -> s.startsWith("+"), Collectors.toSet()));
        List<File> filesByTags = fileRepository.findFilesByTags(collect.get(true), collect.get(false));
//        fileRepository.findFilesByTags()
        return null;
    }

    @AllArgsConstructor
    public static class PlusKey {
        public String key;
    }
    @AllArgsConstructor
    public static class MinusKey {
        public String key;
    }
    public static void main(String[] args) {


        Map<Boolean, List<String>> collect1 = Arrays.stream(tagSearchQuery.split(" ")).collect(Collectors.partitioningBy(s -> s.startsWith("+"), Collectors.toSet()));
    }
}
