create table if not exists users (
    user_id bigserial primary key,
    login_id varchar(100) not null unique,
    password_hash varchar(200) not null,
    nickname varchar(50) not null,
    user_code varchar(6) not null unique,
    created_at timestamptz not null
);

create table if not exists refresh_tokens (
    token_id varchar(36) primary key,
    user_id bigint not null,
    expires_at timestamptz not null,
    revoked_at timestamptz null,
    created_at timestamptz not null
);

create index if not exists idx_refresh_tokens_user on refresh_tokens (user_id);

create table if not exists login_security (
    login_id varchar(100) primary key,
    failed_count int not null,
    locked_until timestamptz null,
    updated_at timestamptz not null
);

create table if not exists login_audit (
    audit_id bigserial primary key,
    login_id varchar(100) null,
    user_id bigint null,
    success boolean not null,
    reason varchar(50) null,
    ip varchar(45) null,
    user_agent varchar(255) null,
    occurred_at timestamptz not null
);

create index if not exists idx_login_audit_login_id on login_audit (login_id);
create index if not exists idx_login_audit_user_id on login_audit (user_id);

create table if not exists friendships (
    requester_user_id bigint not null,
    target_user_id bigint not null,
    status varchar(20) not null,
    requested_at timestamptz not null,
    accepted_at timestamptz null,
    primary key (requester_user_id, target_user_id)
);

create index if not exists idx_friendships_target on friendships (target_user_id);
create index if not exists idx_friendships_requester on friendships (requester_user_id);

create table if not exists conversations (
    conversation_id bigserial primary key,
    type varchar(20) not null,
    active boolean not null,
    created_at timestamptz not null
);

create table if not exists conversation_members (
    conversation_id bigint not null,
    user_id bigint not null,
    role varchar(20) not null,
    primary key (conversation_id, user_id)
);

create index if not exists idx_conversation_members_user on conversation_members (user_id);

create table if not exists messages (
    message_id bigserial primary key,
    conversation_id bigint not null,
    sender_user_id bigint not null,
    content varchar(2000) not null,
    client_message_key varchar(100) not null,
    created_at timestamptz not null,
    unique (sender_user_id, client_message_key)
);

create index if not exists idx_messages_conversation_message_id on messages (conversation_id, message_id desc);
create index if not exists idx_messages_conversation_sender on messages (conversation_id, sender_user_id, message_id desc);

create table if not exists message_states (
    message_id bigint not null,
    user_id bigint not null,
    state varchar(20) not null,
    created_at timestamptz not null,
    primary key (message_id, user_id)
);

create index if not exists idx_message_states_user_message on message_states (user_id, message_id);

create table if not exists sync_cursors (
    conversation_id bigint not null,
    user_id bigint not null,
    last_delivered_message_id bigint null,
    last_read_message_id bigint null,
    version bigint not null,
    primary key (conversation_id, user_id)
);

create index if not exists idx_sync_cursors_user on sync_cursors (user_id);
