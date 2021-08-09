package com.epam.esm.webservice.service.impl;

import com.epam.esm.webservice.controller.impl.TagController;
import com.epam.esm.webservice.dto.TagDTO;
import com.epam.esm.webservice.entity.Tag;
import com.epam.esm.webservice.repository.TagRepository;
import com.epam.esm.webservice.service.TagService;
import com.epam.esm.webservice.service.exception.InvalidPageNumberException;
import com.epam.esm.webservice.service.exception.MostUsedTagNotFoundException;
import com.epam.esm.webservice.service.exception.TagNotFoundException;
import com.epam.esm.webservice.util.Pagination;
import com.epam.esm.webservice.validator.PaginationValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Service
public class TagServiceImpl implements TagService {

    private static final Logger logger = LogManager.getLogger();

    private TagRepository repository;
    private ModelMapper mapper;
    private PaginationValidator validator;

    @Autowired
    public TagServiceImpl(TagRepository repository, ModelMapper modelMapper, PaginationValidator validator) {
        this.repository = repository;
        this.mapper = modelMapper;
        this.validator = validator;
    }

    @Override
    public List<TagDTO> findAllByParameters(Pagination pagination) {
        if (!validator.isPaginationValid(pagination, repository.count())) {
            throw new InvalidPageNumberException("Invalid page number: " + pagination.getPage());
        }
        return repository.findAll(PageRequest.of(pagination.getPage() - 1, pagination.getLimit())).stream()
                .map(this::convertToDto)
                .map(this::addSelfRelLink)
                .collect(Collectors.toList());
    }

    @Override
    public TagDTO findById(Integer id) {
        Tag tag = repository.findById(id)
                .orElseThrow(() -> new TagNotFoundException(id));
        return addSelfRelLink(convertToDto(tag));
    }

    @Override
    public TagDTO findMostUsedTagOfUserWithHighestCostOfAllOrders() {
        Tag tag = repository.findMostUsedTagOfUserWithHighestCostOfAllOrders()
                .orElseThrow(MostUsedTagNotFoundException::new);
        return convertToDto(tag);
    }

    @Override
    public void save(TagDTO tag) {
        logger.warn("Saving tag");
        Tag tagToSave = mapper.map(tag, Tag.class);
        repository.save(tagToSave);
        logger.warn("Tag saved");
    }

    @Override
    public void deleteById(Integer id) {
        logger.warn("Deleting tag with id={}", id);
        repository.deleteById(id);
        logger.warn("Tag deleted");
    }

    @Override
    public Integer countAll() {
        return ((Long) repository.count()).intValue();
    }

    private TagDTO  addSelfRelLink(TagDTO tagDTO) {
        return tagDTO.add(linkTo(TagController.class).slash(tagDTO.getId()).withSelfRel());
    }

    private TagDTO convertToDto(Tag tag) {
        logger.warn("Converting entity to DTO");
        return mapper.map(tag, TagDTO.class);
    }
}
