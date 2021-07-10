package com.epam.esm.webservice.service.impl;

import com.epam.esm.webservice.CertificateBuilder;
import com.epam.esm.webservice.entity.Certificate;
import com.epam.esm.webservice.entity.Tag;
import com.epam.esm.webservice.repository.CertificateRepository;
import com.epam.esm.webservice.service.CertificateService;
import com.epam.esm.webservice.service.exception.CertificateNotFoundException;
import com.epam.esm.webservice.util.SortBy;
import com.epam.esm.webservice.util.SortType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CertificateServiceImplTest {

    @Mock
    private static CertificateRepository repository;

    private CertificateService service;

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat(
            "yyyy-MM-dd");

    @BeforeEach
    void setup() {
        service = new CertificateServiceImpl(repository);
    }

    @Test
    void testSave_ShouldThrowException_WhenCertificateIsNotExists() {
        Certificate certificate = new Certificate();
        certificate.setId(1);
        assertThrows(CertificateNotFoundException.class, () -> service.save(certificate));
    }

    @Test
    void testSave_ShouldModifyExistingEntityInDatabase() throws ParseException {
        int id = 1;
        List<Tag> tags = Arrays.asList(new Tag("game"), new Tag("shooter"));
        Certificate expectedNotUpdated = new CertificateBuilder()
                .setId(id)
                .setName("Gift 1")
                .setDescription("Best gift certificate")
                .addTag("game")
                .setPrice(new BigDecimal("19.99"))
                .setDuration(10)
                .setCreateDate(FORMATTER.parse(FORMATTER.format(new Date())))
                .setLastUpdateDate(FORMATTER.parse(FORMATTER.format(new Date())))
                .build();
        Certificate expectedUpdated = new CertificateBuilder()
                .setId(id)
                .setName("Gift 1")
                .setDescription("Updated gift certificate")
                .setPrice(new BigDecimal("9.99"))
                .setDuration(10)
                .addTag("game")
                .addTag("shooter")
                .setCreateDate(FORMATTER.parse(FORMATTER.format(new Date())))
                .setLastUpdateDate(FORMATTER.parse(FORMATTER.format(new Date())))
                .build();

        when(repository.getById(id))
                .thenReturn(Optional.of(expectedNotUpdated))
                .thenReturn(Optional.of(expectedUpdated));

        assertEquals(expectedNotUpdated, service.getById(id));
        service.save(expectedUpdated);
        verify(repository).addTagsToCertificate(expectedUpdated);
        assertEquals(expectedUpdated, service.getById(id));
    }

    @Test
    void testSave_ShouldAddNewCertificateToDatabase() throws ParseException {
        int id = 5;
        Certificate expected = new CertificateBuilder()
                .setName("Gift 1")
                .setDescription("Best gift certificate")
                .addTag("game")
                .setPrice(new BigDecimal("19.99"))
                .setDuration(10)
                .addTag("game")
                .build();

        service.save(expected);

        expected.setId(id);
        expected.setCreateDate(FORMATTER.parse(FORMATTER.format(new Date())));
        expected.setLastUpdateDate(FORMATTER.parse(FORMATTER.format(new Date())));

        verify(repository).add(expected);
        when(repository.getById(id)).thenReturn(Optional.of(expected));
        assertEquals(expected, service.getById(id));
    }

    @Test
    void testDelete_WhenCertificateExists_ShouldDeleteAndReturnOptionalEmpty() {
        int id = 4;
        Certificate expected = new CertificateBuilder()
                .setName("")
                .setDescription("")
                .setPrice(new BigDecimal("1"))
                .setCreateDate(new Date())
                .setLastUpdateDate(new Date())
                .build();
        when(repository.getById(4))
                .thenReturn(Optional.of(expected))
                .thenReturn(Optional.empty());
        assertEquals(expected, service.getById(id));
        service.delete(id);
        verify(repository).delete(id);
        assertThrows(CertificateNotFoundException.class, () -> service.getById(id));
    }

    @Test
    void testGetAllCertificatesByParams_ShouldReturnListWithOneCertificate() throws ParseException {
        List<Certificate> expected = new ArrayList<>();
        expected.add(new CertificateBuilder()
                .setId(1)
                .setName("Gift 1")
                .setDescription("Best gift certificate for gamers")
                .setPrice(new BigDecimal("19.99"))
                .setDuration(10)
                .setCreateDate(FORMATTER.parse(FORMATTER.format(new Date())))
                .setLastUpdateDate(FORMATTER.parse(FORMATTER.format(new Date())))
                .addTag("game")
                .build()
        );
        when(repository.getAllWithParams(any(), any(), any(), eq("for gamers"))).thenReturn(expected);
        assertEquals(expected, service.getAllCertificatesByParams(null, SortBy.NAME, SortType.ASC, "for gamers"));
    }

    @Test
    void testGetAllCertificatesByParams_ShouldReturnEmptyList() {
        when(repository.getAllWithParams(any(), any(), any(), any())).thenReturn(Collections.emptyList());
        assertIterableEquals(Collections.emptyList(),
                service.getAllCertificatesByParams(any(), any(), any(), any()));
    }

    @Test
    void testGetById_ShouldReturnCertificate() throws ParseException {
        Certificate expected = new Certificate(
                1,
                "Gift 1",
                "Best gift certificate for gamers",
                new BigDecimal("19.99"),
                10,
                FORMATTER.parse(FORMATTER.format(new Date())),
                FORMATTER.parse(FORMATTER.format(new Date()))
        );
        expected.getTags().add(new Tag("game"));
        when(repository.getById(1)).thenReturn(Optional.of(expected));
        assertEquals(expected, service.getById(1));
    }

    @Test
    void testGetById_ShouldReturnNull() {
        when(repository.getById(1)).thenReturn(Optional.empty());
        assertThrows(CertificateNotFoundException.class, () -> service.getById(1));
    }
}
