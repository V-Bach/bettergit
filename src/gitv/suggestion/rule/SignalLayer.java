package gitv.suggestion.rule;

import gitv.git.RepoContext;

import java.util.EnumSet;
import java.util.Set;

public class SignalLayer {
    public Set<Signal> generateSignals(RepoContext context) {
        Set<Signal> signals = EnumSet.noneOf(Signal.class);
        
        if (context.hasStagedChanges()) signals.add(Signal.STAGED_CHANGES);
        if (context.hasUnstagedChanges()) signals.add(Signal.UNSTAGED_CHANGES);
        if (context.isAheadOfRemote()) signals.add(Signal.AHEAD_REMOTE);
        if (context.isBehindRemote()) signals.add(Signal.BEHIND_REMOTE);
        if (!context.hasRemote()) signals.add(Signal.NO_REMOTE);
        
        if (context.isAheadOfRemote() && context.isBehindRemote()) {
            signals.add(Signal.DIVERGED);
        }
        
        return signals;
    }
}
