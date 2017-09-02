CREATE TABLE time_entries (
    time_entry_id BIGINT PRIMARY KEY,
    description VARCHAR(255),
    end_date TIMESTAMP,
    start_date TIMESTAMP NOT NULL,
    project_id BIGINT NOT NULL REFERENCES projects(project_id)
);