CREATE TABLE IF NOT EXISTS `convenience` (
    `id`            bigint       NOT NULL AUTO_INCREMENT,
    `is_free`       TINYINT(1)   NOT NULL,
    `price`         int          NOT NULL,
    `room_id`       bigint,
    `studycafe_id`  bigint,
    `name`          varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `grade` (
    `id`              bigint     NOT NULL AUTO_INCREMENT,
    `cleanliness`     int        NOT NULL,
    `deafening`       int        NOT NULL,
    `fixtures_status` int        NOT NULL,
    `is_recommended`  TINYINT(1) NOT NULL,
    `total`           double     NOT NULL,
    `review_id`       bigint     NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `hashtag_record` (
    `id`           bigint       NOT NULL AUTO_INCREMENT,
    `count`        int,
    `review_id`    bigint       NOT NULL,
    `studycafe_id` bigint       NOT NULL,
    `name`         varchar(255) NOT NULL,
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
    `created_date`  TIMESTAMP,
    `provider_id`   bigint,
    `email`         varchar(255) NOT NULL,
    `name`          varchar(255) NOT NULL,
    `nickname`      varchar(255) NOT NULL,
    `password`      varchar(255) NOT NULL,
    `phone_number`  varchar(255),
    `photo`         varchar(255),
    `type`          varchar(255) DEFAULT 'DEFAULT',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `member_bookmark` (
    `id`           bigint NOT NULL AUTO_INCREMENT,
    `member_id`    bigint NOT NULL,
    `studycafe_id` bigint NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `member_roles` (
    `member_id` bigint       NOT NULL,
    `roles`     varchar(255)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `notice` (
    `id`           bigint       NOT NULL,
    `studycafe_id` bigint       NOT NULL,
    `detail`       varchar(255),
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `notification_info` (
    `id`           bigint       NOT NULL,
    `end_date`     date,
    `start_date`   date,
    `studycafe_id` bigint       NOT NULL,
    `detail`       varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `operation_info` (
    `id`           bigint     NOT NULL,
    `all_day`      TINYINT(1) NOT NULL,
    `closed`       TINYINT(1) NOT NULL,
    `end_time`     TIME,
    `start_time`   TIME,
    `studycafe_id` bigint     NOT NULL,
    `week` varchar(255)       NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `payment` (
    `id`             bigint       NOT NULL AUTO_INCREMENT,
    `price`          int          NOT NULL,
    `cancel_reason`  varchar(255),
    `complete_time`  varchar(255),
    `method`         varchar(255) NOT NULL,
    `order_id`       varchar(255) NOT NULL,
    `payment_key`    varchar(255) NOT NULL,
    `payment_status` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `refund_policy` (
    `id`           bigint       NOT NULL AUTO_INCREMENT,
    `rate`         int          NOT NULL,
    `refund_day`   varchar(255) NOT NULL,
    `type`         varchar(255),
    `studycafe_id` bigint       NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `reservation_record` (
    `id`               bigint       NOT NULL AUTO_INCREMENT,
    `complete_payment` TINYINT(1),
    `date`             date         NOT NULL,
    `duration`         int          NOT NULL,
    `start_time`       time         NOT NULL,
    `end_time`         time         NOT NULL,
    `head_count`       int          NOT NULL,
    `name`             varchar(255) NOT NULL,
    `order_id`         varchar(255),
    `phone_number`     varchar(255) NOT NULL,
    `request`          varchar(255),
    `status`           varchar(255) NOT NULL,
    `member_id`        bigint       NOT NULL,
    `payment_id`       bigint,
    `room_id`          bigint       NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `review` (
    `id`             bigint       NOT NULL AUTO_INCREMENT,
    `created_date`   date         NOT NULL,
    `comment`        varchar(255),
    `detail`         varchar(255),
    `reservation_id` bigint       NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `room` (
    `id`                  bigint       NOT NULL AUTO_INCREMENT,
    `max_head_count`      int          NOT NULL,
    `min_head_count`      int          NOT NULL,
    `min_using_time`      int          NOT NULL,
    `price`               int          NOT NULL,
    `standard_head_count` int,
    `name`                varchar(255) NOT NULL,
    `photo`               varchar(255), -- 머지 ?
    `type`                varchar(255) NOT NULL,
    `studycafe_id`        bigint,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `studycafe` (
    `id`                  bigint       NOT NULL AUTO_INCREMENT,
    `accum_reserve_count` int          NOT NULL, --처음에 0? 그럼 그냥 default?
    `duration`            int, --?
    `total_grade`         double,
    `created_at`          datetime(6),
    `member_id`           bigint,
    `basic`               varchar(255), --모지 ?
    `detail`              varchar(255),
    `introduction`        varchar(255),
    `name`                varchar(255) NOT NULL,
    `nearest_station`     varchar(255),
    `phone_number`        varchar(255), --스카 번호 ? 근데 폰 번호는 이름이 이상하지 얺너
    `photo`               varchar(255) NOT NULL,
    `zipcode`             varchar(255),
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `sub_photo` (
    `id`           bigint       NOT NULL AUTO_INCREMENT,
    `url`          varchar(255),
    `studycafe_id` bigint, --타입 지정하는 컬럼 추가 하기로 하지 않았나 id null 겁사로만 하는건 위험 ?
    `review_id`    bigint,
    `room_id`      bigint,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--이거 뭔지 ?
alter table grade
    add constraint UK_we0avakghgh4ao6asasoe4se unique (review_id)
    Hibernate:
alter table reservation_record
    add constraint UK_hs9rx6h5qvlbscyo17mykcxyk unique (payment_id)
    Hibernate:
alter table review
    add constraint UK_hyxvthxr4ats27c7vko0rb4xg unique (reservation_id)