package gitv.suggestion;

import gitv.git.RepoContext;

public interface ActionEvaluator {
    ScoredAction evaluate(RepoContext context);
}
