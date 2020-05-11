# --- !Ups

CREATE TABLE "User"(
    "id" SERIAL NOT NULL PRIMARY KEY,
    "username" VARCHAR NOT NULL,
    "password" VARCHAR NOT NULL,
    "email" VARCHAR NOT NULL
);

# --- !Downs
DROP TABLE "User";