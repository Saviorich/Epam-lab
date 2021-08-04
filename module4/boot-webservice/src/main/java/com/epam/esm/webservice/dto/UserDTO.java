package com.epam.esm.webservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Setter
public class UserDTO extends RepresentationModel<UserDTO> {

    private Integer id;
    private String email;
    @JsonIgnore
    private String hash;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date registrationDate;
    private RoleDTO role;

    public UserDTO() {
    }

    public UserDTO(Integer id, String email, String hash, Date registrationDate, RoleDTO role) {
        this.id = id;
        this.email = email;
        this.hash = hash;
        this.registrationDate = registrationDate;
        this.role = role;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserDTO{");
        sb.append("id=").append(id);
        sb.append(", email='").append(email).append('\'');
        sb.append(", hash='").append(hash).append('\'');
        sb.append(", registrationDate=").append(registrationDate);
        sb.append(", role=").append(role);
        sb.append('}');
        return sb.toString();
    }
}
