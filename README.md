# MentalPoker
FH Wedel IT-Security Project WS15/16

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
