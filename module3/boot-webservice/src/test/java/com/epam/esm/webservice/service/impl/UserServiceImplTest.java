package com.epam.esm.webservice.service.impl;

import com.epam.esm.webservice.config.ApplicationConfig;
import com.epam.esm.webservice.config.TestConfig;
import com.epam.esm.webservice.dto.UserDTO;
import com.epam.esm.webservice.entity.User;
import com.epam.esm.webservice.repository.UserRepository;
import com.epam.esm.webservice.repository.impl.UserRepositoryImpl;
import com.epam.esm.webservice.service.UserService;
import com.epam.esm.webservice.service.exception.InvalidPageNumberException;
import com.epam.esm.webservice.service.exception.UserNotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ApplicationConfig.class)
@ContextConfiguration(classes = { ModelMapper.class, UserRepositoryImpl.class, PaginationValidatorImpl.class})
@ActiveProfiles("prod")
class UserServiceImplTest {

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private PaginationValidator validator;

    @Mock
    private UserRepository repository;

    private UserService service;

    @BeforeEach
    void setup() {
        service = new UserServiceImpl(repository, mapper, validator);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void testGetAllByParameters_ShouldReturnLimitedAmountOfEntries(int limit) {
        Pagination pagination = new Pagination(1, limit);
        User[] users = new User[limit];
        for (int i = 0; i < limit; i++) {
            users[i] = new User();
        }
        List<User> expected = asList(users);
        when(repository.findAllByParameters(pagination)).thenReturn(expected);
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
        User expected = new User();
        when(repository.findById(id)).thenReturn(Optional.of(expected));
        assertEquals(mapper.map(expected, UserDTO.class), service.findById(id));
    }

    @Test
    void testGetById_ShouldThrowResourceNotFoundException() {
        when(repository.findById(1)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.findById(1));
    }
}