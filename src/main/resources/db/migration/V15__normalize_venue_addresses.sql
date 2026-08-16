-- Preserve a complete structured address for the development venue created in V9.
UPDATE VENUE
SET street = 'Via Roma',
    street_number = '1',
    city = 'Milano',
    postal_code = '20100',
    country = 'Italia'
WHERE id = 1
  AND street IS NULL
  AND street_number IS NULL
  AND city IS NULL
  AND postal_code IS NULL
  AND country IS NULL;

-- Older databases may contain venues created between V10 and this migration.
UPDATE VENUE SET street = 'Not specified' WHERE street IS NULL OR BTRIM(street) = '';
UPDATE VENUE SET street_number = 'N/A' WHERE street_number IS NULL OR BTRIM(street_number) = '';
UPDATE VENUE SET city = 'Not specified' WHERE city IS NULL OR BTRIM(city) = '';
UPDATE VENUE SET postal_code = 'N/A' WHERE postal_code IS NULL OR BTRIM(postal_code) = '';
UPDATE VENUE SET country = 'Not specified' WHERE country IS NULL OR BTRIM(country) = '';

ALTER TABLE VENUE ALTER COLUMN street SET NOT NULL;
ALTER TABLE VENUE ALTER COLUMN street_number SET NOT NULL;
ALTER TABLE VENUE ALTER COLUMN city SET NOT NULL;
ALTER TABLE VENUE ALTER COLUMN postal_code SET NOT NULL;
ALTER TABLE VENUE ALTER COLUMN country SET NOT NULL;
