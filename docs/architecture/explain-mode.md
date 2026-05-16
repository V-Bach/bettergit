# Explainability Philosophy

A core tenet of Gitv is that automated tooling must never act as an impenetrable black box. Users must trust the tool, and trust is built on transparency. This is embodied in the `--explain` flag.

## Why Explain Mode Exists

When standard Git users migrate to automated tools, their primary fear is: *"What is this thing going to do to my repository?"*

Explain Mode (`gitv go --explain`) answers this directly. By separating the planning phase (Decision Engine) from the execution phase, we can print the entire intended workflow without making a single change to the disk.

## Architectural Enforcement

Explainability is not an afterthought; it is enforced by the architecture:
- Because the `PlanBuilder` outputs a deterministic data structure, rendering that structure as text is trivial.
- Because there is "no logic leakage" into the Execution Engine, the printed plan is guaranteed to be exactly what will execute.

## Guided Co-Pilot

Explain Mode turns Gitv into an educational tool. Beginners can use `--explain` to learn the correct sequence of Git commands for complex scenarios, essentially using Gitv as an expert co-pilot.
