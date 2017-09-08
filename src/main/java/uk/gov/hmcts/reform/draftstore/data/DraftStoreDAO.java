package uk.gov.hmcts.reform.draftstore.data;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import uk.gov.hmcts.reform.draftstore.domain.CreateDraft;
import uk.gov.hmcts.reform.draftstore.domain.Draft;
import uk.gov.hmcts.reform.draftstore.domain.SaveStatus;
import uk.gov.hmcts.reform.draftstore.domain.UpdateDraft;
import uk.gov.hmcts.reform.draftstore.exception.NoDraftFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static uk.gov.hmcts.reform.draftstore.domain.SaveStatus.Created;
import static uk.gov.hmcts.reform.draftstore.domain.SaveStatus.Updated;

public class DraftStoreDAO {

    // region queries
    private static final String INSERT =
        "INSERT INTO draft_document (user_id, document_type, document) "
            + "VALUES (:userId, :type, cast(:document AS JSON))";

    private static final String UPDATE =
        "UPDATE draft_document "
            + "SET document = cast(:document AS JSON) "
            + "WHERE user_id = :userId "
            + "AND document_type = :type";

    private static final String DELETE =
        "DELETE FROM draft_document "
            + "WHERE user_id = :userId "
            + "AND document_type = :type";
    // endregion

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public DraftStoreDAO(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public SaveStatus insertOrUpdate(String userId, String type, String newDocument) {
        MapSqlParameterSource params =
            new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("type", type)
                .addValue("document", newDocument);

        int rows = jdbcTemplate.update(UPDATE, params);
        if (rows == 1) {
            return Updated;
        } else {
            jdbcTemplate.update(INSERT, params);
            return Created;
        }
    }

    /**
     * Creates a new draft.
     * @return id of newly created draft.
     */
    public int insert(String userId, CreateDraft newDraft) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
            "INSERT INTO draft_document (user_id, document, document_type) VALUES (:userId, :doc::JSON, :type)",
            new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("doc", newDraft.document.toString())
                .addValue("type", newDraft.type),
            keyHolder,
            new String[] {"id"}
        );
        return keyHolder.getKey().intValue();
    }

    public void update(int id, UpdateDraft draft) {
        jdbcTemplate.update(
            "UPDATE draft_document SET document = :doc::JSON, document_type = :type WHERE id = :id",
            new MapSqlParameterSource()
                .addValue("doc", draft.document.toString())
                .addValue("type", draft.type)
                .addValue("id", id)
        );
    }

    public List<Draft> readAll(String userId, String type) {
        return jdbcTemplate.query(
            "SELECT * FROM draft_document WHERE user_id = :userId AND document_type = :type",
            new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("type", type),
            new DraftMapper()
        );
    }

    public List<Draft> readAll(String userId) {
        return jdbcTemplate.query(
            "SELECT * FROM draft_document WHERE user_id = :userId",
            new MapSqlParameterSource("userId", userId),
            new DraftMapper()
        );
    }

    public Optional<Draft> read(int draftId) {
        try {
            Draft draft =
                jdbcTemplate.queryForObject(
                    "SELECT * FROM draft_document WHERE id = :id",
                    new MapSqlParameterSource("id", draftId),
                    new DraftMapper()
                );
            return Optional.of(draft);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public void delete(String userId, String type) {
        int rows =
            jdbcTemplate.update(
                DELETE,
                new MapSqlParameterSource()
                    .addValue("userId", userId)
                    .addValue("type", type)
            );
        if (rows == 0) {
            throw new NoDraftFoundException();
        }
    }

    public void delete(int id) {
        jdbcTemplate.update(
            "DELETE FROM draft_document WHERE id = :id",
            new MapSqlParameterSource("id", id)
        );
    }

    private static final class DraftMapper implements RowMapper<Draft> {
        @Override
        public Draft mapRow(ResultSet rs, int rowNumber) throws SQLException {
            return new Draft(
                rs.getInt("id"),
                rs.getString("user_id"),
                rs.getString("document"),
                rs.getString("document_type")
            );
        }
    }
}


