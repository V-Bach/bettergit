# Native Compilation with GraalVM

For a CLI tool, startup time is critical. Waiting for a Java Virtual Machine (JVM) to spin up every time you run `gitv status` is unacceptable. Therefore, Gitv uses **GraalVM Native Image** to compile the Java application into standalone, platform-specific executables.

## What is GraalVM Native Image?

Native Image is a technology that performs Ahead-Of-Time (AOT) compilation. It analyzes the application, removes unused code, and pre-compiles the remaining Java bytecode into native machine code. 

The resulting binary:
- Requires no JVM installed on the user's machine.
- Starts up in milliseconds.
- Has a significantly lower memory footprint.

## Challenges with Native Compilation

AOT compilation requires knowing all classes at build time. Dynamic Java features like Reflection or dynamic proxies require special configuration for GraalVM to understand them.

Because Gitv avoids heavy reflection-based frameworks, our native image configuration is relatively minimal, handled primarily by metadata provided via PicoCLI.

## Building Natively (Local)

To build the native binary on your own machine, you must have GraalVM installed and the `native-image` tool available in your path.

```bash
mvn clean package -Pnative
```
The resulting binary will be located in the `target/` directory, named `gitv` (or `gitv.exe` on Windows).
