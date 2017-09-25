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
import java.sql.Timestamp;
import java.time.Clock;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static uk.gov.hmcts.reform.draftstore.domain.SaveStatus.Created;
import static uk.gov.hmcts.reform.draftstore.domain.SaveStatus.Updated;

public class DraftStoreDAO {

    // region queries
    private static final String INSERT =
        "INSERT INTO draft_document (user_id, service, document_type, document, created, updated) "
            + "VALUES (:userId, :service, :type, cast(:document AS JSON), :created, :updated)";

    private static final String UPDATE =
        "UPDATE draft_document "
            + "SET document = cast(:document AS JSON), updated = :updated "
            + "WHERE user_id = :userId "
            + "AND service = :service "
            + "AND document_type = :type";

    private static final String DELETE =
        "DELETE FROM draft_document "
            + "WHERE user_id = :userId "
            + "AND document_type = :type";
    // endregion

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final int defaultMaxStaleDays;
    private final Clock clock;

    public DraftStoreDAO(NamedParameterJdbcTemplate jdbcTemplate, int defaultMaxStaleDays, Clock clock) {
        this.jdbcTemplate = jdbcTemplate;
        this.defaultMaxStaleDays = defaultMaxStaleDays;
        this.clock = clock;
    }

    public SaveStatus insertOrUpdate(String userId, String service, String type, String newDocument) {
        Timestamp now = Timestamp.from(this.clock.instant());
        MapSqlParameterSource params =
            new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("service", service)
                .addValue("type", type)
                .addValue("document", newDocument)
                .addValue("created", now)
                .addValue("updated", now);

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
    public int insert(String userId, String service, CreateDraft newDraft) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Timestamp now = Timestamp.from(this.clock.instant());

        jdbcTemplate.update(
            "INSERT INTO draft_document (user_id, service, document, document_type, max_stale_days, created, updated)"
                + "VALUES (:userId, :service, :doc::JSON, :type, :maxStaleDays, :created, :updated)",
            new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("service", service)
                .addValue("doc", newDraft.document.toString())
                .addValue("type", newDraft.type)
                .addValue("maxStaleDays", Optional.ofNullable(newDraft.maxStaleDays).orElse(defaultMaxStaleDays))
                .addValue("created", now)
                .addValue("updated", now),
            keyHolder,
            new String[] {"id"}
        );
        return keyHolder.getKey().intValue();
    }

    public void update(int id, UpdateDraft draft) {
        jdbcTemplate.update(
            "UPDATE draft_document SET document = :doc::JSON, document_type = :type, updated = :updated "
                + "WHERE id = :id",
            new MapSqlParameterSource()
                .addValue("doc", draft.document.toString())
                .addValue("type", draft.type)
                .addValue("updated", Timestamp.from(this.clock.instant()))
                .addValue("id", id)
        );
    }

    public List<Draft> readAll(String userId, String service, String type) {
        return jdbcTemplate.query(
            "SELECT * FROM draft_document WHERE user_id = :userId AND service = :service AND document_type = :type",
            new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("service", service)
                .addValue("type", type),
            new DraftMapper()
        );
    }

    public List<Draft> readAll(String userId, String service) {
        return jdbcTemplate.query(
            "SELECT * FROM draft_document WHERE user_id = :userId AND service = :service",
            new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("service", service),
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

    public void deleteStaleDrafts() {
        jdbcTemplate.update(
            "DELETE FROM draft_document "
                + "WHERE updated + interval '1 day' * max_stale_days < :now",
            new MapSqlParameterSource("now", Timestamp.from(this.clock.instant()))
        );
    }

    private static final class DraftMapper implements RowMapper<Draft> {
        @Override
        public Draft mapRow(ResultSet rs, int rowNumber) throws SQLException {
            return new Draft(
                rs.getString("id"),
                rs.getString("user_id"),
                rs.getString("service"),
                rs.getString("document"),
                rs.getString("document_type"),
                rs.getTimestamp("created").toInstant().atOffset(ZoneOffset.UTC).toZonedDateTime(),
                rs.getTimestamp("updated").toInstant().atOffset(ZoneOffset.UTC).toZonedDateTime()
            );
        }
    }
}


