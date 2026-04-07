package gitv.suggestion;

import gitv.engine.ActionType;
import gitv.git.RepoContext;
import java.util.Collections;

public class SuggestionEngine {
    public Suggestion suggest(RepoContext context) {
        int changedFiles = context.getChangedFiles();
        if (changedFiles == 0) {
            return new Suggestion("✅ Working tree clean", Collections.emptyList(), false, "");
        } else if (changedFiles <= 5) {
            return new Suggestion("⚠️ You have " + changedFiles + " changes\n👉 Safe to commit", Collections.singletonList(ActionType.COMMIT), true, "Do you want to run suggested command? (y/n) ");
        } else {
            return new Suggestion("⚠️ You have many changes (" + changedFiles + " files)\n👉 Consider reviewing", Collections.singletonList(ActionType.COMMIT), true, "You have many changes. Are you sure? (y/n) ");
        }
    }
}
