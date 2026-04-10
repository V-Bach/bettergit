package gitv.suggestion;

import gitv.engine.ActionKey;
import java.util.List;

public class Suggestion {
    private final String message;
    private final List<ActionKey> actions;
    private final boolean requiresConfirmation;
    private final String confirmationMessage;

    public Suggestion(String message, List<ActionKey> actions, boolean requiresConfirmation, String confirmationMessage) {
        this.message = message;
        this.actions = actions;
        this.requiresConfirmation = requiresConfirmation;
        this.confirmationMessage = confirmationMessage;
    }

    public String getMessage() {
        return message;
    }

    public List<ActionKey> getActions() {
        return actions;
    }

    public boolean requiresConfirmation() {
        return requiresConfirmation;
    }

    public String getConfirmationMessage() {
        return confirmationMessage;
    }
}
