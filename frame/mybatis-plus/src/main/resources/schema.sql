CREATE TABLE t_user (
    `u_id` BIGINT auto_increment NOT NULL,
    `u_name` VARCHAR(32) NOT NULL,
    `u_gender` INT NOT NULL DEFAULT 0,
	CONSTRAINT u_pk PRIMARY KEY (u_id)
);

CREATE TABLE t_tenant_user (
    `u_id` BIGINT auto_increment NOT NULL,
    `u_name` VARCHAR(32) NOT NULL,
    `r_id` BIGINT NOT NULL,
    `tenant_id` BIGINT NOT NULL,
	CONSTRAINT u_tenant_pk PRIMARY KEY (u_id)
);

CREATE TABLE t_tenant_role (
    `r_id` BIGINT auto_increment NOT NULL,
    `r_name` VARCHAR(32) NOT NULL,
    `tenant_id` BIGINT NOT NULL,
    CONSTRAINT r_pk PRIMARY KEY (r_id)
);

CREATE TABLE t_dt_1 (
    `u_id` BIGINT auto_increment NOT NULL,
    `u_name` VARCHAR(32) NOT NULL,
	CONSTRAINT u_dt1_pk PRIMARY KEY (u_id)
);
CREATE TABLE t_dt_2 (
    `u_id` BIGINT auto_increment NOT NULL,
    `u_name` VARCHAR(32) NOT NULL,
	CONSTRAINT u_dt2_pk PRIMARY KEY (u_id)
);

CREATE TABLE t_lock_user (
    `u_id` BIGINT auto_increment NOT NULL,
    `u_name` VARCHAR(32) NOT NULL,
    `v_lock` BIGINT NOT NULL,
	CONSTRAINT u_lock_pk PRIMARY KEY (u_id)
);