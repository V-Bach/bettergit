package gitv.suggestion;

import gitv.git.RepoContext;
import gitv.suggestion.rule.ActionRule;
import gitv.suggestion.rule.RuleAggregator;
import gitv.suggestion.rule.RuleResponse;
import gitv.suggestion.rule.Signal;
import gitv.suggestion.rule.SignalLayer;
import gitv.suggestion.rule.impl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DecisionEngine {

    private final List<ActionRule> rules;
    private final SignalLayer signalLayer;
    private final RuleAggregator aggregator;

    public DecisionEngine() {
        this.rules = new ArrayList<>();
        this.rules.add(new CommitRule());
        this.rules.add(new StageRule());
        this.rules.add(new PullRule());
        this.rules.add(new PushRule());
        this.rules.add(new DivergenceRule());

        this.signalLayer = new SignalLayer();
        this.aggregator = new RuleAggregator();
    }

    public DecisionResult decide(RepoContext context) {
        Set<Signal> signals = signalLayer.generateSignals(context);
        
        List<RuleResponse> responses = new ArrayList<>();
        for (ActionRule rule : rules) {
            Optional<RuleResponse> response = rule.evaluate(signals);
            response.ifPresent(responses::add);
        }

        RuleAggregator.AggregationResult result = aggregator.aggregate(responses);

        return new DecisionResult(result.getGoal(), result.getIntents(), result.getAppliedRules());
    }
}

