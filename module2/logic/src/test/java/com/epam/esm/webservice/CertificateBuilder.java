package com.epam.esm.webservice;

import com.epam.esm.webservice.entity.Certificate;
import com.epam.esm.webservice.entity.Tag;

import java.math.BigDecimal;
import java.util.Date;

public class CertificateBuilder {

    private final Certificate certificate = new Certificate();

    public CertificateBuilder setId(int id) {
        certificate.setId(id);
        return this;
    }

    public CertificateBuilder setName(String name) {
        certificate.setName(name);
        return this;
    }

    public CertificateBuilder setDescription(String description) {
        certificate.setDescription(description);
        return this;
    }

    public CertificateBuilder setPrice(BigDecimal price) {
        certificate.setPrice(price);
        return this;
    }

    public CertificateBuilder setDuration(int duration) {
        certificate.setDuration(duration);
        return this;
    }

    public CertificateBuilder setCreateDate(Date createDate) {
        certificate.setCreateDate(createDate);
        return this;
    }

    public CertificateBuilder setLastUpdateDate(Date lastUpdateDate) {
        certificate.setLastUpdateDate(lastUpdateDate);
        return this;
    }

    public CertificateBuilder addTag(String name) {
        certificate.getTags().add(new Tag("name"));
        return this;
    }

    public Certificate build() {
        return certificate;
    }
}