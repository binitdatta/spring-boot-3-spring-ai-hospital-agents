create database ai_agent_factory

use ai_agent_factory

create table department
(
    department_id bigint auto_increment
        primary key,
    code          varchar(32)  not null,
    name          varchar(128) not null,
    constraint code
        unique (code)
);

create table inventory_item
(
    item_id   bigint auto_increment
        primary key,
    sku       varchar(64)    not null,
    name      varchar(128)   not null,
    uom       varchar(16)    not null,
    par_level int default 50 not null,
    constraint sku
        unique (sku)
);

create table patient
(
    patient_id bigint auto_increment
        primary key,
    mrn        varchar(32)          not null,
    first_name varchar(64)          not null,
    last_name  varchar(64)          not null,
    dob        date                 not null,
    sex        enum ('F', 'M', 'X') not null,
    phone      varchar(32)          null,
    email      varchar(128)         null,
    constraint mrn
        unique (mrn)
);

create table rag_document
(
    doc_id      bigint auto_increment
        primary key,
    external_id varchar(128)                                            null,
    title       varchar(256)                                            not null,
    doc_type    enum ('SOP', 'POLICY', 'CHECKLIST', 'FAQ', 'GUIDELINE') not null,
    version     varchar(32) default '1.0'                               null,
    source_uri  varchar(512)                                            null,
    created_at  datetime    default CURRENT_TIMESTAMP                   not null,
    constraint external_id
        unique (external_id)
);

create table rag_chunk
(
    chunk_id    bigint auto_increment
        primary key,
    doc_id      bigint       not null,
    chunk_index int          not null,
    heading     varchar(256) null,
    text        mediumtext   not null,
    constraint uq_chunk
        unique (doc_id, chunk_index),
    constraint fk_chunk_doc
        foreign key (doc_id) references rag_document (doc_id)
);

create fulltext index ft_chunk_text
    on rag_chunk (text);

create table rag_embedding
(
    chunk_id    bigint                             not null
        primary key,
    model       varchar(64)                        not null,
    dimension   int                                not null,
    vector_blob longblob                           not null,
    l2_norm     double                             null,
    updated_at  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint fk_emb_chunk
        foreign key (chunk_id) references rag_chunk (chunk_id)
            on delete cascade
);

create table role
(
    role_id     bigint auto_increment
        primary key,
    code        varchar(32)                           not null,
    name        varchar(128)                          not null,
    category    enum ('CLINICAL', 'SUPPORT', 'ADMIN') not null,
    clinical    tinyint(1) default 0                  not null,
    description varchar(256)                          null,
    created_at  datetime   default CURRENT_TIMESTAMP  not null,
    constraint code
        unique (code)
);

create table staff
(
    staff_id   bigint auto_increment
        primary key,
    staff_no   varchar(32)          not null,
    first_name varchar(64)          not null,
    last_name  varchar(64)          not null,
    email      varchar(128)         null,
    phone      varchar(32)          null,
    active     tinyint(1) default 1 not null,
    constraint email
        unique (email),
    constraint staff_no
        unique (staff_no)
);

create table staff_role
(
    role_id bigint auto_increment
        primary key,
    code    varchar(32)  not null,
    name    varchar(128) not null,
    constraint code
        unique (code)
);

create table staff_role_map
(
    staff_id bigint not null,
    role_id  bigint not null,
    primary key (staff_id, role_id),
    constraint fk_srm_role
        foreign key (role_id) references staff_role (role_id),
    constraint fk_srm_staff
        foreign key (staff_id) references staff (staff_id)
);

create table staffing_transfer_audit
(
    audit_id     bigint auto_increment
        primary key,
    requested_by varchar(64)                        null,
    approved_by  varchar(64)                        null,
    payload_json json                               null,
    created_at   datetime default CURRENT_TIMESTAMP null
);

create table unit
(
    unit_id       bigint auto_increment
        primary key,
    department_id bigint                                      not null,
    code          varchar(32)                                 not null,
    name          varchar(128)                                not null,
    acuity_level  enum ('GEN', 'STEPDOWN', 'ICU', 'ED', 'OR') not null,
    constraint code
        unique (code),
    constraint fk_unit_dept
        foreign key (department_id) references department (department_id)
);

create table room
(
    room_id               bigint auto_increment
        primary key,
    unit_id               bigint                                                         not null,
    room_number           varchar(16)                                                    not null,
    isolation_type        enum ('NONE', 'CONTACT', 'DROPLET', 'AIRBORNE') default 'NONE' not null,
    has_negative_pressure tinyint(1)                                      default 0      not null,
    constraint uq_room_unit_num
        unique (unit_id, room_number),
    constraint fk_room_unit
        foreign key (unit_id) references unit (unit_id)
);

create table bed
(
    bed_id               bigint auto_increment
        primary key,
    room_id              bigint                                                                           not null,
    bed_label            varchar(16)                                                                      not null,
    status               enum ('AVAILABLE', 'OCCUPIED', 'CLEANING', 'OUT_OF_SERVICE') default 'AVAILABLE' not null,
    out_of_service_since datetime                                                                         null,
    last_cleaned_at      datetime                                                                         null,
    constraint uq_bed_room_label
        unique (room_id, bed_label),
    constraint fk_bed_room
        foreign key (room_id) references room (room_id)
);

create table bed_event
(
    event_id    bigint auto_increment
        primary key,
    bed_id      bigint                                                       not null,
    from_status enum ('AVAILABLE', 'OCCUPIED', 'CLEANING', 'OUT_OF_SERVICE') null,
    to_status   enum ('AVAILABLE', 'OCCUPIED', 'CLEANING', 'OUT_OF_SERVICE') not null,
    at_ts       datetime default CURRENT_TIMESTAMP                           not null,
    reason      varchar(255)                                                 null,
    constraint bed_event_ibfk_1
        foreign key (bed_id) references bed (bed_id)
);

create index bed_id
    on bed_event (bed_id);

create table encounter
(
    encounter_id    bigint auto_increment
        primary key,
    patient_id      bigint                                                             not null,
    encounter_no    varchar(32)                                                        not null,
    type            enum ('ED', 'INPATIENT', 'OUTPATIENT')                             not null,
    admit_dt        datetime                                                           null,
    discharge_dt    datetime                                                           null,
    current_unit_id bigint                                                             null,
    current_bed_id  bigint                                                             null,
    bed_assigned_at datetime                                                           null,
    status          enum ('REGISTERED', 'ADMITTED', 'DISCHARGED') default 'REGISTERED' not null,
    constraint encounter_no
        unique (encounter_no),
    constraint fk_enc_bed
        foreign key (current_bed_id) references bed (bed_id),
    constraint fk_enc_patient
        foreign key (patient_id) references patient (patient_id),
    constraint fk_enc_unit
        foreign key (current_unit_id) references unit (unit_id)
);

create table evs_ticket
(
    evs_ticket_id bigint auto_increment
        primary key,
    room_id       bigint                                                                        not null,
    priority      enum ('NORMAL', 'HIGH', 'STAT')                     default 'NORMAL'          not null,
    status        enum ('QUEUED', 'IN_PROGRESS', 'DONE', 'CANCELLED') default 'QUEUED'          not null,
    eta_minutes   int                                                                           null,
    created_by    bigint                                                                        null,
    created_at    datetime                                            default CURRENT_TIMESTAMP not null,
    closed_at     datetime                                                                      null,
    constraint fk_evs_room
        foreign key (room_id) references room (room_id),
    constraint fk_evs_staff
        foreign key (created_by) references staff (staff_id)
);

create table staff_assignment
(
    assignment_id bigint auto_increment
        primary key,
    staff_id      bigint                                                                 not null,
    role_id       bigint                                                                 not null,
    unit_id       bigint                                                                 not null,
    start_dt      datetime                                                               not null,
    end_dt        datetime                                                               not null,
    status        enum ('PLANNED', 'ACTIVE', 'COMPLETED', 'CANCELLED') default 'PLANNED' not null,
    note          varchar(256)                                                           null,
    constraint staff_assignment_ibfk_1
        foreign key (staff_id) references staff (staff_id),
    constraint staff_assignment_ibfk_2
        foreign key (role_id) references role (role_id),
    constraint staff_assignment_ibfk_3
        foreign key (unit_id) references unit (unit_id)
);

create index ix_window
    on staff_assignment (start_dt, end_dt, unit_id, role_id);

create index role_id
    on staff_assignment (role_id);

create index staff_id
    on staff_assignment (staff_id);

create index unit_id
    on staff_assignment (unit_id);

create table staffing_target
(
    target_id              bigint auto_increment
        primary key,
    unit_id                bigint                       not null,
    role_id                bigint                       not null,
    shift_name             enum ('DAY', 'EVE', 'NIGHT') not null,
    target_count           int                          not null,
    min_count              int                          not null,
    max_count              int                          null,
    nurse_to_patient_ratio decimal(5, 2)                null,
    effective_date         date default (curdate())     not null,
    constraint staffing_target_ibfk_1
        foreign key (unit_id) references unit (unit_id),
    constraint staffing_target_ibfk_2
        foreign key (role_id) references role (role_id)
);

create index role_id
    on staffing_target (role_id);

create index unit_id
    on staffing_target (unit_id);

create table transport_job
(
    transport_job_id bigint auto_increment
        primary key,
    encounter_id     bigint                                                                                      not null,
    from_unit_id     bigint                                                                                      not null,
    to_unit_id       bigint                                                                                      not null,
    status           enum ('REQUESTED', 'ASSIGNED', 'IN_TRANSIT', 'DONE', 'CANCELLED') default 'REQUESTED'       not null,
    requested_at     datetime                                                          default CURRENT_TIMESTAMP not null,
    completed_at     datetime                                                                                    null,
    assigned_to      bigint                                                                                      null,
    constraint fk_tj_enc
        foreign key (encounter_id) references encounter (encounter_id),
    constraint fk_tj_from
        foreign key (from_unit_id) references unit (unit_id),
    constraint fk_tj_staff
        foreign key (assigned_to) references staff (staff_id),
    constraint fk_tj_to
        foreign key (to_unit_id) references unit (unit_id)
);

create table unit_inventory
(
    unit_id     bigint        not null,
    item_id     bigint        not null,
    on_hand_qty int default 0 not null,
    primary key (unit_id, item_id),
    constraint fk_ui_item
        foreign key (item_id) references inventory_item (item_id),
    constraint fk_ui_unit
        foreign key (unit_id) references unit (unit_id)
);

create definer = root@localhost view v_bed_availability as
select `b`.`bed_id`                AS `bed_id`,
       `u`.`code`                  AS `unit_code`,
       `r`.`room_number`           AS `room_number`,
       `b`.`bed_label`             AS `bed_label`,
       `b`.`status`                AS `status`,
       `r`.`isolation_type`        AS `isolation_type`,
       `r`.`has_negative_pressure` AS `has_negative_pressure`,
       `b`.`last_cleaned_at`       AS `last_cleaned_at`
from ((`ai_agent_factory`.`bed` `b` join `ai_agent_factory`.`room` `r`
       on ((`r`.`room_id` = `b`.`room_id`))) join `ai_agent_factory`.`unit` `u` on ((`u`.`unit_id` = `r`.`unit_id`)))
where (`b`.`status` in ('AVAILABLE', 'CLEANING'));

