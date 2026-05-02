package gitv.engine;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class StateManager {
    private final File stateFile;

    public StateManager(File repoRoot) {
        this.stateFile = new File(repoRoot, ".git/gitv/state.txt");
    }

    public boolean hasState() {
        return stateFile.exists();
    }

    public ExecutionState loadState() {
        if (!stateFile.exists()) return null;
        Properties props = new Properties();
        try (FileReader reader = new FileReader(stateFile)) {
            props.load(reader);
            String id = props.getProperty("executionId");
            String hash = props.getProperty("initialHeadHash");
            String plannedStr = props.getProperty("plannedActions", "");
            String completedStr = props.getProperty("completedSteps", "");

            List<ActionKey> planned = new ArrayList<>();
            if (!plannedStr.isEmpty()) {
                for (String s : plannedStr.split(",")) {
                    ActionKey key = ActionKey.get(s);
                    if (key != null) planned.add(key);
                }
            }

            List<ActionKey> completed = new ArrayList<>();
            if (!completedStr.isEmpty()) {
                for (String s : completedStr.split(",")) {
                    ActionKey key = ActionKey.get(s);
                    if (key != null) completed.add(key);
                }
            }
            return new ExecutionState(id, hash, planned, completed);
        } catch (Exception e) {
            return null;
        }
    }

    public void saveState(ExecutionState state) {
        stateFile.getParentFile().mkdirs();
        Properties props = new Properties();
        props.setProperty("executionId", state.getExecutionId() != null ? state.getExecutionId() : "");
        props.setProperty("initialHeadHash", state.getInitialHeadHash() != null ? state.getInitialHeadHash() : "");
        props.setProperty("plannedActions", state.getPlannedActions().stream().map(Object::toString).collect(Collectors.joining(",")));
        props.setProperty("completedSteps", state.getCompletedSteps().stream().map(Object::toString).collect(Collectors.joining(",")));
        
        try (FileWriter writer = new FileWriter(stateFile)) {
            props.store(writer, "Gitv Execution State");
        } catch (IOException e) {
            // ignore
        }
    }

    public void clearState() {
        if (stateFile.exists()) {
            stateFile.delete();
        }
    }
}
