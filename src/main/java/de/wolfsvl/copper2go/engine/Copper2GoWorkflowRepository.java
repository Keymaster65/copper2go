package de.wolfsvl.copper2go.engine;

public class Copper2GoWorkflowRepository {

    public final String branch;
    public final String workflowGitURI;
    public final String workflowBase;

    public Copper2GoWorkflowRepository(
            final String branch,
            final String workflowGitURI,
            String workflowBase
    ) {
        this.branch = branch;
        this.workflowGitURI = workflowGitURI;
        this.workflowBase = workflowBase;
    }
}
