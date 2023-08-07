ALTER TABLE projects RENAME COLUMN project_id TO id;
ALTER TABLE projects RENAME COLUMN is_archived TO archived;
ALTER TABLE time_entries RENAME COLUMN time_entry_id TO id;
ALTER TABLE time_entries RENAME COLUMN is_deleted TO deleted;