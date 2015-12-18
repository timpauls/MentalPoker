# MentalPoker
FH Wedel IT-Security Project WS15/16

### Client/Server
The package de.fhwedel.coinflipping cointains classes needed for the network based coin flipping.
The client is implemented in de.fhwedel.coinflipping.network.CoinFlippingClient. It currently contains a hard coded URL and port of a server that will be connected to when running its main method.

The server is implemented in de.fhwedel.coinflipping.network.CoinFlippingServer. It will launch when running its main method, using either a port supplied as the first argument to the main method, or if no port is given, the default port 6882.

`./gradlew fatJar` will generate a JAR of the server, containing all dependencies.

Sample usage:
`java -jar MentalPoker-all-1.0-SNAPSHOT.jar 6882`

An instance of the server is hosted at `87.106.18.90:6882`.

### Dependencies
MentalPoker relies on the Bouncy Castle security library, specifically on [my fork of it](https://github.com/timpauls/bc-java).

### gradle
This project uses gradle as a build tool and can be built by running `./gradlew [task]`.
The first execution will download gradle if necessary. All dependencies will automatically be resolved.

### Gradle tasks
#### Testing
Tests are executed by running
```
./gradlew test
```
