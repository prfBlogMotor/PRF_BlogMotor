CREATE TABLE "user"

(
  
"id" bigint NOT NULL,
  
"username" character varying(100),
  
"password" character varying(100),
  
CONSTRAINT pk_user_id PRIMARY KEY ("id")

)

WITH (
  OidS=FALSE
);

ALTER TABLE "user"
  OWNER TO postgres;

CREATE TABLE "visiting"

(
  
"id" bigint NOT NULL,
  
"date" timestamp without time zone,
  
"userid" bigint,
  
CONSTRAINT pk_visiting_id PRIMARY KEY ("id"),
  
CONSTRAINT fk_user_id FOREIGN KEY ("userid")
      REFERENCES "user" ("id") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION

)

WITH (
  OIDS=FALSE
);

ALTER TABLE "visiting"
  OWNER TO postgres;

CREATE TABLE "taxonomy"

(
  
"id" bigint NOT NULL,
  
"categoryname" character varying(50),
  
CONSTRAINT pk_taxonomy_id PRIMARY KEY ("id")

)

WITH (
  OIDS=FALSE
);

ALTER TABLE "taxonomy"
  OWNER TO postgres;

CREATE TABLE "post"

(
  
"id" bigint NOT NULL,

"title" character varying(100),
  
"text" text,
  
"date" timestamp without time zone,
  
"userid" bigint,
  
CONSTRAINT pk_post_id PRIMARY KEY ("id"),
  
CONSTRAINT fk_post_user FOREIGN KEY ("userid")
      REFERENCES "user" ("id") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION

)

WITH (
  OIDS=FALSE
);

ALTER TABLE "post"
  OWNER TO postgres;

CREATE TABLE "comment"

(
  
"id" bigint NOT NULL,
  
"comment" text,
  
"date" timestamp without time zone,
  
"userid" bigint,
  
"postid" bigint,
  
CONSTRAINT pk_comment_id PRIMARY KEY ("id"),
  
CONSTRAINT pk_comment_post FOREIGN KEY ("postid")
      REFERENCES "post" ("id") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  
CONSTRAINT pk_comment_user FOREIGN KEY ("userid")
      REFERENCES "user" ("id") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION

)

WITH (
  OIDS=FALSE
);

ALTER TABLE "comment"
  OWNER TO postgres;

CREATE TABLE "posttaxonomy"

(
  
"id" bigint NOT NULL,
  
"postid" bigint,
  
"taxonomyid" bigint,
  
CONSTRAINT pk_post_taxonomy PRIMARY KEY ("id"),
  
CONSTRAINT fk_posttaxonomy_post FOREIGN KEY ("postid")
      REFERENCES "post" ("id") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  
CONSTRAINT fk_posttaxonomy_taxonomy FOREIGN KEY ("taxonomyid")
      REFERENCES "taxonomy" ("id") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION

)

WITH (
  OIDS=FALSE
);

ALTER TABLE "posttaxonomy"
  OWNER TO postgres;




INSERT INTO "user"( id, username, password) VALUES (0, 'admin', 'admin');
INSERT INTO "user"( id, username, password) VALUES (1, 'user', 'user');
INSERT INTO "user"( id, username, password) VALUES (2, 'user1', 'user1');

INSERT INTO visiting(id, date, userid) VALUES (0, '2014.03.20. 16:00:00', 0);
INSERT INTO visiting(id, date, userid) VALUES (1, '2014.03.21. 16:00:00', 1);
INSERT INTO visiting(id, date, userid) VALUES (2, '2014.03.22. 16:00:00', 2);
    
INSERT INTO taxonomy(id, categoryname) VALUES (0, 'Hibernate');
INSERT INTO taxonomy(id, categoryname) VALUES (1, 'J2EE');
INSERT INTO taxonomy(id, categoryname) VALUES (2, 'Java');
INSERT INTO taxonomy(id, categoryname) VALUES (3, 'Primefaces');

INSERT INTO post(id, title, text, date, userid) VALUES (0, 'Working on J2EE', 'Working on J2EE dasdadsasdasda', '2014.04.01. 18:00:00', 0);
INSERT INTO post(id, title, text, date, userid) VALUES (1, 'Working with Hibernate', 'Working with Hibernate sdasdasfasf', '2014.04.02. 18:00:00', 1);
INSERT INTO post(id, title, text, date, userid) VALUES (2, 'Working with Java', 'Working with Java sadfafasf adfas', '2014.04.03. 18:00:00', 2);
INSERT INTO post(id, title, text, date, userid) VALUES (3, 'Working with J2EE and Primefaces', 'Working with J2EE and Primefaces asd dasda a ', '2014.04.04. 18:00:00', 2);

INSERT INTO posttaxonomy(id, postid, taxonomyid) VALUES (0, 0, 1);
INSERT INTO posttaxonomy(id, postid, taxonomyid) VALUES (1, 1, 0);
INSERT INTO posttaxonomy(id, postid, taxonomyid) VALUES (2, 2, 2);
INSERT INTO posttaxonomy(id, postid, taxonomyid) VALUES (3, 3, 1);
INSERT INTO posttaxonomy(id, postid, taxonomyid) VALUES (4, 3, 3);

INSERT INTO comment(id, comment, date, userid, postid) VALUES (0, 'It is a recommened article.', '2014.04.02 16:00:00', 0, 0);
INSERT INTO comment(id, comment, date, userid, postid) VALUES (1, 'I aggree with it', '2014.04.03 16:00:00', 1, 0);
INSERT INTO comment(id, comment, date, userid, postid) VALUES (2, 'Nice', '2014.04.02 16:00:00', 2, 0);

