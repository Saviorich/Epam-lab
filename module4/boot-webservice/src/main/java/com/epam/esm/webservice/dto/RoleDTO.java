package com.epam.esm.webservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDTO {
    @JsonIgnore
    private Integer id;
    private String role;

    public RoleDTO() {
    }

    public RoleDTO(Integer id, String role) {
        this.id = id;
        this.role = role;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RoleDTO{");
        sb.append("id=").append(id);
        sb.append(", role='").append(role).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
