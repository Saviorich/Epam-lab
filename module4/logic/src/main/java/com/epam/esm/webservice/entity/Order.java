package com.epam.esm.webservice.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "`order`")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private Date date;
    private BigDecimal cost;
    @ManyToMany(fetch = EAGER, cascade = MERGE)
    @JoinTable(
            name = "order_gift_certificate",
            joinColumns = {@JoinColumn(name = "order_id")},
            inverseJoinColumns = {@JoinColumn(name = "gift_certificate_id")}
    )
    private List<Certificate> certificates;

    public Order() {
    }

    public Order(Integer id, User user, Date date, BigDecimal cost, List<Certificate> certificates) {
        this.id = id;
        this.user = user;
        this.date = date;
        this.cost = cost;
        this.certificates = certificates;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (id != null ? !id.equals(order.id) : order.id != null) return false;
        if (user != null ? !user.equals(order.user) : order.user != null) return false;
        if (date != null ? !date.equals(order.date) : order.date != null) return false;
        if (cost != null ? !cost.equals(order.cost) : order.cost != null) return false;
        return certificates != null ? certificates.equals(order.certificates) : order.certificates == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (cost != null ? cost.hashCode() : 0);
        result = 31 * result + (certificates != null ? certificates.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Order{");
        sb.append("id=").append(id);
        sb.append(", user=").append(user);
        sb.append(", date=").append(date);
        sb.append(", cost=").append(cost);
        sb.append('}');
        return sb.toString();
    }
}
