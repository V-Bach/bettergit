package gitv.workflow;

import gitv.engine.ActionKey;
import gitv.suggestion.DecisionResult;
import gitv.suggestion.ScoredAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlanBuilderTest {

    private PlanBuilder planBuilder;

    @BeforeEach
    void setUp() {
        planBuilder = new PlanBuilder();
    }

    /**
     * Helper to mock out a DecisionResult without bleeding context.
     * Note: In the refactored architecture, RepoContext is no longer 
     * read by PlanBuilder. build() parameters can safely accept null context.
     */
    private DecisionResult createDecision(ActionKey selected, ScoredAction... alternatives) {
        ScoredAction selectedAction = (selected == ActionKey.NONE) 
                                      ? ScoredAction.none() 
                                      : new ScoredAction(selected, 1.0, 100, false, Collections.emptyList());
        return new DecisionResult(selectedAction, Arrays.asList(alternatives));
    }

    /**
     * Helper to reliably generate the blocked intent signature.
     */
    private ScoredAction blockedPush() {
        return ScoredAction.blocked(ActionKey.PUSH, "Behind remote limits");
    }

    /**
     * Extracts ActionKeys from final steps for strict Sequence Validation.
     */
    private List<ActionKey> extractActions(ExecutionPlan plan) {
        return plan.getSteps().stream()
                .map(ExecutionStep::getAction)
                .collect(Collectors.toList());
    }

    // --- ✅ REQUIRED TEST CASES (ARCHITECTURAL GUARDS) ---

    @Test
    void shouldReturnEmptyPlan_whenNoActionSelected() {
        DecisionResult decision = createDecision(ActionKey.NONE);
        // Null context proves context-less execution
        ExecutionPlan plan = planBuilder.build(decision, null);
        
        List<ActionKey> actions = extractActions(plan);
        assertEquals(Collections.emptyList(), actions, 
                "Plan should be completely empty for NONE with no blocked alternatives");
    }

    @Test
    void shouldCommitAndPush_whenCommitSelected() {
        DecisionResult decision = createDecision(ActionKey.COMMIT);
        ExecutionPlan plan = planBuilder.build(decision, null);

        List<ActionKey> actions = extractActions(plan);
        assertEquals(Arrays.asList(ActionKey.COMMIT, ActionKey.PUSH), actions,
                "Clean commit intent should map to [COMMIT, PUSH]");
    }

    @Test
    void shouldPush_whenPushSelected() {
        DecisionResult decision = createDecision(ActionKey.PUSH);
        ExecutionPlan plan = planBuilder.build(decision, null);

        List<ActionKey> actions = extractActions(plan);
        assertEquals(Collections.singletonList(ActionKey.PUSH), actions,
                "Clean push intent should strictly map to [PUSH]");
    }

    @Test
    void shouldPullThenPush_whenPushBlockedAndNoSelectedAction() {
        DecisionResult decision = createDecision(ActionKey.NONE, blockedPush());
        ExecutionPlan plan = planBuilder.build(decision, null);

        List<ActionKey> actions = extractActions(plan);
        assertEquals(Arrays.asList(ActionKey.PULL, ActionKey.PUSH), actions,
                "Blocked PUSH as fallback should synthesize [PULL, PUSH]");
    }

    @Test
    void shouldPullCommitPush_whenCommitSelectedAndPushBlocked() {
        DecisionResult decision = createDecision(ActionKey.COMMIT, blockedPush());
        ExecutionPlan plan = planBuilder.build(decision, null);

        List<ActionKey> actions = extractActions(plan);
        assertEquals(Arrays.asList(ActionKey.PULL, ActionKey.COMMIT, ActionKey.PUSH), actions,
                "Blocked PUSH with COMMIT should synthesize [PULL, COMMIT, PUSH]");
    }

    // --- 🚫 ANTI-BUG TEST ---

    @Test
    void shouldOnlyPull_whenPullSelectedEvenIfPushBlocked() {
        DecisionResult decision = createDecision(ActionKey.PULL, blockedPush());
        ExecutionPlan plan = planBuilder.build(decision, null);

        List<ActionKey> actions = extractActions(plan);
        assertEquals(Collections.singletonList(ActionKey.PULL), actions,
                "PlanBuilder MUST NOT invent a PUSH intent when PULL is natively selected, even if a blocked push exists.");
    }
}
