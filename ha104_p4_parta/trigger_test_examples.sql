-- *********************************************************
-- EECS3421B
-- Project 4 Part A (Triggers)
-- Trigger tests/examples
-- Name: Hassaan Abid
-- ID: 214243935
-- EECS Account: ha104
-- *********************************************************

-- =============================================================================
-- Testing Trigger # 1 
-- By deleing the following book 
-- title = 'Acrimony Made Simple' and year = 1992"
-- expected result: 
--                      1. The book should be deleted 
--                      2. All offers for this book should also be deleted

-- This drop is necessary to test trigger # 1
drop table YRB_OFFER;

-- make sure book is present
select * from yrb_book where title = 'Acrimony Made Simple' and year = 1992;

-- make sure book has offers
select * from new_offer where title = 'Acrimony Made Simple' and year = 1992;

-- delete the book
delete from yrb_book where title = 'Acrimony Made Simple' and year = 1992;

-- check if all the others also got deleted
select * from new_offer where title = 'Acrimony Made Simple' and year = 1992;

-- =============================================================================
-- Testing Trigger # 2
-- reject insertion into purchase if customer is not a member of offering club 

-- see who is and isnt member of club 'AAA', cid 2 is while 1 isnt.
select * from new_member where club = 'AAA';

-- make sure insertion is sucessfull since cid = 2 is a member of club = 'AAA'
insert into new_purchase values
    (2, 'AAA', 'Richmond Underground','1997', '2019-11-29-04.55.46.656000', 2);

-- insertion should fail since cid = 1 is not a member of club = 'AAA'
-- i.e. a trigger violation should show
-- "Error: Purchase is invalid since customer is not a member".
insert into new_purchase values
(1, 'AAA', 'Richmond Underground','1997', '2019-11-29-04.55.46.656000', 2);

-- an extra check to make sure that the purchase was rejected
-- bookt with title = Richmond Underground and year = 1997 should not be among 
-- purchases of cid = 1
select * from new_purchase where cid = 1;

-- =============================================================================
-- Testing Trigger # 3
-- when a new customer is inserted make them a member of club 'Basic' 

insert into yrb_customer values (50, 'Joe Jackson', 'Boston');

-- check if customer was inserted
select * from yrb_customer where cid = 50;

-- make sure customer is now a member of club basic
select * from new_member where cid = 50 and club = 'Basic';
-- =============================================================================
