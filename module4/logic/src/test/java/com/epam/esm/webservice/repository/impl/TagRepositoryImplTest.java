package com.epam.esm.webservice.repository.impl;

import com.epam.esm.webservice.config.TestConfig;
import com.epam.esm.webservice.entity.Tag;
import com.epam.esm.webservice.entity.User;
import com.epam.esm.webservice.repository.TagRepository;
import com.epam.esm.webservice.util.Pagination;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = TestConfig.class)
@ContextConfiguration(classes = TagRepository.class)
@ActiveProfiles("test")
class TagRepositoryImplTest {

    @Autowired
    private TagRepository repository;

    @ParameterizedTest
    @CsvSource({"1,1", "1,2", "1,3", "1,4", "1,5"})
    void testFindAllByParameters_ShouldReturnLimitedAmount(Integer page, Integer limit) {
        Pagination pagination = new Pagination(page, limit);
        assertEquals((long) limit, repository.findAll(PageRequest.of(pagination.getPage() - 1, pagination.getLimit())).getTotalElements());
    }

    @Test
    void testFindById_ShouldReturnExistingValue() {
        String expectedName = "game";
        assertEquals(expectedName, repository.findById(1).get().getName());
    }

    @Test
    void testFindById_ShouldNotPresent() {
        assertFalse(repository.findById(152).isPresent());
    }


    @Test
    void testSave_ShouldCreateNewTag() {
        int id = 9;
        Tag tag = new Tag("racing");
        assertFalse(repository.findById(id).isPresent());
        repository.save(tag);
        assertEquals(tag, repository.findById(id).get());
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5})
    void testDelete_ShouldDeleteTagsById(int id) {
        assertTrue(repository.findById(id).isPresent());
        repository.deleteById(id);
        assertFalse(repository.findById(id).isPresent());
    }

    @Test
    void countTotalEntries() {
        assertEquals(8, repository.count());
    }
}