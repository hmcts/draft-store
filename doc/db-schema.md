# Database Schema

```
                                         Table "public.draft_document"
       Column       |            Type             |                          Modifiers
--------------------+-----------------------------+-------------------------------------------------------------
 id                 | bigint                      | not null default nextval('draft_document_id_seq'::regclass)
 user_id            | character varying(256)      | not null
 document           | jsonb                       |
 document_type      | character varying           | not null
 service            | character varying           | not null
 created            | timestamp without time zone | not null
 updated            | timestamp without time zone | not null
 max_stale_days     | smallint                    |
 encrypted_document | bytea                       |
Indexes:
    "draft_document_pkey" PRIMARY KEY, btree (id)
```
