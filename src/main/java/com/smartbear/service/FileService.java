package com.smartbear.service;

import com.smartbear.model.CreateFileRequest;
import com.smartbear.model.CreateFileResponse;
import com.smartbear.model.SearchFilesResponse;
import com.smartbear.model.entity.File;
import com.smartbear.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private static final Function<File, SearchFilesResponse.File> MAPPER_FUNCTION = f -> new SearchFilesResponse.File(f.getUuid(), f.getName());

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
        Set<String> inclusionTags = new HashSet<>();
        Set<String> exclusionTags = new HashSet<>();
        StringTokenizer stringTokenizer = new StringTokenizer(tagSearchQuery, "+-", true);
        while (stringTokenizer.hasMoreTokens()){
            String nextToken = stringTokenizer.nextToken();
            log.debug(nextToken);
            if ("+".equals(nextToken)){
                inclusionTags.add(stringTokenizer.nextToken());
            } else {
                exclusionTags.add(stringTokenizer.nextToken());
            }
        }
        log.info("exclustion tags: {}, inclusion tags: {}", exclusionTags, inclusionTags);
        List<File> filesByTags;

        if (exclusionTags.isEmpty()) {
            filesByTags = fileRepository.findFilesByInclusionTags(inclusionTags);
        } else if (inclusionTags.isEmpty()){
            filesByTags = fileRepository.getFilesByExclusionTags(exclusionTags);
        } else {
            filesByTags = fileRepository.findFilesByTags(inclusionTags, exclusionTags);
        }

        SearchFilesResponse searchFilesResponse = new SearchFilesResponse();
        searchFilesResponse.setTotalRecords(filesByTags.size());
        searchFilesResponse.setFiles(filesByTags.stream().map(MAPPER_FUNCTION).collect(Collectors.toList()));
        searchFilesResponse.setRelatedTags(collectRelatedTags(filesByTags, combineTags(inclusionTags, exclusionTags)));
        return searchFilesResponse;
    }

    private static Set<String> combineTags(Set<String> inclusionTags, Set<String> exclusionTags) {
        Set<String> result = new HashSet<>();
        result.addAll(inclusionTags);
        result.addAll(exclusionTags);
        return result;
    }

    private List<SearchFilesResponse.RelatedTag> collectRelatedTags(List<File> filesByTags, Set<String> tagsInQuery) {
        Map<String, Long> tagsSummary = new HashMap<>();
        for (File f : filesByTags) {
            Set<String> tagsForFile = new HashSet<>(f.getTags());
            tagsForFile.removeAll(tagsInQuery);
            for (String tag : tagsForFile) {
                tagsSummary.computeIfAbsent(tag, tagKey -> filesByTags.stream().filter(file -> file.getTags().contains(tagKey)).count());
            }
        }

        return tagsSummary.entrySet().stream().map(entry -> new SearchFilesResponse.RelatedTag(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    }
}
