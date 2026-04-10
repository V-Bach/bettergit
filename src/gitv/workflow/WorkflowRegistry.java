package gitv.workflow;

import gitv.engine.ActionKey;

import java.util.HashMap;
import java.util.Map;

public class WorkflowRegistry {
    private final Map<ActionKey, Workflow> registry = new HashMap<>();
    private boolean locked = false;

    public void register(ActionKey key, Workflow workflow) {
        if (locked) {
            throw new IllegalStateException("Registry is locked");
        }
        if (registry.containsKey(key)) {
            throw new IllegalArgumentException("Workflow already registered for key: " + key);
        }
        registry.put(key, workflow);
    }

    public Workflow get(ActionKey key) {
        return registry.get(key);
    }

    public boolean contains(ActionKey key) {
        return registry.containsKey(key);
    }

    public void lock() {
        this.locked = true;
    }
}
