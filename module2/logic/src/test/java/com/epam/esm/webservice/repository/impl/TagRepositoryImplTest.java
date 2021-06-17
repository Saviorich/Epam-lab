package com.epam.esm.webservice.repository.impl;

import com.epam.esm.webservice.DatabaseSetup;
import com.epam.esm.webservice.config.TestDBConfig;
import com.epam.esm.webservice.entity.Tag;
import com.epam.esm.webservice.repository.TagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Component
@ActiveProfiles(profiles = "test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestDBConfig.class}, loader = AnnotationConfigContextLoader.class)
class TagRepositoryImplTest {

    private TagRepository repository;

    @Autowired
    public TagRepositoryImplTest(DataSource dataSource) {
        repository = new TagRepositoryImpl(dataSource);
        new DatabaseSetup(dataSource)
                .createTables()
                .insertTags();
    }

    @ParameterizedTest
    @ValueSource(strings = {"racing", "food", "christmas"})
    void testSave_ShouldCreateNewTag(String name) {
        int id = 7;
        Tag tag = new Tag(id, name);
        assertFalse(repository.getById(id).isPresent());
        repository.save(tag);
        assertEquals(tag, repository.getById(id).get());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4})
    void testDelete_ShouldDeleteTagsById(int id) {
        assertTrue(repository.getById(id).isPresent());
        repository.delete(id);
        assertFalse(repository.getById(id).isPresent());
    }

    @ParameterizedTest
    @CsvSource({"1,game", "2,shooter", "3,moba", "4,singleplayer"})
    void testGetById_ShouldReturnTagById(int id, String name) {
        assertEquals(name, repository.getById(id).get().getName());
    }

    @Test
    void testGetAll_ShouldReturnAllTags() {
        List<Tag> expected = Arrays.asList(
                new Tag(1, "game"),
                new Tag(2, "shooter"),
                new Tag(3, "moba"),
                new Tag(4, "singleplayer"),
                new Tag(5, "multiplayer"),
                new Tag(6, "delivery"));
        assertEquals(expected, repository.getAll());
    }
}