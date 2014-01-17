TickData
========

Processes and stores 15 second tick data into a database and writes out minute data tick data file

The data in the database allows to recreate tick data for analysis.
By storing 15 second tick data allows for greater flexibility and comprehensive analysis.

The file of one minute tick data to be able to do offline historical analysis for that day.
The data would be appended to monthly data that at end of the month would be appended for that year.
