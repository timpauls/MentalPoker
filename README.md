# MentalPoker
FH Wedel IT-Security Project WS15/16

Secure coin flipping server/client based on the SRA algorithm.

### Prerequisites
OpenJDK 8 is required to run this software (Oracle JDK will not work).

The program relies on the used certificate files to be present in the folder `ssl-certs/` relative to the execution path.

### Building
`./gradlew fatJar` will generate a JAR containing the server and client required to play, both in interactive and
automatic mode. The JAR contains all required dependencies.

### Usage
#### Interactive mode
To enter interactive mode, simply launch the jar without any parameters:
`java -jar MentalPoker-all-1.0-SNAPSHOT.jar`

Alternatively, you can pass the parameter `--interactive`, which also allows for the additional parameter `--log` to enable
display of log messages:
`java -jar MentalPoker-all-1.0-SNAPSHOT.jar --interactive --log`

In interactive mode, a multiple-choice-style menu guides you through using the program either as a server, or as a client
for the coin flipping protocol.

In server mode you must specify a port for the server to listen on. The server remains up until its process is actively killed.

In client mode you can choose to play against one of a list of online servers, localhost on the default port, or you can 
enter an IP and port of a server that is not currently listed. The client terminates after each played game.

#### Automatic mode
##### Server
`java -jar MentalPoker-all-1.0-SNAPSHOT.jar --server [port]`

Example:
`java -jar MentalPoker-all-1.0-SNAPSHOT.jar --server 6882`

##### Client
`java -jar MentalPoker-all-1.0-SNAPSHOT.jar --client [host] [port]`

Example:
`java -jar MentalPoker-all-1.0-SNAPSHOT.jar --client localhost 6882`

Both, client and server, may be launched with the optional last parameter `--log` to enable display of log messages.