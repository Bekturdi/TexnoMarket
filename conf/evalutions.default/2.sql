# --- !Ups

CREATE TABLE "Phone"(
    "id" SERIAL NOT NULL PRIMARY KEY,
    "phoneName" VARCHAR NOT NULL,
    "phoneModel" VARCHAR NOT NULL,
    "phoneRam" VARCHAR NOT NULL,
    "phoneHdd" VARCHAR NOT NULL,
    "phonePrice" VARCHAR NOT NULL
);

# --- !Downs
DROP TABLE "Phone";