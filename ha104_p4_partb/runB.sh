# -- Name: Hassaan Abid
# -- ID: 214243935
# -- EECS Account: ha104
# -- Part B - run application script

#  Incase of permission error execute: chmod -R +x ./
#   this will recursively make all files under current directory executeable

# connect to database
db2 connect to c3421a

# delete existing schema
db2 -tf ./util/yrb_drop.sql

# create new schema
db2 -tf ./util/yrb_create.sql

# setup enviorment variables
source ~db2leduc/cshrc.runtime

# compile java files
javac Connect.java YRBQueries.java YRB.java

clear

# run the program
java YRB

