package com.epam.esm.webservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Certificate {

    private int id;
    private String name;
    private String description;
    private BigDecimal price;
    private int duration;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createDate;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateDate;

    private List<Tag> tags = new ArrayList<>();

    public Certificate() {
    }

    public Certificate(int id, String name, String description, BigDecimal price,
                       int duration, Date createDate, Date lastUpdateDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.createDate = createDate;
        this.lastUpdateDate = lastUpdateDate;
    }

    public Certificate(String name, String description, BigDecimal price,
                       int duration, Date createDate, Date lastUpdateDate) {
        this(0, name, description, price, duration, createDate, lastUpdateDate);
    }

    public Certificate(String name, String description, BigDecimal price, int duration) {
        this(name, description, price, duration, null, null);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = new ArrayList<>(tags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Certificate that = (Certificate) o;

        if (id != that.id) return false;
        if (duration != that.duration) return false;
        if (!name.equals(that.name)) return false;
        if (!description.equals(that.description)) return false;
        if (!price.equals(that.price)) return false;
        if (!createDate.equals(that.createDate)) return false;
        if (!lastUpdateDate.equals(that.lastUpdateDate)) return false;
        return tags.equals(that.tags);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + price.hashCode();
        result = 31 * result + duration;
        result = 31 * result + createDate.hashCode();
        result = 31 * result + lastUpdateDate.hashCode();
        result = 31 * result + tags.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Certificate{");
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
