-- V11__fix_column_typo.sql
-- Fix typo in V10: column was named 'additional_nfo' instead of 'additional_info'

ALTER TABLE venue RENAME COLUMN additional_nfo TO additional_info;
