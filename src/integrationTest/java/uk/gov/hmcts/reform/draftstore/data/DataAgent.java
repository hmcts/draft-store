package uk.gov.hmcts.reform.draftstore.data;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.SQLException;

public class DataAgent {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public DataAgent(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setupDocumentForUser(String userId, String type, byte[] encryptedDoc) throws SQLException {

        jdbcTemplate.update(
            "INSERT INTO draft_document (user_id, document_type, encrypted_document, service, created, updated) "
                + "VALUES (:userId, :type, :document, 'cmc', now(), now())",
            new MapSqlParameterSource("userId", userId)
                .addValue("type", type)
                .addValue("document", encryptedDoc)
        );
    }

    public void deleteDocuments(String userId) {
        jdbcTemplate.update(
            "DELETE FROM draft_document WHERE user_id = :userId",
            new MapSqlParameterSource("userId", userId)
        );
    }
}
