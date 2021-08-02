package com.epam.esm.webservice.service.impl;

import com.epam.esm.webservice.config.ApplicationConfig;
import com.epam.esm.webservice.config.TestConfig;
import com.epam.esm.webservice.dto.CertificateDTO;
import com.epam.esm.webservice.dto.TagDTO;
import com.epam.esm.webservice.entity.Certificate;
import com.epam.esm.webservice.entity.Tag;
import com.epam.esm.webservice.repository.TagRepository;
import com.epam.esm.webservice.repository.impl.TagRepositoryImpl;
import com.epam.esm.webservice.repository.impl.UserRepositoryImpl;
import com.epam.esm.webservice.service.TagService;
import com.epam.esm.webservice.service.exception.InvalidPageNumberException;
import com.epam.esm.webservice.service.exception.ResourceNotFoundException;
import com.epam.esm.webservice.service.exception.TagNotFoundException;
import com.epam.esm.webservice.util.Pagination;
import com.epam.esm.webservice.validator.PaginationValidator;
import com.epam.esm.webservice.validator.impl.PaginationValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {ApplicationConfig.class})
@ContextConfiguration(classes = {ModelMapper.class, TagRepositoryImpl.class, PaginationValidatorImpl.class})
@ActiveProfiles("prod")
class TagServiceImplTest {

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private PaginationValidator validator;

    @Mock
    private TagRepository repository;

    private TagService service;

    @BeforeEach
    void setup() {
        service = new TagServiceImpl(repository, mapper, validator);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void testFindAllByParameters_ShouldReturnLimitedAmountOfEntries(int limit) {
        Pagination pagination = new Pagination(1, limit);
        Tag[] tags = new Tag[limit];
        for (int i = 0; i < limit; i++) {
            tags[i] = new Tag();
        }
        List<Tag> expected = asList(tags);
        when(repository.findAllByParams(pagination)).thenReturn(expected);
        assertEquals(expected.size(), service.findAllByParameters(pagination).size());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -2, -3, -4, 0})
    void testGetAllByParameters_ShouldThrowInvalidPageNumberExceptionForEachParameter(Integer page) {
        Pagination pagination = new Pagination(page, 5);
        assertThrows(InvalidPageNumberException.class, () -> service.findAllByParameters(pagination));
    }

    @Test
    void testGetById_ShouldReturnDto() {
        int id = 1;
        Tag expected = new Tag();
        when(repository.findById(id)).thenReturn(Optional.of(expected));
        assertEquals(mapper.map(expected, TagDTO.class), service.findById(id));
    }

    @Test
    void testGetById_ShouldThrowException() {
        when(repository.findById(1)).thenReturn(Optional.empty());
        assertThrows(TagNotFoundException.class, () -> service.findById(1));
    }

    @Test
    void testSave_ShouldConvertAndAdd() {
        int id = 1;
        Tag tag = new Tag();
        TagDTO expected = new TagDTO();
        assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
        when(repository.findById(id)).thenReturn(Optional.of(tag));
        doNothing().when(repository).add(tag);
        service.save(expected);
        assertEquals(expected, service.findById(id));
    }


    @Test
    void testDelete_ShouldDoNothing() {
        int id = 1;
        when(repository.findById(id)).thenReturn(Optional.of(new Tag())).thenReturn(Optional.empty());
        assertNotNull(service.findById(id));
        service.deleteById(id);
        verify(repository).delete(id);
        assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
    }
}