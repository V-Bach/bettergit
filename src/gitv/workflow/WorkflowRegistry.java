package gitv.workflow;

import gitv.engine.ActionType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class WorkflowRegistry {
    private final Map<ActionType, Workflow> workflows;

    public WorkflowRegistry(List<Workflow> workflowList) {
        workflows = new EnumMap<>(ActionType.class);
        if (workflowList != null) {
            for (Workflow workflow : workflowList) {
                workflows.put(workflow.getType(), workflow);
            }
        }
    }

    public Workflow getWorkflow(ActionType type) {
        return workflows.get(type);
    }
}
