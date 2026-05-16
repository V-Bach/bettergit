package gitv.workflow;

import gitv.engine.ActionKey;
import gitv.suggestion.DecisionResult;
import gitv.suggestion.rule.Goal;
import gitv.suggestion.rule.RuleResponse;
import gitv.suggestion.rule.Tier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlanBuilderTest {

    private PlanBuilder planBuilder;

    @BeforeEach
    void setUp() {
        planBuilder = new PlanBuilder();
    }

    private DecisionResult createDecision(ModuleIntent... intents) {
        return new DecisionResult(
                Goal.NONE,
                Arrays.asList(intents),
                Collections.emptyList(),
                Collections.emptyList(),
                ExecutionMode.AUTO,
                Collections.emptySet()
        );
    }

    private List<ActionKey> extractActions(ExecutionPlan plan) {
        return plan.getSteps().stream()
                .map(ExecutionStep::getAction)
                .collect(Collectors.toList());
    }

    // --- ✅ REQUIRED TEST CASES (ARCHITECTURAL GUARDS) ---

    @Test
    void shouldReturnEmptyPlan_whenNoIntents() {
        DecisionResult decision = createDecision();
        ExecutionPlan plan = planBuilder.build(decision);
        
        List<ActionKey> actions = extractActions(plan);
        assertEquals(Collections.emptyList(), actions, 
                "Plan should be completely empty when no intents are provided");
    }

    @Test
    void shouldMapIntentsDirectlyToActions() {
        DecisionResult decision = createDecision(
            new ModuleIntent(ModuleID.STAGE, Collections.emptySet(), gitv.workflow.Anchor.PRE_COMMIT, ExecutionMode.AUTO, false),
            new ModuleIntent(ModuleID.COMMIT, Collections.emptySet(), gitv.workflow.Anchor.COMMIT, ExecutionMode.AUTO, false)
        );
        ExecutionPlan plan = planBuilder.build(decision);

        List<ActionKey> actions = extractActions(plan);
        assertEquals(Arrays.asList(ActionKey.ADD, ActionKey.COMMIT), actions,
                "PlanBuilder must perform pure 1-to-1 mapping of intents to actions");
    }

    @Test
    void shouldThrowException_whenAutoModeIsMutative() {
        DecisionResult decision = createDecision(
            new ModuleIntent(ModuleID.PUSH, Collections.emptySet(), gitv.workflow.Anchor.COMMIT, ExecutionMode.AUTO, true)
        );

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            planBuilder.build(decision);
        });
        
        assertEquals("DecisionEngine produced invalid ExecutionMode for workflow: AUTO mode cannot be mutative.", exception.getMessage());
    }

    @Test
    void shouldAllowGuardedMutativeIntent() {
        DecisionResult decision = createDecision(
            new ModuleIntent(ModuleID.PUSH, Collections.emptySet(), gitv.workflow.Anchor.COMMIT, ExecutionMode.GUARDED, true)
        );
        ExecutionPlan plan = planBuilder.build(decision);

        List<ActionKey> actions = extractActions(plan);
        assertEquals(Collections.singletonList(ActionKey.PUSH), actions);
    }
}
