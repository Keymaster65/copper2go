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
package io.github.keymaster65.copper2go.engine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.jqwik.api.Example;
import org.assertj.core.api.Assertions;

class WorkflowRepositoryConfigTest {

    public static final String BRANCH = "/branch";
    public static final String WORKFLOW_GIT_URI = "/workflowGitURI";
    public static final String WORKFLOW_BASE = "/workflowBase";

    public static final String WORKFLOW_REPOSITORY_CONFIG = """
            {
                "branch": "%s",
                "workflowGitURI": "%s",
                "workflowBase": "%s"
            }
                        """;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Example
    void constructor() throws JsonProcessingException {


        final WorkflowRepositoryConfig workflowRepositoryConfig = objectMapper.readValue(
                String.format(WORKFLOW_REPOSITORY_CONFIG, BRANCH, WORKFLOW_GIT_URI, WORKFLOW_BASE),
                WorkflowRepositoryConfig.class
        );

        Assertions.assertThat(workflowRepositoryConfig.branch).isEqualTo(BRANCH);
        Assertions.assertThat(workflowRepositoryConfig.workflowGitURI).isEqualTo(WORKFLOW_GIT_URI);
        Assertions.assertThat(workflowRepositoryConfig.workflowBase).isEqualTo(WORKFLOW_BASE);
    }

    @Example
    void constructorException() {
        Assertions.assertThatCode(() ->
                        objectMapper.readValue("{}", WorkflowRepositoryConfig.class)
                )
                .isNotInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Missing required creator property 'branch'");
    }
}