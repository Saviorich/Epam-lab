DROP TABLE IF EXISTS `gift_certificate`;
CREATE TABLE IF NOT EXISTS `gift_certificate` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(200) NOT NULL,
    `description` VARCHAR(255) NOT NULL,
    `price` DECIMAL(10,2) NOT NULL,
    `duration` INT NOT NULL,
    `create_date` DATE NOT NULL,
    `last_update_date` DATE NOT NULL,
    PRIMARY KEY (`id`));

DROP TABLE IF EXISTS `tag`;
CREATE TABLE IF NOT EXISTS `tag` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(200) NOT NULL UNIQUE,
    PRIMARY KEY (`id`));

DROP TABLE IF EXISTS `gift_certificate_tag`;
CREATE TABLE IF NOT EXISTS `gift_certificate_tag` (
    `gift_certificate_id` INT NOT NULL,
    `tag_id` INT NOT NULL,
    PRIMARY KEY (`gift_certificate_id`, `tag_id`));

DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
    id int auto_increment primary key,
    email varchar(150) null,
    hash varchar(150) null,
    registration_date datetime null
);

DROP TABLE IF EXISTS `order`;
CREATE TABLE IF NOT EXISTS `order` (
    id int auto_increment primary key,
    user_id int            null,
    date    datetime       null,
    cost    decimal(10, 2) null,
    constraint user_id foreign key (user_id) references `user` (id)
);

CREATE INDEX user_id_idx
    ON `order` (user_id);

DROP TABLE IF EXISTS `order_gift_certificate`;
CREATE TABLE IF NOT EXISTS `order_gift_certificate`
(
    order_id            int not null,
    gift_certificate_id int not null,
    primary key (order_id, gift_certificate_id),
    constraint fk_order_has_gift_certificate_gift_certificate1
        foreign key (gift_certificate_id) references `gift_certificate` (`id`),
    constraint fk_order_has_gift_certificate_order1
        foreign key (order_id) references `order` (id)
);

CREATE INDEX fk_order_has_gift_certificate_gift_certificate1_idx
    on `order_gift_certificate` (gift_certificate_id);

CREATE INDEX fk_order_has_gift_certificate_order1_idx
    ON `order_gift_certificate` (order_id);


// INITIALIZATION

SET REFERENTIAL_INTEGRITY = FALSE;

INSERT INTO `gift_certificate` (id, name, description, price, duration, create_date, last_update_date)
VALUES (1, 'gift 1', 'description 1', 10.99, 10, NOW(), NOW()),
       (2, 'gift 2', 'description 2', 19.99, 20, NOW(), NOW()),
       (3, 'gift 3', 'description 3', 29.99, 30, NOW(), NOW()),
       (4, 'gift 4', 'description 4', 39.02, 1, NOW(), NOW()),
       (5, 'gift 5', 'description 4', 39.02, 1, NOW(), NOW());


INSERT INTO `tag`
VALUES (1, 'game'),
       (2, 'beauty'),
       (3, 'food'),
       (4, 'shooter'),
       (5, 'singleplayer'),
       (6, 'multiplayer'),
       (7, 'cosmetic'),
       (8, 'natural');

INSERT INTO `gift_certificate_tag` (gift_certificate_id, tag_id)
VALUES (1, 1),
       (1, 4),
       (1, 6),
       (2, 3),
       (3, 2),
       (3, 7),
       (3, 8),
       (4, 1),
       (4, 5),
       (5, 1);

INSERT INTO `user` (email, hash, registration_date)
    VALUES ( 'random@mail.ru', hash('SHA256', STRINGTOUTF8('password'), 100), NOW()),
           ('google@gmail.com', hash('SHA256', STRINGTOUTF8('notapassword'), 100), NOW()),
           ('yandex@mail.com', hash('SHA256', STRINGTOUTF8('bestpassword'), 100), NOW()),
           ('good@mail.ru',  hash('SHA256', STRINGTOUTF8('bestpassword'), 100), NOW()),
           ('bad@mail.ru',  hash('SHA256', STRINGTOUTF8('bestpassword'), 100), NOW());

INSERT INTO `order` (user_id, date, cost)
    VALUES ( 1, NOW(), 124.12 ),
           (1, NOW(), 10.99),
           (2, NOW(), 214.24),
           (3, NOW(), 421.25),
           (3, NOW(), 5.99),
           (3, NOW(), 1.99),
           (4, NOW(), 999.99);


INSERT INTO `order_gift_certificate` (order_id, gift_certificate_id)
    VALUES ( 1, 2 ),
           (1, 3),
           (1, 4),
           (2, 1),
           (3, 1),
           (3, 2),
           (3, 3),
           (4, 1),
           (4, 2),
           (4, 3),
           (4, 4),
           (4, 5),
           (5, 2),
           (6, 3),
           (7, 1),
           (7, 2),
           (7, 3),
           (7, 4),
           (7, 5);