package gitv.suggestion.rule;

public enum Tier {
    EMERGENCY(3),
    WORKFLOW(2),
    SUGGESTION(1);

    private final int level;

    Tier(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
