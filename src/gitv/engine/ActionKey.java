package gitv.engine;

import java.util.concurrent.ConcurrentHashMap;

public final class ActionKey {
    private static final ConcurrentHashMap<String, ActionKey> INTERN_POOL = new ConcurrentHashMap<>();
    private static volatile boolean locked = false;

    public static final ActionKey COMMIT = ActionKey.ofUnsafe("COMMIT");
    public static final ActionKey PUSH = ActionKey.ofUnsafe("PUSH");
    public static final ActionKey PULL = ActionKey.ofUnsafe("PULL");
    public static final ActionKey SYNC = ActionKey.ofUnsafe("SYNC");
    public static final ActionKey NONE = ActionKey.ofUnsafe("NONE");

    private final String name;

    private ActionKey(String name) {
        this.name = name;
    }

    public static void lock() {
        locked = true;
    }

    public static ActionKey of(String name) {
        if (locked) {
            throw new IllegalStateException("ActionKey creation is locked");
        }
        return ofUnsafe(name);
    }

    private static ActionKey ofUnsafe(String name) {
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
