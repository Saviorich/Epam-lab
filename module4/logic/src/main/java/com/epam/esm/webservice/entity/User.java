package com.epam.esm.webservice.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "user")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;
    private String email;
    private String hash;
    @Column(name = "registration_date")
    private Date registrationDate;
    @OneToOne
    @JoinColumn(name = "role_id")
    private Role role;
    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    public User() {
    }

    public User(Integer id, String email, String hash, Date registrationDate) {
        this.id = id;
        this.email = email;
        this.hash = hash;
        this.registrationDate = registrationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!id.equals(user.id)) return false;
        if (!email.equals(user.email)) return false;
        if (!hash.equals(user.hash)) return false;
        return registrationDate.equals(user.registrationDate);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + hash.hashCode();
        result = 31 * result + registrationDate.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append(", email='").append(email).append('\'');
        sb.append(", hash='").append(hash).append('\'');
        sb.append(", registrationDate=").append(registrationDate);
        sb.append('}');
        return sb.toString();
    }
}
