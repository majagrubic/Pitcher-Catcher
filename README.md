Pitcher-Catcher
===============

Simple program for using Java sockets;

Pitcher creates messages at a given rate and sends them to catcher. 
Catcher "catches" the message and returns it to the Pitcher. Pitcher displays time statistics.



Program uses JComannder for line arguments parsing.


Usage:
<pre>
-p Pitcher mode; 
-c Cather mode;
-port <port> [Pitcher] TCP socket port used for connect, [Catcher] TCP socket port
 used for listening;
-bind <ip_address> [Catcher] TCP socket bind address used for listening;
-mps <rate> [Pitcher] send rate defined as „messages per second“ Default: 1;
-size <size> [Pitcher] Packet size, Minimum: 50, Maximum: 3000, Default: 300;
<hostname> [Pitcher] name of the machine on which Catcher runs;
</pre>
