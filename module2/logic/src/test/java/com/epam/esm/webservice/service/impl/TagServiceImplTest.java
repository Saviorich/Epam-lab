package com.epam.esm.webservice.service.impl;

import com.epam.esm.webservice.entity.Tag;
import com.epam.esm.webservice.repository.TagRepository;
import com.epam.esm.webservice.service.TagService;
import com.epam.esm.webservice.service.exception.TagNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    private TagService service;

    @Mock
    private TagRepository repository;

    @BeforeEach
    void setUp() {
        service = new TagServiceImpl(repository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"game", "shooter", "delivery"})
    void testSave_ShouldCreateNewTag(String name) {
        int id = 1;
        Tag expected = new Tag(name);
        when(repository.getById(id))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(expected));
        assertThrows(TagNotFoundException.class, () -> service.getById(id));
        service.save(expected);
        verify(repository, times(1)).save(expected);
        assertEquals(expected, service.getById(id));
    }

    @ParameterizedTest
    @ValueSource(strings = {"game", "shooter", "delivery"})
    void testDelete_ShouldDeleteTags(String name) {
        int id = 1;
        Tag tag = new Tag(id, name);
        when(repository.getById(id))
                .thenReturn(Optional.of(tag))
                .thenReturn(Optional.empty());
        assertEquals(tag, service.getById(id));
        service.delete(id);
        verify(repository).delete(id);
        assertThrows(TagNotFoundException.class, () -> service.getById(id));
    }

    @Test
    void testGetAll_ShouldReturnAllTags() {
        List<Tag> expected = Arrays.asList(new Tag("game"), new Tag("delivery"));
        when(repository.getAll()).thenReturn(expected);
        assertIterableEquals(expected, service.getAll());
    }

    @Test
    void testGetById_ShouldThrowException() {
        when(repository.getById(anyInt())).thenReturn(Optional.empty());
        assertThrows(TagNotFoundException.class, () -> service.getById(anyInt()));
    }
}