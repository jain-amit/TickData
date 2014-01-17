TickData
========

Processes 15 second tick data file to store into a database and to create a minute data tick data file.

Storing into a database allows to recreate tick data for analysis.
Using 15 second tick data allows for greater flexibility and comprehensive analysis.

The file created of one minute tick data is to be able to do offline historical analysis.
The data would be appended to monthly data file that at end of the month would be appended to yearly data file.
