package gitv.cli;

import gitv.engine.ActionKey;
import gitv.engine.ExecutionEngine;
import gitv.engine.ExecutionState;
import gitv.engine.StateManager;
import gitv.git.ContextBuilder;
import gitv.git.GitService;
import gitv.git.RepoContext;
import gitv.suggestion.DecisionEngine;
import gitv.suggestion.DecisionResult;
import gitv.suggestion.rule.Goal;
import gitv.workflow.ExecutionPlan;
import gitv.workflow.ExecutionStep;
import gitv.workflow.PlanBuilder;
import gitv.workflow.SafetyResult;
import gitv.workflow.SafetyValidator;
import gitv.workflow.WorkflowResult;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

public class WorkflowRunner {

    private final ExecutionEngine executionEngine;
    private final Scanner scanner = new Scanner(System.in);

    public WorkflowRunner(ExecutionEngine executionEngine) {
        this.executionEngine = executionEngine;
    }

    public int run(Goal targetGoal, boolean isApply, boolean isExplain, boolean isConfirm) {
        DecisionEngine engine = new DecisionEngine();
        GitService git = new GitService();
        if (!git.isGitRepository()) {
            System.out.println(Ansi.colorBold("Error: Not a git repository", Ansi.RED));
            return 1;
        }

        String currentHash = git.getHeadHash();
        StateManager stateManager = new StateManager(git.getRepoRoot());
        ExecutionState recoveredState = null;

        if (stateManager.hasState()) {
            ExecutionState state = stateManager.loadState();
            if (state != null && state.getPlannedActions() != null && state.getCompletedSteps().size() < state.getPlannedActions().size()) {
                if (currentHash.equals(state.getInitialHeadHash())) {
                    System.out.print(Ansi.colorBold("\nWarning: Found an interrupted execution plan. Resume? [y/N]: ", Ansi.YELLOW));
                    String answer = scanner.nextLine();
                    if (answer.trim().equalsIgnoreCase("y")) {
                        recoveredState = state;
                    } else {
                        stateManager.clearState();
                    }
                } else {
                    System.out.println(Ansi.color("Warning: Repository state changed since last crash. Discarding old state.", Ansi.YELLOW));
                    stateManager.clearState();
                }
            } else {
                stateManager.clearState();
            }
        }

        ContextBuilder builder = new ContextBuilder();
        RepoContext context = builder.build();

        ExecutionPlan plan;
        DecisionResult decisionResult = null;

        if (recoveredState != null) {
            final List<ActionKey> completed = recoveredState.getCompletedSteps();
            List<ExecutionStep> steps = recoveredState.getPlannedActions().stream()
                .filter(key -> !completed.contains(key))
                .map(key -> new ExecutionStep(key, Collections.singletonList("Recovered from interrupted plan")))
                .collect(Collectors.toList());
            plan = new ExecutionPlan(steps, gitv.workflow.ExecutionMode.AUTO);

            decisionResult = new DecisionResult(Goal.NONE, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), gitv.workflow.ExecutionMode.AUTO, Collections.emptySet());
        } else {
            decisionResult = engine.decide(context, targetGoal);
            PlanBuilder planBuilder = new PlanBuilder();
            plan = planBuilder.build(decisionResult);
        }

        SafetyValidator safetyValidator = new SafetyValidator();
        SafetyResult safetyResult = safetyValidator.validate(plan, context);

        if (isExplain) {
            CliFormatter.printExplanation(decisionResult, plan, safetyResult);
            return 0;
        }

        if (!isApply) {
            CliFormatter.printPlan(plan);
            System.out.println(Ansi.color("\n[DRY RUN] Run with `--apply` to execute.", Ansi.YELLOW));
            return 0;
        }

        List<ExecutionStep> steps = plan.getSteps();
        if (steps == null || steps.isEmpty() || steps.get(0).getAction() == ActionKey.NONE) {
            System.out.println(Ansi.color("No actions required.", Ansi.GRAY));
            return 0;
        }

        if (plan.getMode() == gitv.workflow.ExecutionMode.INTERACTIVE) {
            System.out.println(Ansi.colorBold("\nWarning: INTERACTIVE MODE: Gitv will yield terminal control for manual intervention.", Ansi.YELLOW));
        } else if (plan.getMode() == gitv.workflow.ExecutionMode.GUARDED) {
            System.out.println(Ansi.colorBold("\nWarning: GUARDED MODE: This plan mutates repository state and may halt midway.", Ansi.YELLOW));
            System.out.print(Ansi.bold("Do you want to proceed? [y/N]: "));
            String answer = scanner.nextLine();
            if (!answer.trim().equalsIgnoreCase("y") && !answer.trim().equalsIgnoreCase("yes")) {
                System.out.println(Ansi.color("Aborted.", Ansi.GRAY));
                return 0;
            }
            context = builder.build();
        } else if (isConfirm) {
            System.out.print(Ansi.bold("\nDo you want to run the execution plan? [y/N]: "));
            String answer = scanner.nextLine();
            if (!answer.trim().equalsIgnoreCase("y") && !answer.trim().equalsIgnoreCase("yes")) {
                System.out.println(Ansi.color("Aborted.", Ansi.GRAY));
                return 0;
            }
            context = builder.build();
        }

        if (!safetyResult.isSafe()) {
            System.out.println(Ansi.colorBold("\nSafety Guard Blocked: Cannot execute plan. Reason: ", Ansi.RED) + safetyResult.getMessage());
            return 1;
        }

        String executionId = recoveredState != null && recoveredState.getExecutionId() != null
                ? recoveredState.getExecutionId()
                : UUID.randomUUID().toString();
        File logFile = new File(git.getRepoRoot(), ".git/gitv/execution.log");

        WorkflowResult result = executionEngine.execute(plan, context, executionId, logFile, stateManager, recoveredState, currentHash);
        if (result.isSuccess()) {
            System.out.println(Ansi.colorBold("Success: " + result.getMessage(), Ansi.GREEN));
            return 0;
        } else {
            System.out.println(Ansi.colorBold("Execution Failed: ", Ansi.RED) + result.getMessage());
            System.out.println(Ansi.color("   (Check .git/gitv/execution.log for full details)", Ansi.GRAY));

            System.out.println(Ansi.color("\nRe-evaluating repository state for recovery guidance...", Ansi.CYAN));
            context = builder.build();
            DecisionResult recoveryDecision = engine.decide(context);

            if (recoveryDecision != null && !recoveryDecision.getAllAdvisories().isEmpty()) {
                System.out.println(Ansi.bold("\nSuggested Fix (Goal: " + recoveryDecision.getGoal() + "):"));
                for (gitv.workflow.Advisory advisory : recoveryDecision.getAllAdvisories()) {
                    System.out.println("- [" + advisory.severity() + "] " + advisory.message());
                    if (advisory.actionableFix() != null && advisory.actionableFix() != ActionKey.NONE) {
                        System.out.println(Ansi.color("  Action: run `gitv go` to execute ", Ansi.CYAN) + Ansi.bold(advisory.actionableFix().toString()));
                    }
                }
            } else {
                System.out.println(Ansi.color("\nNo automatic recovery suggestions available.", Ansi.GRAY));
            }
            return 1;
        }
    }
}
