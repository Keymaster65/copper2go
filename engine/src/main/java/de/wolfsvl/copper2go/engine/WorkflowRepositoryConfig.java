package de.wolfsvl.copper2go.engine;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class WorkflowRepositoryConfig {

    public final String branch;
    public final String workflowGitURI;
    public final String workflowBase;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public WorkflowRepositoryConfig(
            @JsonProperty(required = true, value = "branch") final String branch,
            @JsonProperty(required = true, value = "workflowGitURI") final String workflowGitURI,
            @JsonProperty(required = true, value = "workflowBase") final String workflowBase

    ) {
        this.branch = branch;
        this.workflowGitURI = workflowGitURI;
        this.workflowBase = workflowBase;
    }

    public WorkflowRepositoryConfig withBranch(final String branch){
        return new WorkflowRepositoryConfig(branch, this.workflowGitURI, this.workflowBase);
    }
}
