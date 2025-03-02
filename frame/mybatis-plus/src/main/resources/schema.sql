CREATE TABLE t_user (
    `u_id` BIGINT auto_increment NOT NULL,
    `u_name` VARCHAR(32) NOT NULL,
    `r_id` BIGINT NULL,
    `u_gender` INT NOT NULL DEFAULT 0,
	CONSTRAINT u_pk PRIMARY KEY (u_id)
);

CREATE TABLE t_role (
    `r_id` BIGINT auto_increment NOT NULL,
    `r_name` VARCHAR(32) NOT NULL,
    CONSTRAINT r_pk PRIMARY KEY (r_id)
);

CREATE TABLE t_permissions (
    `p_id` BIGINT auto_increment NOT NULL,
    `p_name` VARCHAR(16) NOT NULL,
    CONSTRAINT p_pk PRIMARY KEY (p_id)
);

CREATE TABLE t_role_permissions (
    `r_id` BIGINT NOT NULL,
    `p_id` BIGINT NOT NULL,
    CONSTRAINT rp_pk PRIMARY KEY (r_id, p_id)
);
