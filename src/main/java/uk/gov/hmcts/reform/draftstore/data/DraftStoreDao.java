package uk.gov.hmcts.reform.draftstore.data;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import uk.gov.hmcts.reform.draftstore.data.model.CreateDraft;
import uk.gov.hmcts.reform.draftstore.data.model.DocumentTypeCount;
import uk.gov.hmcts.reform.draftstore.data.model.Draft;
import uk.gov.hmcts.reform.draftstore.data.model.UpdateDraft;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("checkstyle:LineLength")
public class DraftStoreDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final int defaultMaxStaleDays;
    private final Clock clock;

    public DraftStoreDao(
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
     *
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
        if (keyHolder.getKey() == null) {
            throw new DataRetrievalFailureException("Unable to retrieve the generated key [null] or key not generated.");
        }
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

    public List<DocumentTypeCount> getDraftTypeCountsByUser(String userId) {
        try {
            return jdbcTemplate.query("SELECT document_type, COUNT(id) AS amount "
                    + "FROM draft_document WHERE user_id = :id GROUP BY document_type",
                new MapSqlParameterSource("id", userId),
                (ResultSet rs, int rowNum) -> new DocumentTypeCount(rs.getString("document_type"), rs.getInt("amount")));
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
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

    /**
     * Deletes drafts that were not updated for a specific time.
     * @return number of deleted drafts
     */
    public int deleteStaleDrafts() {
        return jdbcTemplate.update(
            "DELETE FROM draft_document "
                + "WHERE updated + interval '1 day' * COALESCE(max_stale_days, :defaultMaxStaleDays) < :now",
            new MapSqlParameterSource()
                .addValue("now", Timestamp.from(this.clock.instant()))
                .addValue("defaultMaxStaleDays", this.defaultMaxStaleDays)
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

    public List<Map<String, Object>> getDraftCountPerService() {
        return jdbcTemplate.queryForList(
            "SELECT service, count(*) "
                + "FROM draft_document "
                + "GROUP BY service "
                + "ORDER BY count DESC",
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


