package gitv.workflow;

import java.util.Collections;
import java.util.Set;

public class ModuleIntent {
    private final ModuleID id;
    private final Set<Option> options;
    private final Anchor anchor;
    private final ExecutionMode mode;
    private final boolean isMutative;

    public ModuleIntent(ModuleID id, Set<Option> options, Anchor anchor, ExecutionMode mode, boolean isMutative) {
        this.id = id;
        this.options = options != null ? options : Collections.emptySet();
        this.anchor = anchor;
        this.mode = mode != null ? mode : ExecutionMode.AUTO;
        this.isMutative = isMutative;
    }

    public ModuleID getId() {
        return id;
    }

    public Set<Option> getOptions() {
        return options;
    }
    
    public Anchor getAnchor() {
        return anchor;
    }

    public ExecutionMode getMode() {
        return mode;
    }

    public boolean isMutative() {
        return isMutative;
    }
}
