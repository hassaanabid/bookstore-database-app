-- *********************************************************
-- EECS3421B
-- Project 4 Part A (Triggers)
-- Name: Hassaan Abid
-- ID: 214243935
-- EECS Account: ha104
-- *********************************************************

-- Examples/Tests are located in file: trigger_test_examples.sql
-- NOTE: execute bash script: runA.sh to test these triggers

--#SET TERMINATOR @
-- trigger #1
-- on delete book cascade delete its offers as well
create or replace trigger cleanUpOffers
after delete on yrb_book
referencing
    old as O
for each row mode db2sql
begin atomic
    DELETE FROM new_offer AS Y WHERE Y.title = O.title and Y.year = O.year ;
end@


-- trigger #2
-- reject insertion into purchase if customer is not a member of offering club
create or replace trigger rejectNonMemberPurchase
before insert on new_purchase
referencing
    new as N
for each row mode db2sql
when (
    N.cid not in
(SELECT	M.cid from new_member AS M where M.club = N.club))
begin atomic
SIGNAL SQLSTATE '80000' 
('Error: Purchase is invalid since customer is not a member');
end@

-- trigger #3
-- on insert customer, add customer to club 'Basic' as well
create or replace trigger addToClubBasic
after insert on yrb_customer
referencing
    new as N
for each row mode db2sql
when (
    N.cid not in
(SELECT	cid from new_member where club = 'Basic'))
begin atomic
    insert into new_member (club, cid) values('Basic', N.cid) ;
end@
