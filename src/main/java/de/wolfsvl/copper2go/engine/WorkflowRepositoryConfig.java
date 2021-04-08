package de.wolfsvl.copper2go.engine;

public class WorkflowRepositoryConfig {

    public String branch;
    public String workflowGitURI;
    public String workflowBase;

    public WorkflowRepositoryConfig() {
    }

    public WorkflowRepositoryConfig(
            final String branch,
            final String workflowGitURI,
            final String workflowBase
    ) {
        this.branch = branch;
        this.workflowGitURI = workflowGitURI;
        this.workflowBase = workflowBase;
    }
}
