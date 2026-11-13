-- Initial schema: topics, practice sessions, questions, answers and evaluations.
-- Written to run on both H2 (PostgreSQL mode) and PostgreSQL.
-- Never edit this file once applied; add a new V<n>__description.sql instead.

-- Sequences match Hibernate's defaults for GenerationType.SEQUENCE (allocationSize 50).
create sequence topic_seq start with 1 increment by 50;
create sequence practice_session_seq start with 1 increment by 50;
create sequence question_seq start with 1 increment by 50;
create sequence answer_seq start with 1 increment by 50;
create sequence evaluation_seq start with 1 increment by 50;

create table topic (
    id              bigint                      not null,
    name            varchar(100)                not null,
    normalized_name varchar(100)                not null,
    parent_id       bigint,
    created_at      timestamp(6) with time zone not null,
    constraint pk_topic primary key (id),
    constraint fk_topic_parent foreign key (parent_id) references topic (id),
    constraint uk_topic_parent_name unique (parent_id, normalized_name)
);
create index ix_topic_parent on topic (parent_id);

create table practice_session (
    id         bigint                      not null,
    title      varchar(200),
    started_at timestamp(6) with time zone not null,
    ended_at   timestamp(6) with time zone,
    constraint pk_practice_session primary key (id)
);

create table question (
    id         bigint                      not null,
    topic_id   bigint                      not null,
    level      varchar(20)                 not null,
    text       varchar(4000)               not null,
    parent_id  bigint,
    session_id bigint,
    position   integer,
    created_at timestamp(6) with time zone not null,
    constraint pk_question primary key (id),
    constraint fk_question_topic foreign key (topic_id) references topic (id),
    constraint fk_question_parent foreign key (parent_id) references question (id),
    constraint fk_question_session foreign key (session_id) references practice_session (id),
    constraint uk_question_session_position unique (session_id, position),
    constraint ck_question_level check (level in ('JUNIOR', 'MID', 'SENIOR', 'ARCHITECT')),
    constraint ck_question_position check (position is null or position >= 1)
);
create index ix_question_topic_level on question (topic_id, level);
create index ix_question_parent on question (parent_id);

create table question_key_points (
    question_id bigint        not null,
    position    integer       not null,
    key_point   varchar(1000) not null,
    constraint pk_question_key_points primary key (question_id, position),
    constraint fk_question_key_points_question foreign key (question_id) references question (id)
);

create table answer (
    id          bigint                      not null,
    question_id bigint                      not null,
    text        varchar(10000)              not null,
    answered_at timestamp(6) with time zone not null,
    constraint pk_answer primary key (id),
    constraint fk_answer_question foreign key (question_id) references question (id)
);
create index ix_answer_question on answer (question_id);

create table evaluation (
    id           bigint                      not null,
    answer_id    bigint                      not null,
    score        integer                     not null,
    feedback     varchar(4000)               not null,
    model_answer varchar(10000),
    llm_model    varchar(100)                not null,
    created_at   timestamp(6) with time zone not null,
    constraint pk_evaluation primary key (id),
    constraint fk_evaluation_answer foreign key (answer_id) references answer (id),
    constraint uk_evaluation_answer unique (answer_id),
    constraint ck_evaluation_score check (score between 0 and 10)
);

create table evaluation_strengths (
    evaluation_id bigint        not null,
    position      integer       not null,
    strength      varchar(1000) not null,
    constraint pk_evaluation_strengths primary key (evaluation_id, position),
    constraint fk_evaluation_strengths_evaluation foreign key (evaluation_id) references evaluation (id)
);

create table evaluation_missing_points (
    evaluation_id bigint        not null,
    position      integer       not null,
    missing_point varchar(1000) not null,
    constraint pk_evaluation_missing_points primary key (evaluation_id, position),
    constraint fk_evaluation_missing_points_evaluation foreign key (evaluation_id) references evaluation (id)
);
