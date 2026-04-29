package gitv.suggestion.rule;

import gitv.workflow.ModuleIntent;

import java.util.*;
import java.util.stream.Collectors;

public class RuleAggregator {
    
    public AggregationResult aggregate(List<RuleResponse> responses) {
        if (responses == null || responses.isEmpty()) {
            return new AggregationResult(Goal.NONE, Collections.emptyList(), Collections.emptyList(), gitv.workflow.ExecutionMode.AUTO);
        }

        // 1. Find highest Tier
        Tier highestTier = responses.stream()
                .map(RuleResponse::getTier)
                .max(Comparator.comparingInt(Tier::getLevel))
                .orElse(Tier.SUGGESTION);

        // 2. Select Goal with max cumulative score in the highest Tier
        Map<Goal, Integer> goalScores = new HashMap<>();
        for (RuleResponse r : responses) {
            if (r.getTier() == highestTier) {
                goalScores.put(r.getGoal(), goalScores.getOrDefault(r.getGoal(), 0) + r.getScore());
            }
        }

        Goal winningGoal = goalScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(Goal.NONE);

        // 3. Collect modules for the winning goal
        Map<gitv.workflow.ModuleID, RuleResponse> moduleMap = new EnumMap<>(gitv.workflow.ModuleID.class);
        List<RuleResponse> appliedRules = new ArrayList<>();
        
        for (RuleResponse r : responses) {
            if (r.getGoal() == winningGoal && r.getTier() == highestTier) {
                appliedRules.add(r);
                if (r.getModule() != gitv.workflow.ModuleID.NONE) {
                    RuleResponse existing = moduleMap.get(r.getModule());
                    if (existing == null || r.getScore() > existing.getScore()) {
                        moduleMap.put(r.getModule(), r);
                    } else if (existing != null && r.getScore() == existing.getScore()) {
                        // Merge options if same score
                        Set<gitv.workflow.Option> mergedOptions = new HashSet<>(existing.getOptions());
                        mergedOptions.addAll(r.getOptions());
                        gitv.workflow.ExecutionMode mergedMode = existing.getMode().ordinal() > r.getMode().ordinal() ? existing.getMode() : r.getMode();
                        boolean mergedMutative = existing.isMutative() || r.isMutative();
                        moduleMap.put(r.getModule(), new RuleResponse(
                            existing.getGoal(), existing.getTier(), existing.getScore(),
                            existing.getModule(), mergedOptions, existing.getAnchor(), existing.getAdvisory(),
                            mergedMode, mergedMutative
                        ));
                    }
                }
            }
        }

        // 4. Sort modules by Anchor
        List<ModuleIntent> intents = moduleMap.values().stream()
                .sorted(Comparator.comparing(r -> r.getAnchor().ordinal()))
                .map(r -> new ModuleIntent(r.getModule(), r.getOptions(), r.getAnchor(), r.getMode(), r.isMutative()))
                .collect(Collectors.toList());

        gitv.workflow.ExecutionMode overallMode = intents.stream()
                .map(ModuleIntent::getMode)
                .max(Comparator.comparing(gitv.workflow.ExecutionMode::ordinal))
                .orElse(gitv.workflow.ExecutionMode.AUTO);

        return new AggregationResult(winningGoal, intents, appliedRules, overallMode);
    }
    
    public static class AggregationResult {
        private final Goal goal;
        private final List<ModuleIntent> intents;
        private final List<RuleResponse> appliedRules;
        private final gitv.workflow.ExecutionMode mode;

        public AggregationResult(Goal goal, List<ModuleIntent> intents, List<RuleResponse> appliedRules, gitv.workflow.ExecutionMode mode) {
            this.goal = goal;
            this.intents = intents;
            this.appliedRules = appliedRules;
            this.mode = mode;
        }

        public Goal getGoal() { return goal; }
        public List<ModuleIntent> getIntents() { return intents; }
        public List<RuleResponse> getAppliedRules() { return appliedRules; }
        public gitv.workflow.ExecutionMode getMode() { return mode; }
    }
}
