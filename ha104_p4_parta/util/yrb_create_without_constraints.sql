
-- The following SQL statements create the same schema as the YRB schema without 
-- foreign key constraints. You can use these tables to create and test your triggers.
-- The tables have different names from the YRB tables since the YRB tables are used in the second part.

-- The part A is to practice and learn triggers which will be included in the final exam.

-- Author : Nasim Razavi

create table new_category as (select * from yrb_category) with data;
    alter table new_category add constraint new_category_pk primary key (cat);
create table new_club as (select * from yrb_club) with data;
    alter table new_club add constraint new_club_pk primary key (club);
create table new_member as (select * from yrb_member) with data;
    alter table new_member add constraint new_member_pk primary key (club,cid);
create table new_offer as (select * from yrb_offer) with data;
    alter table new_offer add constraint new_offer_pk primary key (club, title, year);
create table new_purchase as (select * from yrb_purchase) with data;
    alter table new_purchase add constraint new_purchase_pk primary key (cid, club, title, year, when);
create table new_shipping as (select * from yrb_shipping) with data;
    alter table new_shipping add constraint new_shipping_pk primary key (weight);