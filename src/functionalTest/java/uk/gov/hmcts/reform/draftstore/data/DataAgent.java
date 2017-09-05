package uk.gov.hmcts.reform.draftstore.data;

import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.SQLException;

public class DataAgent {
    private NamedParameterJdbcTemplate jdbcTemplate;

    public DataAgent(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String documentForUser(String userId, String type) {
        return jdbcTemplate.queryForObject(
            "SELECT document FROM draft_document WHERE user_id = :userId AND document_type = :type",
            new MapSqlParameterSource("userId", userId).addValue("type", type),
            String.class
        );
    }

    public void setupDocumentForUser(String userId, String type, String document) throws SQLException {
        PGobject jsonbObj = new PGobject();
        jsonbObj.setType("json");
        jsonbObj.setValue(document);

        jdbcTemplate.update(
            "INSERT INTO draft_document (user_id, document_type, document) VALUES (:userId, :type, :document)",
            new MapSqlParameterSource("userId", userId)
                .addValue("type", type)
                .addValue("document", jsonbObj)
        );
    }

    public void deleteDocument(String userId, String type) {
        jdbcTemplate.update(
            "DELETE FROM draft_document WHERE user_id = :userId AND document_type = :type",
            new MapSqlParameterSource("userId", userId).addValue("type", type)
        );
    }

    public Integer countForUser(String userId, String type) {
        return jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM draft_document WHERE user_id = :userId AND document_type = :type",
            new MapSqlParameterSource("userId", userId).addValue("type", type),
            Integer.class
        );
    }
}
