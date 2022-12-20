/*
 * Copyright 2021 Wolf Sluyterman van Langeweyde
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.keymaster65.copper2go.engine.scotty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class WorkflowRepositoryConfig {

    public final String branch;
    public final String workflowGitURI;
    public final String workflowBase;
    public final int checkIntervalSec;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public WorkflowRepositoryConfig(
            @JsonProperty(required = true, value = "branch") final String branch,
            @JsonProperty(required = true, value = "workflowGitURI") final String workflowGitURI,
            @JsonProperty(required = true, value = "workflowBase") final String workflowBase,
            @JsonProperty(required = true, value = "checkIntervalSec") final int checkIntervalSec

    ) {
        this.branch = branch;
        this.workflowGitURI = workflowGitURI;
        this.workflowBase = workflowBase;
        this.checkIntervalSec = checkIntervalSec;
    }
}
