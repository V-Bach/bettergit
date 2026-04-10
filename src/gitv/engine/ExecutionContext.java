package gitv.engine;

import gitv.git.RepoContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExecutionContext {
    private final RepoContext repoContext;
    private final Map<String, String> data = new ConcurrentHashMap<>();
    private static final int MAX_ENTRIES = 100;
    private static final int MAX_VALUE_LENGTH = 1024;

    public ExecutionContext(RepoContext repoContext) {
        this.repoContext = repoContext;
    }

    public void put(String key, String value) {
        if (data.size() >= MAX_ENTRIES && !data.containsKey(key)) {
            throw new IllegalStateException("Context max entries limit reached");
        }
        if (value != null && value.length() > MAX_VALUE_LENGTH) {
            throw new IllegalArgumentException("Value size exceeds limit");
        }
        data.put(key, value);
    }

    public String get(String key) {
        return data.get(key);
    }
    
    public RepoContext getRepoContext() {
        return repoContext;
    }
}
