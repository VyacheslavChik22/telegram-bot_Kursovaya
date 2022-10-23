-- liquibase formatted sql

-- changeset vyacheslav:1
CREATE TABLE notification_task (

      Id SERIAL primary key ,
      textNotification TEXT,
      timeNotification timestamp
);
-- changeset vyacheslav:2

ALTER TABLE notification_task ADD chatId INTEGER;