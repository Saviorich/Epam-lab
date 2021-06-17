package com.epam.esm.webservice.repository.impl;

import com.epam.esm.webservice.CertificateBuilder;
import com.epam.esm.webservice.DatabaseSetup;
import com.epam.esm.webservice.config.TestDBConfig;
import com.epam.esm.webservice.entity.Certificate;
import com.epam.esm.webservice.entity.Tag;
import com.epam.esm.webservice.repository.CertificateRepository;
import com.epam.esm.webservice.util.SortBy;
import com.epam.esm.webservice.util.SortType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Component
@ActiveProfiles(profiles = "test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestDBConfig.class}, loader = AnnotationConfigContextLoader.class)
class CertificateRepositoryImplTest {

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat(
            "yyyy-MM-dd");

    private final CertificateRepository repository;

    @Autowired
    public CertificateRepositoryImplTest(DataSource dataSource, PlatformTransactionManager manager) {
        repository = new CertificateRepositoryImpl(dataSource, manager);
        new DatabaseSetup(dataSource)
                .createTables()
                .disableReferentialIntegrity()
                .createAliases()
                .insertTags()
                .insertCertificates()
                .insertJunctionTable();
    }

    @Test
    void testGetAllWithParams_ShouldReturnValuesByTags() {
        List<Certificate> actualCertificate = repository.getAllWithParams(
                Collections.singletonList("game"),
                SortBy.NAME,
                SortType.ASC,
                "");
        for (Certificate certificate : actualCertificate) {
            assertTrue(certificate.getTags().contains(new Tag("game")));
        }
    }

    @Test
    void testGetAllWithParams_ShouldReturnAllValues() {
        int amount = 4;
        List<Certificate> expected = new ArrayList<>();
        assertFalse(repository.getById(amount + 1).isPresent());
        assertEquals(amount, repository.getAllWithParams(null, SortBy.NAME, SortType.ASC, "").size());
    }

    @Test
    void testUpdate_ShouldModifyExistingCertificate() throws ParseException {
        int id = 4;
        Certificate certificate = new CertificateBuilder()
                .setId(id)
                .setName("Gift 4")
                .setDescription("Free drinks in any coffee")
                .setPrice(new BigDecimal("3"))
                .setDuration(1)
                .addTag("delivery")
                .build();
        assertTrue(repository.getById(id).isPresent());
        certificate.setDescription("Free drinks only in Starbucks");
        repository.update(certificate);
        assertEquals(certificate.getDescription(), repository.getById(id).get().getDescription());
    }

    @Test
    void testAdd_ShouldAddCertificateToDatabase() throws ParseException {
        Certificate expected = new CertificateBuilder()
                .setName("Gift 5")
                .setDescription("New certificate")
                .setPrice(new BigDecimal("25.99"))
                .setDuration(30)
                .addTag("game")
                .addTag("shooter")
                .build();
        assertFalse(repository.getById(5).isPresent());
        repository.add(expected);

        expected.setId(5);
        expected.setCreateDate(FORMATTER.parse(FORMATTER.format(new Date())));
        expected.setLastUpdateDate(FORMATTER.parse(FORMATTER.format(new Date())));

        assertEquals(expected, repository.getById(5).get());
    }

    @Test
    void testDelete_ShouldDeleteCertificateById() {
        int id = 4;
        assertTrue(repository.getById(id).isPresent());
        repository.delete(id);
        assertFalse(repository.getById(id).isPresent());
    }

    @ParameterizedTest
    @CsvSource({"1,Gift 1", "2,Gift 2", "3,Gift 3", "4,Gift 4"})
    void testGetById_ShouldReturnExistingCertificates(int id, String expectedName) {
        assertEquals(expectedName, repository.getById(id)
                .get()
                .getName());
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 6, 7, 8, 9, 10})
    void testGetById_ShouldReturnOptionalEmpty(int id) {
        assertFalse(repository.getById(id).isPresent());
    }

    @Test
    void testDeleteTagsFromCertificate_ShouldDeleteTagsFromExistingCertificate() throws ParseException {
        Certificate expected = new CertificateBuilder()
                .addTag("game")
                .addTag("shooter")
                .addTag("multiplayer")
                .build();;
        assertIterableEquals(expected.getTags(), repository.getById(2)
                .get()
                .getTags());
        expected.getTags().removeIf(t -> t.equals(new Tag("multiplayer")));
        repository.deleteTagsFromCertificate(2, Collections.singletonList(new Tag(5, "simulator")));
        assertIterableEquals(expected.getTags(), repository.getById(2)
                .get()
                .getTags());
    }
}