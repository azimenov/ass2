create table if not exists public.user
(
    client_id     varchar(50) unique,
    client_secret varchar(100),
    scope         varchar[]
    );
