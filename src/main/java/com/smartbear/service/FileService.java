package com.smartbear.service;

import com.smartbear.model.CreateFileRequest;
import com.smartbear.model.CreateFileResponse;
import com.smartbear.model.SearchFilesResponse;
import com.smartbear.model.entity.File;
import com.smartbear.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    
    private final FileRepository fileRepository;
    private final TagRepository tagRepository;

    public CreateFileResponse createFile(CreateFileRequest createFileRequest) {
        File newFile = newFile(createFileRequest);
        fileRepository.save(newFile);
        CreateFileResponse createFileResponse = new CreateFileResponse();
        createFileResponse.setUuid(newFile.getUuid().toString());
        return createFileResponse;
    }

    private File newFile(CreateFileRequest createFileRequest) {
        File file = new File();
        file.setName(createFileRequest.getName());
        file.setUuid(UUID.randomUUID());
        file.setTags(String.join("", createFileRequest.getTags()));
        createFileRequest.getTags().stream().map(tag -> {
            Tag t = new Tag();
            t.set
        })
        return file;
    }

    public SearchFilesResponse search(String tagSearchQuery, String page) {
        return null;
    }
}
