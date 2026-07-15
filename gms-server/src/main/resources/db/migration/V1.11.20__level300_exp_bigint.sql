-- Level 300 data layer: characters.exp must hold values beyond INT range.
ALTER TABLE characters MODIFY exp BIGINT NOT NULL DEFAULT 0;
