--liquibase formatted sql

--changeset merjane-refacto:002-create-orders-table
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY
);
