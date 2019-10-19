create sequence survey_seq;

create table survey(id integer, appname varchar(100), appversion varchar(100), androidversion varchar(100), androidlanguage  varchar(100), deviceinfo varchar(100), useremail varchar(100), username varchar(100), day int, month int, year int, hour int, minute int);
alter table survey add constraint survey_pk  primary key (id) ;
cluster survey using survey_pk;

create table item(id integer, key varchar(100), prop varchar(100), value varchar(255));
alter table item add constraint item_pk  primary key (id, key, prop);
cluster item using item_pk;
alter table item add constraint item_id_fk foreign key(id) references survey(id);

