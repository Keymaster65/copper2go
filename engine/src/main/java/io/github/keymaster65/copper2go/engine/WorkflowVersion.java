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

public final class WorkflowVersion {
    public final String name;
    public final long major;
    public final long minor;

    private WorkflowVersion(final String name, final long major, final long minor) {
        this.name = name;
        this.major = major;
        this.minor = minor;
    }

    public static WorkflowVersion of(final String uri) throws EngineException {
        try {
            String[] parts = uri.split("/");
            String name = parts[parts.length - 1];
            String majorMinor = parts[parts.length - 2];
            String[] splittedVersion = majorMinor.split("\\.");
            return new WorkflowVersion(
                    name,
                    Long.parseLong(splittedVersion[0]),
                    Long.parseLong(splittedVersion[1])
            );
        } catch (ArrayIndexOutOfBoundsException|NumberFormatException e) {
            throw new EngineException("Unable to get worklow form uri '" + uri + "'.", e);
        }
    }
}