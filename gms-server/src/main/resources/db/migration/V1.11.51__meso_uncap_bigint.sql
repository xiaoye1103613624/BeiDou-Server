-- Meso uncap: hold values beyond INT range (21.47E).
ALTER TABLE characters MODIFY meso BIGINT NOT NULL DEFAULT 0;
ALTER TABLE characters MODIFY MerchantMesos BIGINT NOT NULL DEFAULT 0;
ALTER TABLE storages MODIFY meso BIGINT NOT NULL DEFAULT 0;
