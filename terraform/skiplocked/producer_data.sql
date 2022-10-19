-- auto-generated definition
create schema skiplocked collate latin1_swedish_ci;

use skiplocked;

-- auto-generated definition
create table datatable
(
    id      bigint auto_increment
        primary key,
    created mediumtext   not null,
    data    varchar(100) not null
);



-- auto-generated definition
create table datatable_outbox
(
    id      bigint auto_increment
        primary key,
    data    varchar(100) not null,
    created mediumtext   null
);


