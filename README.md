### Instruction

This is Replicated (using 2-Phase Commit) Multi-threaded Key-Value Store using RPC implemntation of project3 of course CS6550.
It is different from two ways from single server-client connection. 
Key-Value Store Servers are across 5 distinct servers to increase Server bandwidth and ensure availability, by replicating key-value store at each of 5 different instances of servers.   
Note that client code has not been changed radically, only in that clients should be able to contact any of the five KV replica servers instead of a single server and get consistent data back from any of the replicas (in the case of GETs).  
Client should also be able to issue PUT operations and DELETE operations to any of the five replicas.   
On PUT or DELETE operations, servers ensure each of the replicated KV stores at each replica is consistent, by implementing a two-phase protocol for updates. 
The project assumes no servers will fail such that 2 Phase Commit will not stall. 
Consequently, whenever a client issues a PUT or a DELETE to *any* server replica, that receiving replica will ensure the updates have been received and commited.  

It's now extended to implement PAXOS algorithms with proposer and acceptors. There are assumptions about this project:

* The server receiving the request from the client is the proposer.

* The proposer will not fail.

* There are three status of servers, PREPARED: the proposer prepares the vote to acceptors, PROMISED: acceptors promise the vote, ACCEPTED: the proposal is about to be committed.

* Rejection happens when concurrence of requests happens, low vote will be left.

The server now can be extended to more than 5 servers running concurrently.

#### Go to directory

```
cd src
``` 

#### Compile

```
javac client/*.java server/*.java service/*.java
```

#### Run Servers with different terminals

The first argument is the port of running port, and the followings are the existing ports.

```
java server.ServerNode 1000
```
```
java server.ServerNode 1001 1000
```
```
java server.ServerNode 1002 1000 1001
```
```
java server.ServerNode 1003 1000 1001 1002
```
```
java server.ServerNode 1004 1000 1001 1002 1003
```

#### Run Client

By using customer port number and hostname
```
java client.Client localhost 1000
```

#### Scenario

* When servers start in five terminals, one client can run in one terminal to connect with server in port 1000(as above settings), if it issues a put or delete operation with the key, another client connecting port 1002 can get with the key.

* If server on 1003 is down, the rest are still running for requests.

* If server on 1003 recovers, it restores the key value store from other running servers.

#### Delete *.class Files
```
rm client/*.class server/*.class service/*.class
```