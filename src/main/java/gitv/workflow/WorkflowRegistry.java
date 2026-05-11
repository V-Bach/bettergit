package gitv.workflow;

import gitv.engine.ActionKey;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WorkflowRegistry {
    private Map<ActionKey, Workflow> registry = new HashMap<>();
    private Map<String, ActionKey> stringIndex = new HashMap<>();
    private volatile boolean locked = false;

    public void register(ActionKey key, Workflow workflow) {
        if (locked) {
            throw new IllegalStateException("Registry is locked");
        }
        if (registry.containsKey(key)) {
            throw new IllegalArgumentException("Workflow already registered for key: " + key);
        }
        registry.put(key, workflow);
        stringIndex.put(key.toString(), key);
    }

    public Workflow get(ActionKey key) {
        return registry.get(key);
    }
    
    public ActionKey getByName(String name) {
        if (name == null) return null;
        return stringIndex.get(name.toUpperCase());
    }

    public boolean contains(ActionKey key) {
        return registry.containsKey(key);
    }

    public void lock() {
        this.locked = true;
        this.registry = Collections.unmodifiableMap(this.registry);
        this.stringIndex = Collections.unmodifiableMap(this.stringIndex);
    }
}
