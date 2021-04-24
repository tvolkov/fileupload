package com.smartbear.service;

import com.smartbear.model.CreateFileRequest;
import com.smartbear.model.CreateFileResponse;
import com.smartbear.model.SearchFilesResponse;
import com.smartbear.model.entity.File;
import com.smartbear.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Service
@RequiredArgsConstructor
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
//todo add validation for search query
        Map<Boolean, Set<String>> queriesMap = Arrays.stream(tagSearchQuery.split(" "))
                .collect(Collectors.partitioningBy(s -> s.startsWith("+"),
                        Collectors.mapping(tag -> tag.substring(1), Collectors.toSet())));
        List<File> filesByTags = Collections.emptyList();
        if (queriesMap.get(Boolean.FALSE).size() == 0) {
            filesByTags = fileRepository.findFilesByInclusionTags(queriesMap.get(TRUE));
        } else if (queriesMap.get(TRUE).size() == 0){
            filesByTags = fileRepository.getFilesByExclusionTags(queriesMap.get(FALSE));
        } else {
            filesByTags = fileRepository.findFilesByTags(queriesMap.get(true), queriesMap.get(false));
        }

        SearchFilesResponse searchFilesResponse = new SearchFilesResponse();
        searchFilesResponse.setTotalRecords(filesByTags.size());
        searchFilesResponse.setFiles(filesByTags.stream().map(MAPPER_FUNCTION).collect(Collectors.toList()));
        searchFilesResponse.setRelatedTags(collectRelatedTags(filesByTags, combineTags(queriesMap)));
        return null;
    }

    private static Set<String> combineTags(Map<Boolean, Set<String>> queriesMap) {
        return queriesMap.values().stream().reduce(new HashSet<>(), (accumulatorSet, nextSet) -> {
            accumulatorSet.addAll(nextSet);
            return accumulatorSet;
        });
    }

    private List<SearchFilesResponse.RelatedTag> collectRelatedTags(List<File> filesByTags, Set<String> tagsInQuery) {
        List<SearchFilesResponse.RelatedTag> result = new ArrayList<>();
        for (File f : filesByTags) {
            Set<String> tagsExcluded = new HashSet<>(f.getTags());
            tagsExcluded.removeAll(tagsInQuery);
            for (String tag : tagsExcluded) {
                result.add(new SearchFilesResponse.RelatedTag(tag, filesByTags.stream().filter(file -> file.getTags().contains(tag)).count()));
            }
        }
        return result;
    }
}
