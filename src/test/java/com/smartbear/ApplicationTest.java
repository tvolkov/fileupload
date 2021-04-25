package com.smartbear;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbear.model.CreateFileRequest;
import com.smartbear.model.CreateFileResponse;
import com.smartbear.model.SearchFilesResponse;
import com.smartbear.model.entity.File;
import com.smartbear.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Testcontainers
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class ApplicationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FileRepository fileRepository;

    @LocalServerPort
    protected int port;

    @BeforeEach
    void initDb(){
        fileRepository.deleteAll();
        insertTestFilesToDb();
    }

    @Test
    void testCreateFile() throws Exception {
        var createFileRequest = new CreateFileRequest("file1", Arrays.asList("tag1", "tag2"));

        String createFileResponseStr = mockMvc.perform(MockMvcRequestBuilders
                .post("http://localhost:{port}/file", port)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createFileRequest))
        ).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        CreateFileResponse createFileResponse = objectMapper.readValue(createFileResponseStr, CreateFileResponse.class);
        assertNotNull(createFileResponse.getUuid());
    }

    @Test
    void testSearchFiles() throws Exception {
        String searchFileResponseStr = mockMvc.perform(MockMvcRequestBuilders
        .get("http://localhost:{port}/files/+tag2+tag3-tag4/0", port)
        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        SearchFilesResponse searchFilesResponse = objectMapper.readValue(searchFileResponseStr, SearchFilesResponse.class);

        assertEquals(1, searchFilesResponse.getTotalRecords());
        assertEquals(1, searchFilesResponse.getFiles().size());
        assertEquals("file1", searchFilesResponse.getFiles().get(0).getName());
        assertEquals(2, searchFilesResponse.getRelatedTags().size());
        assertEquals("tag1", searchFilesResponse.getRelatedTags().get(0).getTag());
        assertEquals("tag5", searchFilesResponse.getRelatedTags().get(1).getTag());

        searchFileResponseStr = mockMvc.perform(MockMvcRequestBuilders
                .get("http://localhost:{port}/files/+tag2+tag3-tag4/1", port)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        searchFilesResponse = objectMapper.readValue(searchFileResponseStr, SearchFilesResponse.class);

        assertEquals(1, searchFilesResponse.getTotalRecords());
        assertEquals(1, searchFilesResponse.getFiles().size());
        assertEquals("file3", searchFilesResponse.getFiles().get(0).getName());
        assertEquals(1, searchFilesResponse.getRelatedTags().size());
        assertEquals("tag5", searchFilesResponse.getRelatedTags().get(0).getTag());
    }

    @Test
    void testSearchAllFiles() throws Exception {
        var searchFileResponseStr = mockMvc.perform(MockMvcRequestBuilders
                .get("http://localhost:{port}/files/+tag2+tag3-tag4", port)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        var searchFilesResponse = objectMapper.readValue(searchFileResponseStr, SearchFilesResponse.class);

        assertEquals(2, searchFilesResponse.getTotalRecords());
        assertEquals(2, searchFilesResponse.getFiles().size());
        assertEquals("file1", searchFilesResponse.getFiles().get(0).getName());
        assertEquals("file3", searchFilesResponse.getFiles().get(1).getName());
        assertEquals(2, searchFilesResponse.getRelatedTags().size());
        assertEquals("tag1", searchFilesResponse.getRelatedTags().get(0).getTag());
        assertEquals("tag5", searchFilesResponse.getRelatedTags().get(1).getTag());
    }

    @Test
    void testSearchFilesWithInclusionTagsOnly() throws Exception {
        String searchFileResponseStr = mockMvc.perform(MockMvcRequestBuilders
                .get("http://localhost:{port}/files/+tag2+tag3", port)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        SearchFilesResponse searchFilesResponse = objectMapper.readValue(searchFileResponseStr, SearchFilesResponse.class);

        assertEquals(3, searchFilesResponse.getTotalRecords());
        assertEquals(3, searchFilesResponse.getFiles().size());
        assertEquals("file1", searchFilesResponse.getFiles().get(0).getName());
        assertEquals("file3", searchFilesResponse.getFiles().get(1).getName());
        assertEquals("file4", searchFilesResponse.getFiles().get(2).getName());
        assertEquals(3, searchFilesResponse.getRelatedTags().size());
        assertEquals("tag1", searchFilesResponse.getRelatedTags().get(0).getTag());
        assertEquals("tag4", searchFilesResponse.getRelatedTags().get(1).getTag());
        assertEquals("tag5", searchFilesResponse.getRelatedTags().get(2).getTag());
    }

    @Test
    void testSearchFilesWithExclusionTagsOnly() throws Exception {
        String searchFileResponseStr = mockMvc.perform(MockMvcRequestBuilders
                .get("http://localhost:{port}/files/-tag2-tag3", port)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        SearchFilesResponse searchFilesResponse = objectMapper.readValue(searchFileResponseStr, SearchFilesResponse.class);

        assertEquals(0, searchFilesResponse.getTotalRecords());
        assertEquals(0, searchFilesResponse.getFiles().size());
        assertEquals(0, searchFilesResponse.getRelatedTags().size());
    }

    private void insertTestFilesToDb() {
        fileRepository.save(
                File.builder()
                .name("file1")
                .uuid(UUID.randomUUID().toString())
                .tags(new HashSet<>(Arrays.asList("tag1", "tag2", "tag3", "tag5")))
                .build());

        fileRepository.save(
                File.builder()
                        .name("file2")
                        .uuid(UUID.randomUUID().toString())
                        .tags(new HashSet<>(Arrays.asList("tag2")))
                        .build());

        fileRepository.save(
                File.builder()
                        .name("file3")
                        .uuid(UUID.randomUUID().toString())
                        .tags(new HashSet<>(Arrays.asList("tag2", "tag3", "tag5")))
                        .build());

        fileRepository.save(
                File.builder()
                        .name("file4")
                        .uuid(UUID.randomUUID().toString())
                        .tags(new HashSet<>(Arrays.asList("tag2", "tag3", "tag4", "tag5")))
                        .build());

        fileRepository.save(
                File.builder()
                        .name("file5")
                        .uuid(UUID.randomUUID().toString())
                        .tags(new HashSet<>(Arrays.asList("tag3", "tag4")))
                        .build());
    }
}