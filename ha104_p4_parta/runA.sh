
# -- Name: Hassaan Abid
# -- ID: 214243935
# -- EECS Account: ha104

#  Incase of permission error execute: chmod -R +x ./
#   this will recursively make all files under current directory executeable

# connect to database
db2 connect to c3421a

# delete existing schema
db2 -tf ./util/yrb_drop.sql

db2 -tf ./util/drop_yrb_without_constraints.sql

# create new schema
db2 -tf ./util/yrb_create.sql

# create modified verion of schema for testing
db2 -tf ./util/yrb_create_without_constraints.sql

# create triggers
db2 -tf const_trig.sql

# test triggers
db2 -tf trigger_test_examples.sql


