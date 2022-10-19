-- auto-generated definition
create schema skiplocked collate latin1_swedish_ci;

use skiplocked;

create table datatable_result
(
    id      bigint auto_increment
        primary key,
    data    varchar(100) not null,
    created mediumtext   not null
);


