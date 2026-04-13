package gitv.suggestion;

import gitv.engine.ActionKey;
import gitv.git.RepoContext;
import java.util.Collections;

public class SuggestionEngine {
    public Suggestion suggest(RepoContext context) {
        if (!context.hasUnstagedChanges() && !context.hasStagedChanges()) {
            return new Suggestion("✅ Working tree clean", Collections.emptyList(), false, "");
        } else {
            return new Suggestion("⚠️ You have changes\n👉 Safe to commit", Collections.singletonList(ActionKey.COMMIT), true, "Do you want to run suggested command? (y/n) ");
        }
    }
}
