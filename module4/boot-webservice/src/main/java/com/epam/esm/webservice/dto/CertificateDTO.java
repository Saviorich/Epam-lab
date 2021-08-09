package com.epam.esm.webservice.dto;

import com.epam.esm.webservice.entity.Tag;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class CertificateDTO extends RepresentationModel<CertificateDTO> {

    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private int duration;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateDate;
    private List<TagDTO> tags = new ArrayList<>();

    public CertificateDTO() {
    }

    public CertificateDTO(Integer id, String name, String description, BigDecimal price, int duration,
                          Date createDate, Date lastUpdateDate, List<TagDTO> tags) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.createDate = createDate;
        this.lastUpdateDate = lastUpdateDate;
        this.tags = tags;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CertificateDTO{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", price=").append(price);
        sb.append(", duration=").append(duration);
        sb.append(", createDate=").append(createDate);
        sb.append(", lastUpdateDate=").append(lastUpdateDate);
        sb.append(", tags=").append(tags);
        sb.append('}');
        return sb.toString();
    }
}
