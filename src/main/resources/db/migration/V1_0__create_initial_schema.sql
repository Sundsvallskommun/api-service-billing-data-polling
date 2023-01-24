    create table access_card (
       id bigint not null auto_increment,
        created datetime(6),
        first_name varchar(255),
        flow_instance_id varchar(255) not null,
        last_name varchar(255),
        modified datetime(6),
        photo bit not null,
        processed datetime(6),
        reference_code varchar(255) not null,
        status varchar(255) not null,
        username varchar(255),
        primary key (id)
    ) engine=InnoDB;
create index access_card_flow_instance_id_idx on access_card (flow_instance_id);