ALTER TABLE refresh_tokens
    DROP INDEX uk_refresh_tokens_token;

ALTER TABLE refresh_tokens
    CHANGE COLUMN token token_hash VARCHAR(64) NOT NULL;

ALTER TABLE refresh_tokens
    ADD CONSTRAINT uk_refresh_tokens_token_hash UNIQUE (token_hash);
