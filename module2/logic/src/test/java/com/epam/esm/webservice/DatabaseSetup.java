package com.epam.esm.webservice;

import com.epam.esm.webservice.entity.Certificate;
import com.epam.esm.webservice.entity.Tag;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

public class DatabaseSetup {

    private static final List<String> TAGS_TO_INSERT = new ArrayList<>();

    private static final List<Certificate> CERTIFICATES_TO_INSERT = new ArrayList<>();

    private static final String CREATE_TABLE_QUERY =
                    "DROP TABLE IF EXISTS `gift_certificate`;" +
                    "CREATE TABLE IF NOT EXISTS `gift_certificate` ( " +
                    "  `id` INT NOT NULL AUTO_INCREMENT, " +
                    "  `name` VARCHAR(200) NOT NULL, " +
                    "  `description` VARCHAR(255) NOT NULL, " +
                    "  `price` DECIMAL(10,2) NOT NULL, " +
                    "  `duration` INT NOT NULL, " +
                    "  `create_date` DATE NOT NULL, " +
                    "  `last_update_date` DATE NOT NULL, " +
                    "  PRIMARY KEY (`id`));\n" +
                    "DROP TABLE IF EXISTS `tag`;" +
                    "CREATE TABLE IF NOT EXISTS `tag` (" +
                    "  `id` INT NOT NULL AUTO_INCREMENT," +
                    "  `name` VARCHAR(200) NOT NULL," +
                    "  PRIMARY KEY (`id`));" +
                    "DROP TABLE IF EXISTS `gift_certificate_has_tag`;" +
                    "CREATE TABLE IF NOT EXISTS `gift_certificate_has_tag` (" +
                    "  `gift_certificate_id` INT NOT NULL," +
                    "  `tag_id` INT NOT NULL," +
                    "  PRIMARY KEY (`gift_certificate_id`, `tag_id`));";

    private static final String INSERT_CERTIFICATES_QUERY =
            "INSERT INTO GIFT_CERTIFICATE (NAME, DESCRIPTION, PRICE, DURATION, CREATE_DATE, LAST_UPDATE_DATE) " +
            "VALUES ( ?, ?, ?, ?, NOW(), NOW() )";
    private static final String INSERT_TAGS_QUERY =
            "INSERT INTO TAG (NAME) VALUES (?);";
    private static final String INSERT_INTO_JUNCTION_TABLE =
            "INSERT INTO GIFT_CERTIFICATE_HAS_TAG (GIFT_CERTIFICATE_ID, TAG_ID) VALUES ( ?, ? )";

    private static final String DELETE_ALL_TAGS = "DELETE FROM TAG;";
    private static final String DELETE_ALL_CERTIFICATE = "DELETE FROM GIFT_CERTIFICATE";
    private static final String CLEAR_JUNCTION_TABLE = "DELETE FROM GIFT_CERTIFICATE_HAS_TAG";

    private static final String DISABLE_REFERENTIAL_INTEGRITY = "SET REFERENTIAL_INTEGRITY = FALSE";

    private static final String CREATE_DATE_FORMAT_ALIAS =
            "CREATE ALIAS IF NOT EXISTS DATE_FORMAT FOR \"com.epam.esm.webservice.DatabaseSetup.dateFormat\"";
    private static final String CREATE_ALIAS_FOR_TAGS =
            "CREATE ALIAS IF NOT EXISTS tag_insert_if_not_exists FOR \"com.epam.esm.webservice.DatabaseSetup.tagInsertIfNotExists\"";

    private static JdbcTemplate template;

    static {
        TAGS_TO_INSERT.add("game");
        TAGS_TO_INSERT.add("shooter");
        TAGS_TO_INSERT.add( "moba");
        TAGS_TO_INSERT.add("singleplayer");
        TAGS_TO_INSERT.add("multiplayer");
        TAGS_TO_INSERT.add("delivery");

        Certificate certificate = new Certificate(
                "Gift 1", "Best gift certificate for gamers", new BigDecimal("19.99"), 10
        );
        certificate.getTags().add(new Tag("game"));
        CERTIFICATES_TO_INSERT.add(certificate);

        certificate = new Certificate(
                "Gift 2", "Amazing gift for people who like shooters", new BigDecimal("39.99"), 30
        );
        certificate.setTags(Arrays.asList(new Tag("game"), new Tag("shooter"), new Tag("multiplayer")));
        CERTIFICATES_TO_INSERT.add(certificate);

        certificate = new Certificate(
                "Gift 3", "#1 game on market", new BigDecimal("9.99"), 3
        );
        certificate.setTags(Arrays.asList(new Tag("game"), new Tag("singleplayer")));
        CERTIFICATES_TO_INSERT.add(certificate);

        certificate = new Certificate(
                "Gift 4", "Free drinks in any coffee", new BigDecimal("3"), 1
        );
        certificate.getTags().add(new Tag("delivery"));
        CERTIFICATES_TO_INSERT.add(certificate);
    }

    public DatabaseSetup(DataSource dataSource) {
        template = new JdbcTemplate(dataSource);
    }

    public DatabaseSetup createTables() {
        template.update(CREATE_TABLE_QUERY);
        return this;
    }

    public DatabaseSetup insertTags() {
        template.update(DELETE_ALL_TAGS);
        template.batchUpdate(INSERT_TAGS_QUERY, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, TAGS_TO_INSERT.get(i));
            }

            @Override
            public int getBatchSize() {
                return TAGS_TO_INSERT.size();
            }
        });
        return this;
    }

    public DatabaseSetup insertCertificates() {
        template.execute(DELETE_ALL_CERTIFICATE);
        template.batchUpdate(INSERT_CERTIFICATES_QUERY, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Certificate current = CERTIFICATES_TO_INSERT.get(i);
                ps.setString(1, current.getName());
                ps.setString(2, current.getDescription());
                ps.setBigDecimal(3, current.getPrice());
                ps.setInt(4, current.getDuration());
            }

            @Override
            public int getBatchSize() {
                return CERTIFICATES_TO_INSERT.size();
            }
        });
        return this;
    }

    public DatabaseSetup insertJunctionTable() {
        template.execute(CLEAR_JUNCTION_TABLE);
        template.update(INSERT_INTO_JUNCTION_TABLE, 1, 1);
        template.update(INSERT_INTO_JUNCTION_TABLE, 2, 1);
        template.update(INSERT_INTO_JUNCTION_TABLE, 2, 2);
        template.update(INSERT_INTO_JUNCTION_TABLE, 2, 5);
        template.update(INSERT_INTO_JUNCTION_TABLE, 3, 1);
        template.update(INSERT_INTO_JUNCTION_TABLE, 3, 4);
        template.update(INSERT_INTO_JUNCTION_TABLE, 4, 1);
        return this;
    }

    public DatabaseSetup createAliases() {
        template.update(CREATE_DATE_FORMAT_ALIAS);
        template.update(CREATE_ALIAS_FOR_TAGS);
        return this;
    }

    public DatabaseSetup disableReferentialIntegrity() {
        template.execute(DISABLE_REFERENTIAL_INTEGRITY);
        return this;
    }

    public static String dateFormat(java.sql.Date date, String mysqlPattern) {
        String javaPattern = mysqlPattern;
        for (Map.Entry<String, String> entry : mysqlToJavaDateFormat().entrySet()) {
            javaPattern = javaPattern.replace(entry.getKey(), entry.getValue());
        }
        TemporalAccessor accessor = DateTimeFormatter.ISO_DATE_TIME.parse(date.toString());
        Instant i = Instant.from(accessor);
        return java.util.Date.from(i).toString();
    }

    public static void tagInsertIfNotExists(String name) {
        Boolean exist = template.query("SELECT EXISTS (SELECT id FROM tag WHERE name=?)",
                ps -> {
                    ps.setString(1, name);
                },
                rs -> rs.next() ? rs.getBoolean(1) : null);
        if (exist) {
            template.update("INSERT INTO tag (NAME) VALUES (?)", name);
        }
    }

    private static Map<String, String> mysqlToJavaDateFormat() {
        Map<String, String> convert = new HashMap<>();
        convert.put("%a", "E");
        convert.put("%b", "M");
        convert.put("%c", "M");
        convert.put("%d", "dd");
        convert.put("%e", "d");
        convert.put("%f", "S");
        convert.put("%H", "HH");
        convert.put("%h", "H");
        convert.put("%I", "h");
        convert.put("%i", "mm");
        convert.put("%J", "D");
        convert.put("%k", "h");
        convert.put("%l", "h");
        convert.put("%M", "M");
        convert.put("%m", "MM");
        convert.put("%p", "a");
        convert.put("%r", "hh:mm:ss a");
        convert.put("%s", "ss");
        convert.put("%S", "ss");
        convert.put("%T", "HH:mm:ss");
        convert.put("%U", "w");
        convert.put("%u", "w");
        convert.put("%V", "w");
        convert.put("%v", "w");
        convert.put("%W", "EEE");
        convert.put("%w", "F");
        convert.put("%Y", "yyyy");
        convert.put("%y", "yy");
        return convert;
    }
}
