package com.epam.esm.webservice.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
public class TagDTO extends RepresentationModel<TagDTO> {

    private Integer id;
    private String name;

    public TagDTO() {
    }

    public TagDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
