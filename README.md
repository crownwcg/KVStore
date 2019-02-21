### Instruction

#### Go to directory

```
cd src
``` 

#### Compile

```
javac client/*.java server/*.java service/*.java rpc/*.java
```

#### Run TCP Server

By using default settings
```
java server.TCPServer
```

By using customer port number
```
java server.TCPServer 8000
```

#### Run TCP Client

By using default settings
```
java client.TCPClient
```

By using customer port number and hostname
```
java client.TCPClient localhost 8000
```

#### Run UDP Server

By using default settings
```
java server.UDPServer
```

By using customer port number
```
java server.UDPServer 8080
```

#### Run UDP Client

By using default settings
```
java client.UDPClient
```

By using customer port number and hostname
```
java client.UDPClient localhost 8080
```
#### Run RMI Server

By using default settings
```
java rpc.RMIServer
```

By using customer port number
```
java rpc.RMIServer 9000
```

#### Run RMI Client

By using default settings
```
java rpc.RMIClient
```

By using customer port number and hostname
```
java rpc.RMIClient localhost 9000
```

The test cases is hardcoded as 
```
String[] msgs = new String[]{
        "PUT,a,1",
        "GET,a",
        "PUT,b,2",
        "PUT,c,3",
        "DELETE,a",
        "PUT,a,4",
        "PUT,d,4",
        "GET,a",
        "GET,b",
        "GET,c",
        "GET,d",
        "DELETE,a",
        "DELETE,b",
        "DELETE,c",
        "DELETE,d"
};
```

After running
```
rm client/*.class server/*.class service/*.class rpc/*.class
```