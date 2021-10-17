# khtunnel
**khtunnel** is a simple library which is used to tunnel a tcp communication in http.
This library can be used to build any application which needs to use TCP to HTTP tunneling for, for example, bypassing a firewall.
## Example
The basic usage of khtunnel is for relaying, using HTTP, a TCP communication between 2 endpoints.  
Thus the basic architecture of the communication is **TCP source -> HTTP tunnel client -> HTTP tunnel server -> TCP destination**.  
### HTTP TUNNEL CLIENT
```kotlin
/* HTTP tunnel Client */
Thread{
    val listeningPort = 10111
    val outgoingPort = 10112 // HTTP tunnel server listening port
    val outgoingAddress = "127.0.0.1" // HTTP tunnel server address
    val serverSocket = ServerSocket(listeningPort)
    while(true){
        val inSocket = serverSocket.accept()
        val outSocket = Socket(outgoingAddress,outgoingPort)
        /* HTTP tunnel client channel */
        Thread(TcpToHttpTunnelHandler(inSocket,outSocket)).start() // listens to incoming communications from TCP source in raw TCP and encapsulates the message in HTTP before relaying it to the HTTP tunnel server.
        Thread(HttpToTcpTunnelHandler(outSocket,inSocket)).start() // listens to incoming communications in HTTP from HTTP tunnel server and relays the payload (http body) as a raw tcp message.
    }
}.start()
```
### HTTP TUNNEL SERVER
```kotlin
/* HTTP tunnel Server */
Thread{
    val listeningPort = 10112
    val outgoingPort = 10113 // TCP destination listening port
    val outgoingAddress = "127.0.0.1" // TCP destination address
    val serverSocket = ServerSocket(listeningPort)
    while(true){
        val inSocket = serverSocket.accept()
        val outSocket = Socket(outgoingAddress,outgoingPort)
        /* HTTP tunnel server channel (it's the reverse of the client) */
        Thread(HttpToTcpTunnelHandler(inSocket,outSocket)).start() // listens to incoming communications in HTTP and relays the payload (http body) as a raw tcp message to the TCP destination.
        Thread(TcpToHttpTunnelHandler(outSocket,inSocket)).start() // listens to incoming communications from HTTP tunnel client in raw TCP and encapsulates the message in HTTP before relaying it.
    }
}.start()
```

## How to build  
simply run:  
```bash
./gradlew build
```
## Gradle installation from maven central  
Kotlin DSL    
```kotlin
implmentation("io.github.ericmpapa:khtunnel:1.0")
```
Groovy DSL  
```groovy
implementation "io.github.ericmpapa:khtunnel:1.0"
```

## Documentation

[Check some use cases](https://github.com/ericmpapa/khtunnel/wiki)
