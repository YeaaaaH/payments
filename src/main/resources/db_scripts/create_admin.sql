insert into users(username, password, email, first_name, last_name, created_on)
values('admin', '$2a$12$xZQ4H3c8FEUbWDC0rDCY0OmQ5J5QTG5BdVOU30LHgxAoD4vv0QD5G', 'admin@mail.admin', 'ADMIN_TEST', 'LAST_NAME_ADMIN', now());

insert into user_roles(role_id, user_id)
values (1, ?);