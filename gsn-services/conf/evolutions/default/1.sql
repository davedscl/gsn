# --- !Ups

create table client (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  client_id                     varchar(255),
  secret                        varchar(255),
  redirect                      varchar(255),
  user_id                       bigint,
  linked                        boolean,
  constraint pk_client primary key (id)
);

create table client_users (
  client_id                     bigint not null,
  users_id                      bigint not null,
  constraint pk_client_users primary key (client_id,users_id)
);

create table data_source (
  id                            bigint auto_increment not null,
  value                         varchar(255),
  is_public                     boolean default false not null,
  constraint pk_data_source primary key (id)
);

create table groups (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  description                   varchar(255),
  constraint pk_groups primary key (id)
);

create table group_data_source_read (
  id                            bigint auto_increment not null,
  group_id                      bigint,
  data_source_id                bigint,
  constraint uq_group_data_source_read_group_id_data_source_id unique (group_id,data_source_id),
  constraint pk_group_data_source_read primary key (id)
);

create table group_data_source_write (
  id                            bigint auto_increment not null,
  group_id                      bigint,
  data_source_id                bigint,
  constraint uq_group_data_source_write_group_id_data_source_id unique (group_id,data_source_id),
  constraint pk_group_data_source_write primary key (id)
);

create table linked_account (
  id                            bigint auto_increment not null,
  user_id                       bigint,
  provider_user_id              varchar(255),
  provider_key                  varchar(255),
  constraint pk_linked_account primary key (id)
);

create table oauth_code (
  id                            bigint auto_increment not null,
  user_id                       bigint,
  client_id                     bigint,
  code                          varchar(255),
  creation                      bigint,
  constraint pk_oauth_code primary key (id)
);

create table oauth_token (
  id                            bigint auto_increment not null,
  user_id                       bigint,
  client_id                     bigint,
  token                         varchar(255),
  refresh                       varchar(255),
  creation                      bigint,
  duration                      bigint,
  constraint pk_oauth_token primary key (id)
);

create table security_role (
  id                            bigint auto_increment not null,
  role_name                     varchar(255),
  constraint pk_security_role primary key (id)
);

create table token_action (
  id                            bigint auto_increment not null,
  token                         varchar(255),
  target_user_id                bigint,
  type                          varchar(2),
  created                       timestamp,
  expires                       timestamp,
  constraint ck_token_action_type check ( type in ('EV','PR')),
  constraint uq_token_action_token unique (token),
  constraint pk_token_action primary key (id)
);

create table users (
  id                            bigint auto_increment not null,
  email                         varchar(255),
  name                          varchar(255),
  first_name                    varchar(255),
  last_name                     varchar(255),
  last_login                    timestamp,
  active                        boolean default false not null,
  email_validated               boolean default false not null,
  constraint pk_users primary key (id)
);

create table users_security_role (
  users_id                      bigint not null,
  security_role_id              bigint not null,
  constraint pk_users_security_role primary key (users_id,security_role_id)
);

create table users_user_permission (
  users_id                      bigint not null,
  user_permission_id            bigint not null,
  constraint pk_users_user_permission primary key (users_id,user_permission_id)
);

create table users_groups (
  users_id                      bigint not null,
  groups_id                     bigint not null,
  constraint pk_users_groups primary key (users_id,groups_id)
);

create table user_data_source_read (
  id                            bigint auto_increment not null,
  user_id                       bigint,
  data_source_id                bigint,
  constraint uq_user_data_source_read_user_id_data_source_id unique (user_id,data_source_id),
  constraint pk_user_data_source_read primary key (id)
);

create table user_data_source_write (
  id                            bigint auto_increment not null,
  user_id                       bigint,
  data_source_id                bigint,
  constraint uq_user_data_source_write_user_id_data_source_id unique (user_id,data_source_id),
  constraint pk_user_data_source_write primary key (id)
);

create table user_permission (
  id                            bigint auto_increment not null,
  value                         varchar(255),
  constraint pk_user_permission primary key (id)
);

create index ix_client_user_id on client (user_id);
alter table client add constraint fk_client_user_id foreign key (user_id) references users (id) on delete restrict on update restrict;

create index ix_client_users_client on client_users (client_id);
alter table client_users add constraint fk_client_users_client foreign key (client_id) references client (id) on delete restrict on update restrict;

create index ix_client_users_users on client_users (users_id);
alter table client_users add constraint fk_client_users_users foreign key (users_id) references users (id) on delete restrict on update restrict;

create index ix_group_data_source_read_group_id on group_data_source_read (group_id);
alter table group_data_source_read add constraint fk_group_data_source_read_group_id foreign key (group_id) references groups (id) on delete restrict on update restrict;

create index ix_group_data_source_read_data_source_id on group_data_source_read (data_source_id);
alter table group_data_source_read add constraint fk_group_data_source_read_data_source_id foreign key (data_source_id) references data_source (id) on delete restrict on update restrict;

create index ix_group_data_source_write_group_id on group_data_source_write (group_id);
alter table group_data_source_write add constraint fk_group_data_source_write_group_id foreign key (group_id) references groups (id) on delete restrict on update restrict;

create index ix_group_data_source_write_data_source_id on group_data_source_write (data_source_id);
alter table group_data_source_write add constraint fk_group_data_source_write_data_source_id foreign key (data_source_id) references data_source (id) on delete restrict on update restrict;

create index ix_linked_account_user_id on linked_account (user_id);
alter table linked_account add constraint fk_linked_account_user_id foreign key (user_id) references users (id) on delete restrict on update restrict;

create index ix_oauth_code_user_id on oauth_code (user_id);
alter table oauth_code add constraint fk_oauth_code_user_id foreign key (user_id) references users (id) on delete restrict on update restrict;

create index ix_oauth_code_client_id on oauth_code (client_id);
alter table oauth_code add constraint fk_oauth_code_client_id foreign key (client_id) references client (id) on delete restrict on update restrict;

create index ix_oauth_token_user_id on oauth_token (user_id);
alter table oauth_token add constraint fk_oauth_token_user_id foreign key (user_id) references users (id) on delete restrict on update restrict;

create index ix_oauth_token_client_id on oauth_token (client_id);
alter table oauth_token add constraint fk_oauth_token_client_id foreign key (client_id) references client (id) on delete restrict on update restrict;

create index ix_token_action_target_user_id on token_action (target_user_id);
alter table token_action add constraint fk_token_action_target_user_id foreign key (target_user_id) references users (id) on delete restrict on update restrict;

create index ix_users_security_role_users on users_security_role (users_id);
alter table users_security_role add constraint fk_users_security_role_users foreign key (users_id) references users (id) on delete restrict on update restrict;

create index ix_users_security_role_security_role on users_security_role (security_role_id);
alter table users_security_role add constraint fk_users_security_role_security_role foreign key (security_role_id) references security_role (id) on delete restrict on update restrict;

create index ix_users_user_permission_users on users_user_permission (users_id);
alter table users_user_permission add constraint fk_users_user_permission_users foreign key (users_id) references users (id) on delete restrict on update restrict;

create index ix_users_user_permission_user_permission on users_user_permission (user_permission_id);
alter table users_user_permission add constraint fk_users_user_permission_user_permission foreign key (user_permission_id) references user_permission (id) on delete restrict on update restrict;

create index ix_users_groups_users on users_groups (users_id);
alter table users_groups add constraint fk_users_groups_users foreign key (users_id) references users (id) on delete restrict on update restrict;

create index ix_users_groups_groups on users_groups (groups_id);
alter table users_groups add constraint fk_users_groups_groups foreign key (groups_id) references groups (id) on delete restrict on update restrict;

create index ix_user_data_source_read_user_id on user_data_source_read (user_id);
alter table user_data_source_read add constraint fk_user_data_source_read_user_id foreign key (user_id) references users (id) on delete restrict on update restrict;

create index ix_user_data_source_read_data_source_id on user_data_source_read (data_source_id);
alter table user_data_source_read add constraint fk_user_data_source_read_data_source_id foreign key (data_source_id) references data_source (id) on delete restrict on update restrict;

create index ix_user_data_source_write_user_id on user_data_source_write (user_id);
alter table user_data_source_write add constraint fk_user_data_source_write_user_id foreign key (user_id) references users (id) on delete restrict on update restrict;

create index ix_user_data_source_write_data_source_id on user_data_source_write (data_source_id);
alter table user_data_source_write add constraint fk_user_data_source_write_data_source_id foreign key (data_source_id) references data_source (id) on delete restrict on update restrict;


# --- !Downs

alter table client drop constraint if exists fk_client_user_id;
drop index if exists ix_client_user_id;

alter table client_users drop constraint if exists fk_client_users_client;
drop index if exists ix_client_users_client;

alter table client_users drop constraint if exists fk_client_users_users;
drop index if exists ix_client_users_users;

alter table group_data_source_read drop constraint if exists fk_group_data_source_read_group_id;
drop index if exists ix_group_data_source_read_group_id;

alter table group_data_source_read drop constraint if exists fk_group_data_source_read_data_source_id;
drop index if exists ix_group_data_source_read_data_source_id;

alter table group_data_source_write drop constraint if exists fk_group_data_source_write_group_id;
drop index if exists ix_group_data_source_write_group_id;

alter table group_data_source_write drop constraint if exists fk_group_data_source_write_data_source_id;
drop index if exists ix_group_data_source_write_data_source_id;

alter table linked_account drop constraint if exists fk_linked_account_user_id;
drop index if exists ix_linked_account_user_id;

alter table oauth_code drop constraint if exists fk_oauth_code_user_id;
drop index if exists ix_oauth_code_user_id;

alter table oauth_code drop constraint if exists fk_oauth_code_client_id;
drop index if exists ix_oauth_code_client_id;

alter table oauth_token drop constraint if exists fk_oauth_token_user_id;
drop index if exists ix_oauth_token_user_id;

alter table oauth_token drop constraint if exists fk_oauth_token_client_id;
drop index if exists ix_oauth_token_client_id;

alter table token_action drop constraint if exists fk_token_action_target_user_id;
drop index if exists ix_token_action_target_user_id;

alter table users_security_role drop constraint if exists fk_users_security_role_users;
drop index if exists ix_users_security_role_users;

alter table users_security_role drop constraint if exists fk_users_security_role_security_role;
drop index if exists ix_users_security_role_security_role;

alter table users_user_permission drop constraint if exists fk_users_user_permission_users;
drop index if exists ix_users_user_permission_users;

alter table users_user_permission drop constraint if exists fk_users_user_permission_user_permission;
drop index if exists ix_users_user_permission_user_permission;

alter table users_groups drop constraint if exists fk_users_groups_users;
drop index if exists ix_users_groups_users;

alter table users_groups drop constraint if exists fk_users_groups_groups;
drop index if exists ix_users_groups_groups;

alter table user_data_source_read drop constraint if exists fk_user_data_source_read_user_id;
drop index if exists ix_user_data_source_read_user_id;

alter table user_data_source_read drop constraint if exists fk_user_data_source_read_data_source_id;
drop index if exists ix_user_data_source_read_data_source_id;

alter table user_data_source_write drop constraint if exists fk_user_data_source_write_user_id;
drop index if exists ix_user_data_source_write_user_id;

alter table user_data_source_write drop constraint if exists fk_user_data_source_write_data_source_id;
drop index if exists ix_user_data_source_write_data_source_id;

drop table if exists client;

drop table if exists client_users;

drop table if exists data_source;

drop table if exists groups;

drop table if exists group_data_source_read;

drop table if exists group_data_source_write;

drop table if exists linked_account;

drop table if exists oauth_code;

drop table if exists oauth_token;

drop table if exists security_role;

drop table if exists token_action;

drop table if exists users;

drop table if exists users_security_role;

drop table if exists users_user_permission;

drop table if exists users_groups;

drop table if exists user_data_source_read;

drop table if exists user_data_source_write;

drop table if exists user_permission;

