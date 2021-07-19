package com.epam.esm.webservice.dto;

import com.epam.esm.webservice.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Setter
public class OrderDTO extends RepresentationModel<OrderDTO> {

    private Integer id;
    private UserDTO user;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;
    private BigDecimal cost;
    private List<CertificateDTO> certificates;

    public OrderDTO() {
    }

    public OrderDTO(Integer id, UserDTO user, Date date, BigDecimal cost, List<CertificateDTO> certificates) {
        this.id = id;
        this.user = user;
        this.date = date;
        this.cost = cost;
        this.certificates = certificates;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OrderDTO{");
        sb.append("id=").append(id);
        sb.append(", user=").append(user);
        sb.append(", date=").append(date);
        sb.append(", cost=").append(cost);
        sb.append(", certificates").append(certificates);
        sb.append('}');
        return sb.toString();
    }
}
