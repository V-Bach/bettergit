# Coding Standards

To maintain a readable and maintainable codebase, Gitv enforces strict coding standards. 

## General Principles

- **Readability over Cleverness:** Code is read more often than written. Avoid overly terse or "clever" one-liners if a multi-line implementation is clearer.
- **Immutability:** Prefer immutable data structures wherever possible, especially within the `RepoContext` and `Plan` objects.
- **Fail Fast:** If an invariant is broken or an unexpected state occurs, throw an exception immediately. Do not attempt to swallow errors or limp along.

## Java Guidelines (Assuming Java based on original context)

- **Formatting:** We use a standard code formatter (e.g., Google Java Format). Ensure your IDE is configured to use it, or run the formatting task before committing.
- **Naming Conventions:**
  - Classes and Interfaces: `PascalCase`
  - Methods and Variables: `camelCase`
  - Constants: `UPPER_SNAKE_CASE`
- **Nullability:** Avoid returning `null`. Use `Optional<T>` to explicitly indicate that a value may be absent.
- **Logging:** Use the SLF4J logging facade. Log contextual information at appropriate levels (`DEBUG` for tracing, `INFO` for major lifecycle events, `WARN` for recoverable issues, `ERROR` for failures).

## Documentation

- **Docstrings:** All public interfaces and significant public methods must have clear Javadoc explaining their purpose, parameters, and return values.
- **Inline Comments:** Use inline comments sparingly to explain *why* complex logic exists, not *what* it is doing (the code should explain the *what*).
