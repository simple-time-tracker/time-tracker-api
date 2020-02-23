ALTER TABLE projects RENAME COLUMN user_id TO created_by;
ALTER TABLE projects ADD COLUMN date_modified TIMESTAMP;
ALTER TABLE projects ADD COLUMN modified_by UUID;

UPDATE projects SET date_modified = date_created;
UPDATE projects SET modified_by = created_by;
ALTER TABLE projects ALTER COLUMN date_modified SET NOT NULL;
ALTER TABLE projects ALTER COLUMN modified_by SET NOT NULL;

ALTER TABLE time_entries RENAME COLUMN user_id TO created_by;