CREATE TABLE projects (
    project_id BIGSERIAL PRIMARY KEY,
    date_created TIMESTAMP NOT NULL ,
    name VARCHAR(255) NOT NULL
);