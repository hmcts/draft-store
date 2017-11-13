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

    public void setupDocumentForUser(String userId, String type, String document) throws SQLException {
        PGobject jsonbObj = new PGobject();
        jsonbObj.setType("json");
        jsonbObj.setValue(document);

        jdbcTemplate.update(
            "INSERT INTO draft_document (user_id, document_type, service, document, created, updated) "
                + "VALUES (:userId, :type, 'cmc', :document, now(), now())",
            new MapSqlParameterSource("userId", userId)
                .addValue("type", type)
                .addValue("document", jsonbObj)
        );
    }

    public void deleteDocuments(String userId) {
        jdbcTemplate.update(
            "DELETE FROM draft_document WHERE user_id = :userId",
            new MapSqlParameterSource("userId", userId)
        );
    }
}
