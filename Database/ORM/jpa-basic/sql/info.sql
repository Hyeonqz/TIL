create table user (
                               email varchar(50) not null primary key ,
                               name varchar(50),
                               create_date datetime
) engine innodb character set utf8;

create table hotel (
                                id varchar(50) not null primary key ,
                                name varchar(50),
                                grade varchar(50),
                                zipcode varchar(50),
                                address1 varchar(255),
                                address2 varchar(255)
) engine innodb character set utf8;