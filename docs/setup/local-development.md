# Local Development Setup

If you want to build Gitv from source or contribute to the project, follow these steps to set up your local development environment.

## Prerequisites

Gitv is built using Java and compiled into native binaries using GraalVM. 

You need to install:
1. **JDK 17 or higher:** A standard Java Development Kit.
2. **GraalVM:** specifically configured with the `native-image` tool for your platform.
3. **Maven:** The build automation tool used by the project.

## Cloning the Repository

```bash
git clone https://github.com/V-Bach/gitv.git
cd gitv
```

## Building the Standard JAR

To compile the code, run tests, and build a standard Java JAR file:

```bash
mvn clean install
```

You can run the application directly via Java for rapid testing:
```bash
java -jar target/gitv-1.0-SNAPSHOT.jar status
```

## Running the Test Suite

Gitv maintains a comprehensive test suite enforcing architectural rules. To run the tests:

```bash
mvn test
```

*Ensure all tests pass before submitting any Pull Requests.*
