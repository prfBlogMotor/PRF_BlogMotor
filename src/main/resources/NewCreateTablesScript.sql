CREATE TABLE "user"

(
  
"id" bigserial,
  
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
  
"id" bigserial,
  
"date" timestamp without time zone,
  
"userid" bigint,
  
CONSTRAINT pk_visiting_id PRIMARY KEY ("id"),
  
CONSTRAINT fk_user_id FOREIGN KEY ("userid")
      REFERENCES "user" ("id")

)

WITH (
  OIDS=FALSE
);

ALTER TABLE "visiting"
  OWNER TO postgres;

CREATE TABLE "taxonomy"

(
  
"id" bigserial,
  
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
  
"id" bigserial,

"title" character varying(100),
  
"text" text,
  
"date" timestamp without time zone,
  
"userid" bigint,
  
CONSTRAINT pk_post_id PRIMARY KEY ("id"),
  
CONSTRAINT fk_post_user FOREIGN KEY ("userid")
      REFERENCES "user" ("id")

)

WITH (
  OIDS=FALSE
);

ALTER TABLE "post"
  OWNER TO postgres;

CREATE TABLE "comment"

(
  
"id" bigserial,
  
"comment" text,
  
"date" timestamp without time zone,
  
"userid" bigint,
  
"postid" bigint,
  
CONSTRAINT pk_comment_id PRIMARY KEY ("id"),
  
CONSTRAINT pk_comment_post FOREIGN KEY ("postid")
      REFERENCES "post" ("id"),
  
CONSTRAINT pk_comment_user FOREIGN KEY ("userid")
      REFERENCES "user" ("id")

)

WITH (
  OIDS=FALSE
);

ALTER TABLE "comment"
  OWNER TO postgres;

CREATE TABLE "posttaxonomy"

(
  
"id" bigserial,
  
"postid" bigint,
  
"taxonomyid" bigint,
  
CONSTRAINT pk_post_taxonomy PRIMARY KEY ("id"),
  
CONSTRAINT fk_posttaxonomy_post FOREIGN KEY ("postid")
      REFERENCES "post" ("id"),
  
CONSTRAINT fk_posttaxonomy_taxonomy FOREIGN KEY ("taxonomyid")
      REFERENCES "taxonomy" ("id")

)

WITH (
  OIDS=FALSE
);

ALTER TABLE "posttaxonomy"
  OWNER TO postgres;




INSERT INTO "user"(username, password) VALUES ('admin', 'admin');
INSERT INTO "user"(username, password) VALUES ('user', 'user');
INSERT INTO "user"(username, password) VALUES ('user1', 'user1');

INSERT INTO visiting(date, userid) VALUES ('2014.03.20. 16:00:00', 1);
INSERT INTO visiting(date, userid) VALUES ('2014.03.21. 16:00:00', 2);
INSERT INTO visiting(date, userid) VALUES ('2014.03.22. 16:00:00', 3);
    
INSERT INTO taxonomy(categoryname) VALUES ('Hibernate');
INSERT INTO taxonomy(categoryname) VALUES ('J2EE');
INSERT INTO taxonomy(categoryname) VALUES ('Java');
INSERT INTO taxonomy(categoryname) VALUES ('Primefaces');

INSERT INTO post(title, text, date, userid) VALUES ('Working on J2EE', 'Working on J2EE dasdadsasdasda', '2014.04.01. 18:00:00', 1);
INSERT INTO post(title, text, date, userid) VALUES ('Working with Hibernate', 'Working with Hibernate sdasdasfasf', '2014.04.02. 18:00:00', 2);
INSERT INTO post(title, text, date, userid) VALUES ('Working with Java', 'Working with Java sadfafasf adfas', '2014.04.03. 18:00:00', 3);
INSERT INTO post(title, text, date, userid) VALUES ('Working with J2EE and Primefaces', 'Working with J2EE and Primefaces asd dasda a ', '2014.04.04. 18:00:00', 3);

INSERT INTO posttaxonomy(postid, taxonomyid) VALUES (1, 2);
INSERT INTO posttaxonomy(postid, taxonomyid) VALUES (2, 1);
INSERT INTO posttaxonomy(postid, taxonomyid) VALUES (3, 3);
INSERT INTO posttaxonomy(postid, taxonomyid) VALUES (4, 2);
INSERT INTO posttaxonomy(postid, taxonomyid) VALUES (4, 4);

INSERT INTO comment(comment, date, userid, postid) VALUES ('It is a recommened article.', '2014.04.02 16:00:00', 1, 1);
INSERT INTO comment(comment, date, userid, postid) VALUES ('I aggree with it', '2014.04.03 16:00:00', 2, 1);
INSERT INTO comment(comment, date, userid, postid) VALUES ('Nice', '2014.04.02 16:00:00', 3, 1);
