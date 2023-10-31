create table if not exists guilds
(
    id   varchar(32)  not null,
    name varchar(255) not null,

    primary key (id)
);

create table if not exists guild_settings
(
    guild_id                varchar(32) not null,
    signoff_channel_id      varchar(32),
    sicknote_channel_id     varchar(32),
    promote_info_channel_id varchar(32),
    is_welcome_enabled      boolean     not null,
    welcome_channel_id      varchar(32),
    welcome_message         varchar(255),
    is_default_role_enabled boolean     not null,
    default_roles           varchar(255),

    primary key (guild_id),
    foreign key (guild_id) references guilds (id)
);

create table if not exists members
(
    guild_id    varchar(32)  not null,
    user_id     varchar(32)  not null,
    permissions varchar(255) not null,

    primary key (guild_id, user_id),
    foreign key (guild_id) references guilds (id)
);

create table if not exists roles
(
    guild_id    varchar(32)  not null,
    role_id     varchar(32)  not null,
    permissions varchar(255) not null,

    primary key (guild_id, role_id),
    foreign key (guild_id) references guilds (id)
);