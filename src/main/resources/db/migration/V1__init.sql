CREATE TABLE IF NOT EXISTS `convenience` (
    `id`            bigint       NOT NULL AUTO_INCREMENT,
    `is_free`       TINYINT(1)   NOT NULL,
    `price`         int          NOT NULL,
    `type`          varchar(255) NOT NULL,
    `room_id`       bigint,
    `studycafe_id`  bigint,
    `name`          varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `grade` (
    `id`              bigint       NOT NULL AUTO_INCREMENT,
    `cleanliness`     int          NOT NULL,
    `deafening`       int          NOT NULL,
    `fixtures_status` int          NOT NULL,
    `total`           decimal(3,2) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `member` (
    `id`            bigint       NOT NULL AUTO_INCREMENT,
    `resigned_date` DATE,
    `usable`        TINYINT(1)   NOT NULL,
    `birthday`      DATE,
    `created_date`  TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    `provider_id`   bigint,
    `email`         varchar(255) NOT NULL,
    `name`          varchar(255),
    `nickname`      varchar(255) NOT NULL,
    `password`      varchar(255),
    `phone_number`  varchar(255) NOT NULL,
    `photo`         varchar(255),
    `type`          varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `bookmark` (
    `id`           bigint NOT NULL AUTO_INCREMENT,
    `member_id`    bigint NOT NULL,
    `studycafe_id` bigint NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `role` (
    `id`        bigint       NOT NULL AUTO_INCREMENT,
    `member_id` bigint       NOT NULL,
    `value`     varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `notice` (
    `id`           bigint       NOT NULL AUTO_INCREMENT,
    `studycafe_id` bigint       NOT NULL,
    `detail`       varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `announcement` (
    `id`           bigint       NOT NULL AUTO_INCREMENT,
    `end_date`     date         NOT NULL,
    `start_date`   date         NOT NULL,
    `detail`       varchar(255) NOT NULL,
    `studycafe_id` bigint       NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `operation_info` (
    `id`           bigint       NOT NULL AUTO_INCREMENT,
    `is_all_day`   TINYINT(1)   NOT NULL,
    `closed`       TINYINT(1)   NOT NULL,
    `end_time`     TIME,
    `start_time`   TIME,
    `week`         varchar(255) NOT NULL,
    `studycafe_id` bigint       NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `payment` (
    `id`                    bigint       NOT NULL AUTO_INCREMENT,
    `order_id`              varchar(255) NOT NULL,
    `payment_key`           varchar(255),
    `method`                varchar(255),
    `complete_time`         TIMESTAMP,
    `price`                 int          NOT NULL,
    `status`        varchar(255) NOT NULL,
    `cancel_reason`         varchar(255),
    `canceler`              varchar(255),
    `virtual_account`       varchar(255),
    `bank_code`             varchar(255),
    `due_date`              TIMESTAMP,
    `secret`                varchar(255),
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `refund_policy` (
    `id`           bigint       NOT NULL AUTO_INCREMENT,
    `rate`         int          NOT NULL,
    `remaining`    varchar(255) NOT NULL,
    `type`         varchar(255) NOT NULL,
    `studycafe_id` bigint       NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `reservation_record` (
    `id`                bigint       NOT NULL AUTO_INCREMENT,
    `date`              date         NOT NULL,
    `using_time`        int          NOT NULL, -- 시간 단위
    `start_time`        time         NOT NULL,
    `end_time`          time         NOT NULL,
    `head_count`        int          NOT NULL,
    `user_name`         varchar(255) NOT NULL,
    `order_id`          varchar(255),
    `user_phone_number` varchar(255) NOT NULL,
    `request`           varchar(255),
    `status`            varchar(255) NOT NULL,
    `member_id`         bigint       NOT NULL,
    `room_id`           bigint       NOT NULL,
    `review_id`         bigint,
    `payment_id`        bigint,      NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `convenience_record` (
    `id`                    bigint       NOT NULL AUTO_INCREMENT,
    `reservation_record_id` bigint       NOT NULL,
    `convenience_name`      varchar(255) NOT NULL,
    `price`                 varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `review` (
    `id`                    bigint       NOT NULL AUTO_INCREMENT,
    `created_date`          date         NOT NULL,
    `comment`               varchar(255), -- 사장님 댓글
    `detail`                varchar(255) NOT NULL,
    `photo`                 varchar(255),
    `is_recommended`        TINYINT(1)   NOT NULL,
    `grade_id`              bigint       NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `hashtag_record` (
    `id`           bigint       NOT NULL AUTO_INCREMENT,
    `name`         varchar(255) NOT NULL,
    `review_id`    bigint       NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `room` (
    `id`                  bigint       NOT NULL AUTO_INCREMENT,
    `name`                varchar(255) NOT NULL,
    `min_head_count`      int          NOT NULL,
    `max_head_count`      int          NOT NULL,
    `min_using_time`      int          NOT NULL,
    `price`               int          NOT NULL,
    `price_type`          varchar(255) NOT NULL,
    `studycafe_id`        bigint       NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `studycafe` (
    `id`                  bigint       NOT NULL AUTO_INCREMENT,
    `name`                varchar(255) NOT NULL,
    `accum_reserve_count` int          DEFAULT '0',
    `total_grade`         decimal(3,2) DEFAULT '0',
    `created_date`        TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    `address_basic`       varchar(255) NOT NULL,
    `address_detail`      varchar(255) NOT NULL,
    `address_zipcode`     varchar(255) NOT NULL,
    `introduction`        varchar(255) NOT NULL,
    `walking_time`        int,
    `nearest_station`     varchar(255),
    `tel`                 varchar(255) NOT NULL,
    `photo`               varchar(255) NOT NULL,
    `member_id`           bigint       NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `accum_hashtag_history` (
    `id`           bigint       NOT NULL AUTO_INCREMENT,
    `count`        int          NOT NULL,
    `name`         varchar(255) NOT NULL,
    `studycafe_id` bigint       NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `sub_photo` (
    `id`           bigint       NOT NULL AUTO_INCREMENT,
    `path`         varchar(255) NOT NULL,
    `type`         varchar(255) NOT NULL,
    `studycafe_id` bigint,
    `review_id`    bigint,
    `room_id`      bigint,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

alter table review add constraint grade_review_unique_key unique (grade_id);

alter table reservation_record add constraint review_reservation_record_unique_key unique (review_id);