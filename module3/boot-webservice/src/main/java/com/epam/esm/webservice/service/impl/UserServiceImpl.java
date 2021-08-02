package com.epam.esm.webservice.service.impl;

import com.epam.esm.webservice.controller.impl.UserController;
import com.epam.esm.webservice.dto.UserDTO;
import com.epam.esm.webservice.entity.User;
import com.epam.esm.webservice.repository.UserRepository;
import com.epam.esm.webservice.service.UserService;
import com.epam.esm.webservice.service.exception.InvalidPageNumberException;
import com.epam.esm.webservice.service.exception.UserNotFoundException;
import com.epam.esm.webservice.util.Pagination;
import com.epam.esm.webservice.validator.PaginationValidator;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Log4j2
@Service
public class UserServiceImpl implements UserService {

    private static final String ORDER_RELATION_NANE = "orders";

    private final UserRepository repository;
    private final ModelMapper mapper;
    private final PaginationValidator validator;

    @Autowired
    public UserServiceImpl(UserRepository repository, ModelMapper mapper, PaginationValidator validator) {
        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
    }

    @Override
    public List<UserDTO> findAllByParameters(Pagination pagination) {
        if (!validator.isPaginationValid(pagination, repository.countTotalEntries())) {
            throw new InvalidPageNumberException("Invalid page number: " + pagination.getPage());
        }
        return repository.findAllByParameters(pagination)
                .stream()
                .map(this::convertToDTO)
                .map(this::addSelfRelLink)
                .map(this::addOrderRelLink)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO findById(Integer id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return addOrderRelLink(addSelfRelLink(convertToDTO(user)));
    }

    @Override
    public void save(UserDTO resource) {
        throw new UnsupportedOperationException("Save operation for User is not implemented.");
    }

    @Override
    public void deleteById(Integer id) {
        throw new UnsupportedOperationException("Delete operation for User is not implemented.");
    }

    @Override
    public Integer countAll() {
        return repository.countTotalEntries();
    }

    private UserDTO addSelfRelLink(UserDTO userDTO) {
        return userDTO.add(linkTo(UserController.class)
                .slash(userDTO.getId())
                .withSelfRel());
    }

    private UserDTO addOrderRelLink(UserDTO userDTO) {
        return userDTO.add(linkTo(UserController.class)
                .slash(userDTO.getId())
                .slash(ORDER_RELATION_NANE)
                .withRel(ORDER_RELATION_NANE));
    }

    public UserDTO convertToDTO(User user) {
        log.warn("Converting entity to DTO");
        return mapper.map(user, UserDTO.class);
    }
}
