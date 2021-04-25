package com.smartbear.service;

import com.smartbear.exception.InvalidPageException;
import com.smartbear.exception.InvalidQueryException;
import com.smartbear.model.CreateFileRequest;
import com.smartbear.model.CreateFileResponse;
import com.smartbear.model.SearchFilesResponse;
import com.smartbear.model.entity.File;
import com.smartbear.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
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

    @Value("${page.size}")
    private int pageSize;

    public CreateFileResponse createFile(CreateFileRequest createFileRequest) {
        File file = newFile(createFileRequest);
        fileRepository.save(file);
        return new CreateFileResponse(file.getUuid());
    }

    private File newFile(CreateFileRequest createFileRequest) {
        return File.builder()
                .name(createFileRequest.getName())
                .uuid(UUID.randomUUID().toString())
                .tags(new HashSet<>(createFileRequest.getTags()))
                .build();
    }

    public SearchFilesResponse search(String tagSearchQuery) {
        return search(tagSearchQuery, "");
    }

    public SearchFilesResponse search(String tagSearchQuery, String pageStr) {
        if (!tagSearchQuery.matches("[+\\-a-zA-Z0-9]+")) {
            throw new InvalidQueryException(tagSearchQuery);
        }

        int page;
        if (pageStr.isEmpty() || pageStr.isBlank()) {
            page = -1;
        } else {
            try {
                page = Integer.parseInt(pageStr);
            } catch (NumberFormatException e) {
                throw new InvalidPageException(pageStr);
            }
        }

        Set<String> inclusionTags = new HashSet<>();
        Set<String> exclusionTags = new HashSet<>();
        StringTokenizer stringTokenizer = new StringTokenizer(tagSearchQuery, "+-", true);
        while (stringTokenizer.hasMoreTokens()) {
            String nextToken = stringTokenizer.nextToken();
            log.debug(nextToken);
            if ("+" .equals(nextToken)) {
                inclusionTags.add(stringTokenizer.nextToken());
            } else {
                exclusionTags.add(stringTokenizer.nextToken());
            }
        }
        log.info("exclustion tags: {}, inclusion tags: {}", exclusionTags, inclusionTags);
        List<File> filesByTags;

        if (exclusionTags.isEmpty()) {
            filesByTags = fileRepository.findFilesByInclusionTags(inclusionTags);
        } else if (inclusionTags.isEmpty()) {
            filesByTags = fileRepository.getFilesByExclusionTags(exclusionTags);
        } else {
            if (page >= 0)
                filesByTags = fileRepository.findFilesByTagsPaginated(inclusionTags, exclusionTags, PageRequest.of(page, pageSize));
            else
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
                tagsSummary.computeIfAbsent(tag, tagKey -> filesByTags
                        .stream()
                        .filter(file -> file.getTags().contains(tagKey))
                        .count());
            }
        }

        return tagsSummary
                .entrySet()
                .stream()
                .map(entry -> new SearchFilesResponse.RelatedTag(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
