package com.epam.esm.webservice.service.impl;

import com.epam.esm.webservice.controller.impl.UserController;
import com.epam.esm.webservice.dto.RoleDTO;
import com.epam.esm.webservice.dto.UserDTO;
import com.epam.esm.webservice.entity.User;
import com.epam.esm.webservice.repository.UserRepository;
import com.epam.esm.webservice.service.UserService;
import com.epam.esm.webservice.service.exception.InvalidPageNumberException;
import com.epam.esm.webservice.service.exception.ResourceNotFoundException;
import com.epam.esm.webservice.service.exception.UserNotFoundException;
import com.epam.esm.webservice.util.Pagination;
import com.epam.esm.webservice.validator.PaginationValidator;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.epam.esm.webservice.util.Resource.CERTIFICATE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Log4j2
@Service
public class UserServiceImpl implements UserService {

    private static final String ORDER_RELATION_NAME = "orders";
    private static final RoleDTO DEFAULT_ROLE = new RoleDTO(2, "ROLE_USER");

    private final UserRepository repository;
    private final ModelMapper mapper;
    private final PaginationValidator validator;
    private final PasswordEncoder encoder;

    @Autowired
    public UserServiceImpl(UserRepository repository, ModelMapper mapper, PaginationValidator validator, PasswordEncoder encoder) {
        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
        this.encoder = encoder;
    }

    @Override
    public List<UserDTO> findAllByParameters(Pagination pagination) {
        if (!validator.isPaginationValid(pagination, repository.count())) {
            throw new InvalidPageNumberException("Invalid page number: " + pagination.getPage());
        }
        return repository.findAll(PageRequest.of(pagination.getPage() - 1, pagination.getLimit()))
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
    public void register(String email, String password) {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(email);
        userDTO.setHash(encoder.encode(password));
        userDTO.setRegistrationDate(new Date());
        userDTO.setRole(DEFAULT_ROLE);
        save(userDTO);
    }

    @Override
    public UserDTO findByEmail(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find user with email=" + email, CERTIFICATE));
        return addOrderRelLink(addSelfRelLink(convertToDTO(user)));
    }

    @Override
    public UserDTO findByEmailAndPassword(String email, String password) {
        UserDTO user = findByEmail(email);
        if (encoder.matches(password, user.getHash())) {
            return user;
        }
        return null;
    }

    @Override
    public void save(UserDTO resource) {
        repository.save(mapper.map(resource, User.class));
    }

    @Override
    public void deleteById(Integer id) {
        throw new UnsupportedOperationException("Delete operation for User is not implemented.");
    }

    @Override
    public Integer countAll() {
        return ((Long) repository.count()).intValue();
    }

    private UserDTO addSelfRelLink(UserDTO userDTO) {
        return userDTO.add(linkTo(UserController.class)
                .slash(userDTO.getId())
                .withSelfRel());
    }

    private UserDTO addOrderRelLink(UserDTO userDTO) {
        return userDTO.add(linkTo(UserController.class)
                .slash(userDTO.getId())
                .slash(ORDER_RELATION_NAME)
                .withRel(ORDER_RELATION_NAME));
    }

    public UserDTO convertToDTO(User user) {
        log.warn("Converting entity to DTO");
        return mapper.map(user, UserDTO.class);
    }
}
