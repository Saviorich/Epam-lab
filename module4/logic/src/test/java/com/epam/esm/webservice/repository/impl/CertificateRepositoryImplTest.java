package com.epam.esm.webservice.repository.impl;

import com.epam.esm.webservice.config.TestConfig;
import com.epam.esm.webservice.entity.Certificate;
import com.epam.esm.webservice.entity.Tag;
import com.epam.esm.webservice.repository.CertificateRepository;
import com.epam.esm.webservice.util.CertificateParameters;
import com.epam.esm.webservice.util.Pagination;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.epam.esm.webservice.util.SortBy.NAME;
import static com.epam.esm.webservice.util.SortType.ASC;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {TestConfig.class})
@ContextConfiguration(classes = {CertificateRepositoryImpl.class})
@ActiveProfiles("test")
class CertificateRepositoryImplTest {

    private CertificateRepository repository;

    @Autowired
    public CertificateRepositoryImplTest(CertificateRepository repository) {
        this.repository = repository;
    }

    @Test
    void testFindAllByParameters_ShouldReturnAllThatContainsTag() {
        CertificateParameters certificateParameters = new CertificateParameters(
                Collections.singletonList("game"),
                NAME,
                ASC,
                "",
                new Pagination(1, 10)
        );
        List<Certificate> actual = repository.findAllByParameters(certificateParameters);
        System.out.println(actual);
        actual.forEach(c -> assertTrue(c.getTags().contains(new Tag("game"))));
    }

    @Test
    void testFindAllByParameters_ShouldReturnAllThatContainsSubstringInDescriptionOrName() {
        String expectedSubstring = "descr";
        CertificateParameters certificateParameters = new CertificateParameters(
                null,
                NAME,
                ASC,
                expectedSubstring,
                new Pagination(1, 10)
        );
        List<Certificate> expected = repository.findAllByParameters(certificateParameters);
        expected.forEach(c -> assertTrue(c.getDescription().contains(expectedSubstring)));
    }

    @Test
    void testFindAllByParameters_ShouldReturnLimitedAmountOfEntries() {
        int expectedAmount = 3;
        CertificateParameters certificateParameters = new CertificateParameters(
                null,
                NAME,
                ASC,
                "",
                new Pagination(1, expectedAmount)
        );
        int actualAmount = repository.findAllByParameters(certificateParameters).size();
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void testFindById_ShouldReturnExistingValue() {
        String expectedName = "gift 1";
        assertEquals(expectedName, repository.findById(1).get().getName());
    }

    @Test
    void testFindById_ShouldNotPresent() {
        assertFalse(repository.findById(Integer.MAX_VALUE).isPresent());
    }

    @Test
    void testAdd_ShouldAddCertificateWithExistingTags() {
        Certificate expected = new Certificate(
                "gift 5",
                "description 5",
                new BigDecimal("11.99"),
                2,
                new Date(),
                new Date()
        );
        expected.setTags(asList(new Tag(1, "game"), new Tag(4, "shooter")));
        repository.add(expected);
        Certificate actual = repository.findById(6).get();
        assertEquals(expected.getName(), actual.getName());
        assertIterableEquals(expected.getTags(), actual.getTags());
    }

    @Test
    void testAdd_ShouldAddCertificateWithNewTags() {
        Certificate expected = new Certificate(
                "gift 5",
                "description 5",
                new BigDecimal("11.99"),
                2,
                new Date(),
                new Date()
        );
        expected.setTags(asList(new Tag("alcohol"), new Tag("weekend")));
        repository.add(expected);
        Certificate actual = repository.findById(6).get();
        assertEquals(expected.getName(), actual.getName());
        assertIterableEquals(expected.getTags(), actual.getTags());
    }

    @Test
    void testUpdate_ShouldAttachExistingTag() {
        Certificate expected = repository.findById(4).get();
        System.out.println("\n\nIn database: " + expected);
        expected.setTags(asList(new Tag(1, "game"), new Tag(5, "singleplayer"), new Tag(6, "multiplayer")));
        System.out.println("\n\nExpected: " + expected);
        repository.update(expected);
        Certificate actual = repository.findById(4).get();
        assertIterableEquals(expected.getTags(), actual.getTags());
    }

    @Test
    void testUpdate_ShouldAttachNewTag() {
        Certificate expected = repository.findById(3).get();
        expected.setTags(asList(new Tag(1, "game"), new Tag(5, "singleplayer"), new Tag("horror")));
        repository.update(expected);
        Certificate actual = repository.findById(3).get();
        assertIterableEquals(expected.getTags(), actual.getTags());
    }

    @Test
    void testUpdate_ShouldDetachTags() {
        Certificate expected = repository.findById(2).get();
        expected.setTags(Collections.singletonList(new Tag(1, "game")));
        repository.update(expected);
        Certificate actual = repository.findById(2).get();
        assertIterableEquals(expected.getTags(), actual.getTags());
    }

    @Test
    void testUpdate_ShouldUpdateOneField() {
        Certificate expected = repository.findById(1).get();
        expected.setDescription("hello");
        repository.update(expected);
        Certificate actual = repository.findById(1).get();
        assertNotNull(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    void testCountTotalEntries_ShouldReturnAmountOfEntries() {
        int expected = 5;
        assertEquals(expected, repository.countFilteredEntries(new CertificateParameters(null, NAME, ASC, "", new Pagination(1, 5))));
    }
}