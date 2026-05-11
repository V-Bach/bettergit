package gitv.engine;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StateManager {
    private final File stateFile;

    public StateManager(File repoRoot) {
        this.stateFile = new File(repoRoot, ".git/gitv/state.json");
    }

    public boolean hasState() {
        return stateFile.exists();
    }

    public ExecutionState loadState() {
        if (!stateFile.exists()) return null;
        try (BufferedReader reader = new BufferedReader(new FileReader(stateFile))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            String content = sb.toString();

            String id = extractString(content, "\"executionId\"\\s*:\\s*\"(.*?)\"");
            String hash = extractString(content, "\"initialHeadHash\"\\s*:\\s*\"(.*?)\"");

            List<ActionKey> planned = extractArray(content, "\"plannedActions\"\\s*:\\s*\\[(.*?)\\]");
            List<ActionKey> completed = extractArray(content, "\"completedSteps\"\\s*:\\s*\\[(.*?)\\]");

            return new ExecutionState(id, hash, planned, completed);
        } catch (Exception e) {
            return null;
        }
    }

    private String extractString(String json, String regex) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1).replace("\\\"", "\"").replace("\\\\", "\\");
        }
        return "";
    }

    private List<ActionKey> extractArray(String json, String regex) {
        List<ActionKey> result = new ArrayList<>();
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            String arrayContent = matcher.group(1);
            java.util.regex.Pattern itemPattern = java.util.regex.Pattern.compile("\"(.*?)\"");
            java.util.regex.Matcher itemMatcher = itemPattern.matcher(arrayContent);
            while (itemMatcher.find()) {
                String val = itemMatcher.group(1).replace("\\\"", "\"").replace("\\\\", "\\");
                ActionKey key = ActionKey.get(val);
                if (key != null) result.add(key);
            }
        }
        return result;
    }

    public void saveState(ExecutionState state) {
        stateFile.getParentFile().mkdirs();
        
        try (FileWriter writer = new FileWriter(stateFile)) {
            writer.write("{\n");
            writer.write("  \"executionId\": \"" + escapeJson(state.getExecutionId()) + "\",\n");
            writer.write("  \"initialHeadHash\": \"" + escapeJson(state.getInitialHeadHash()) + "\",\n");
            
            writer.write("  \"plannedActions\": [\n");
            writer.write(state.getPlannedActions().stream()
                .map(a -> "    \"" + escapeJson(a.toString()) + "\"")
                .collect(Collectors.joining(",\n")));
            writer.write("\n  ],\n");
            
            writer.write("  \"completedSteps\": [\n");
            writer.write(state.getCompletedSteps().stream()
                .map(a -> "    \"" + escapeJson(a.toString()) + "\"")
                .collect(Collectors.joining(",\n")));
            writer.write("\n  ]\n");
            writer.write("}\n");
        } catch (IOException e) {
            // ignore
        }
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public void clearState() {
        if (stateFile.exists()) {
            stateFile.delete();
        }
    }
}
