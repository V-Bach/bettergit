package gitv.suggestion;

import gitv.engine.ActionType;
import java.util.List;

public class Suggestion {
    private final String message;
    private final List<ActionType> actions;
    private final boolean requiresConfirmation;
    private final String confirmationMessage;

    public Suggestion(String message, List<ActionType> actions, boolean requiresConfirmation, String confirmationMessage) {
        this.message = message;
        this.actions = actions;
        this.requiresConfirmation = requiresConfirmation;
        this.confirmationMessage = confirmationMessage;
    }

    public String getMessage() {
        return message;
    }

    public List<ActionType> getActions() {
        return actions;
    }

    public boolean requiresConfirmation() {
        return requiresConfirmation;
    }

    public String getConfirmationMessage() {
        return confirmationMessage;
    }
}
