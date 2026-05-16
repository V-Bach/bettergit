package gitv.workflow;

import gitv.git.RepoContext;
import gitv.engine.ActionKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SafetyValidatorTest {

    private SafetyValidator safetyValidator;

    private RepoContext createEmptyContext() {
        return new RepoContext(false, false, false, false, false, false, false, false, null);
    }

    @BeforeEach
    void setUp() {
        safetyValidator = new SafetyValidator();
    }

    @Test
    void shouldRejectCommitWhenNoStagedChangesExist() {
        RepoContext context = createEmptyContext();
        ExecutionPlan plan = new ExecutionPlan(Collections.singletonList(new ExecutionStep(ActionKey.COMMIT, Collections.emptyList())), ExecutionMode.AUTO);

        SafetyResult result = safetyValidator.validate(plan, context);
        assertFalse(result.isSafe(), "Should block COMMIT when there are no staged changes");
    }

    @Test
    void shouldRejectPushWhenBehindRemote() {
        RepoContext context = createEmptyContext().withBehindRemote(true);
        ExecutionPlan plan = new ExecutionPlan(Collections.singletonList(new ExecutionStep(ActionKey.PUSH, Collections.emptyList())), ExecutionMode.AUTO);

        SafetyResult result = safetyValidator.validate(plan, context);
        assertFalse(result.isSafe(), "Should block PUSH when local branch is behind remote");
    }

    @Test
    void shouldRejectAnyPlanWhenRepositoryIsLocked() {
        RepoContext context = new RepoContext(false, false, false, false, false, false, false, true, "merge in progress");
        ExecutionPlan plan = new ExecutionPlan(Collections.singletonList(new ExecutionStep(ActionKey.ADD, Collections.emptyList())), ExecutionMode.AUTO);

        SafetyResult result = safetyValidator.validate(plan, context);
        assertFalse(result.isSafe(), "Should block any operations if the repository is locked");
    }

    @Test
    void shouldAllowAddThenCommitWhenStateTransitionsAreValid() {
        RepoContext context = new RepoContext(true, false, false, false, false, false, false, false, null);
        ExecutionPlan plan = new ExecutionPlan(Arrays.asList(
                new ExecutionStep(ActionKey.ADD, Collections.emptyList()),
                new ExecutionStep(ActionKey.COMMIT, Collections.emptyList())
        ), ExecutionMode.AUTO);

        SafetyResult result = safetyValidator.validate(plan, context);
        assertTrue(result.isSafe(), "ADD followed by COMMIT should be a safe transition");
    }
}
