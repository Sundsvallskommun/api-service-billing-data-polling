
    create table access_card (
       id bigint not null auto_increment,
        created datetime(6),
        first_name varchar(255),
        flow_instance_id varchar(255) not null,
        last_name varchar(255),
        modified datetime(6),
        photo bit not null,
        posted datetime(6),
        processed datetime(6),
        reference_code varchar(255) not null,
        reference_code_id varchar(255),
        reference_name varchar(255),
        status varchar(255) not null,
        status_message varchar(255),
        username varchar(255),
        primary key (id)
    ) engine=InnoDB;
create index access_card_flow_instance_id_idx on access_card (flow_instance_id);
create index access_card_status_idx on access_card (status);
