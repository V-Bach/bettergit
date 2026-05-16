# The Build System

Gitv uses Maven as its primary build system to manage dependencies, run tests, and orchestrate the creation of artifacts.

## Maven Lifecycle

Our `pom.xml` defines standard Maven lifecycle phases:
- `clean`: Removes the `target/` directory.
- `compile`: Compiles the core Java source code.
- `test`: Executes unit and integration tests using JUnit.
- `package`: Packages the compiled code into an executable JAR file.

## Key Dependencies

Gitv aims to be lightweight, avoiding bloated frameworks. Key dependencies include:
- `picocli`: For robust command-line argument parsing and CLI structure.
- `slf4j` & `logback`: For structured internal logging.
- `junit`: For test execution.

*(Note: We specifically avoid large frameworks like Spring to ensure lightning-fast startup times, which is critical for a CLI tool).*

## Native Compilation Profile

While the standard `package` phase builds a JAR, we rely on a specific Maven profile to trigger GraalVM compilation. 

```bash
mvn clean package -Pnative
```
This profile executes the GraalVM Native Build Tools plugin to perform Ahead-Of-Time (AOT) compilation, resulting in the standalone native executable.
