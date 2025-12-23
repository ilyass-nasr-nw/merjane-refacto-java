--liquibase formatted sql

--changeset merjane-refacto:001-create-products-table
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lead_time INT NOT NULL,
    available INT NOT NULL,
    type VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    expiry_date DATE,
    season_start_date DATE,
    season_end_date DATE
);
