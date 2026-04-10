package gitv.engine;

import java.util.concurrent.ConcurrentHashMap;

public final class ActionKey {
    private static final ConcurrentHashMap<String, ActionKey> INTERN_POOL = new ConcurrentHashMap<>();

    public static final ActionKey COMMIT = ActionKey.of("COMMIT");
    public static final ActionKey PUSH = ActionKey.of("PUSH");
    public static final ActionKey PULL = ActionKey.of("PULL");
    public static final ActionKey SYNC = ActionKey.of("SYNC");
    public static final ActionKey NONE = ActionKey.of("NONE");

    private final String name;

    private ActionKey(String name) {
        this.name = name;
    }

    public static ActionKey of(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Action name cannot be empty");
        }
        String normalized = name.trim().toUpperCase();
        return INTERN_POOL.computeIfAbsent(normalized, ActionKey::new);
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return name;
    }
}
