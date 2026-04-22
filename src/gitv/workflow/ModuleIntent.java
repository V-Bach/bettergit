package gitv.workflow;

import java.util.Collections;
import java.util.Set;

public class ModuleIntent {
    private final ModuleID id;
    private final Set<Option> options;
    private final Anchor anchor;

    public ModuleIntent(ModuleID id, Set<Option> options, Anchor anchor) {
        this.id = id;
        this.options = options != null ? options : Collections.emptySet();
        this.anchor = anchor;
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
}
