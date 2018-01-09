package uk.gov.hmcts.reform.draftstore.data;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import uk.gov.hmcts.reform.draftstore.data.model.CreateDraft;
import uk.gov.hmcts.reform.draftstore.data.model.Draft;
import uk.gov.hmcts.reform.draftstore.data.model.UpdateDraft;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@SuppressWarnings("checkstyle:LineLength")
public class DraftStoreDAO {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final int defaultMaxStaleDays;
    private final Clock clock;

    public DraftStoreDAO(
        NamedParameterJdbcTemplate jdbcTemplate,
        int defaultMaxStaleDays,
        Clock clock
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.defaultMaxStaleDays = defaultMaxStaleDays;
        this.clock = clock;
    }

    /**
     * Creates a new draft.
     * @return id of newly created draft.
     */
    public int insert(String userId, String service, CreateDraft newDraft) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Timestamp now = Timestamp.from(this.clock.instant());

        jdbcTemplate.update(
            "INSERT INTO draft_document (user_id, service, document, encrypted_document, document_type, max_stale_days, created, updated)"
                + "VALUES (:userId, :service, :doc::JSON, :encDoc, :type, :maxStaleDays, :created, :updated)",
            new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("service", service)
                .addValue("doc", newDraft.document)
                .addValue("encDoc", newDraft.encryptedDocument)
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
            "UPDATE draft_document "
                + "SET document = :doc::JSON, encrypted_document = :encDoc,  document_type = :type, updated = :updated "
                + "WHERE id = :id",
            new MapSqlParameterSource()
                .addValue("doc", draft.document)
                .addValue("encDoc", draft.encryptedDocument)
                .addValue("type", draft.type)
                .addValue("updated", Timestamp.from(this.clock.instant()))
                .addValue("id", id)
        );
    }

    public List<Draft> readAll(String userId, String service, Integer after, int limit) {
        return jdbcTemplate.query(
            "SELECT * FROM draft_document "
                + "WHERE user_id = :userId "
                + "AND service = :service "
                + Optional.ofNullable(after).map(a -> "AND id > :after ").orElse("")
                + "ORDER BY id ASC "
                + "LIMIT :limit",
            new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("service", service)
                .addValue("after", after)
                .addValue("limit", limit),
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

    public void delete(int id) {
        jdbcTemplate.update(
            "DELETE FROM draft_document WHERE id = :id",
            new MapSqlParameterSource("id", id)
        );
    }

    public void deleteAll(String userId, String service) {
        jdbcTemplate.update(
            "DELETE FROM draft_document WHERE user_id = :userId AND service = :service",
            new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("service", service)
        );
    }

    public void deleteStaleDrafts() {
        jdbcTemplate.update(
            "DELETE FROM draft_document "
                + "WHERE updated + interval '1 day' * max_stale_days < :now",
            new MapSqlParameterSource("now", Timestamp.from(this.clock.instant()))
        );
    }

    public List<Map<String, Object>> getUnencryptedDrafts() {
        return jdbcTemplate.queryForList(
            "SELECT service, created, updated "
                + "FROM draft_document "
                + "WHERE encrypted_document IS NULL "
                + "ORDER BY updated DESC",
            new MapSqlParameterSource()
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
                rs.getBytes("encrypted_document"),
                rs.getString("document_type"),
                rs.getTimestamp("created").toInstant().atOffset(ZoneOffset.UTC).toZonedDateTime(),
                rs.getTimestamp("updated").toInstant().atOffset(ZoneOffset.UTC).toZonedDateTime()
            );
        }
    }
}


