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
package io.github.keymaster65.copper2go;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;

import java.net.URI;
import java.net.URISyntaxException;

class Commons {

    private Commons() {}
    private static final Logger log = LoggerFactory.getLogger(Commons.class);

    static URI getUri(final String path, GenericContainer<?> copper2GoContainer) throws URISyntaxException {
        return new URI(String.format("http://%s:%d%s",
                copper2GoContainer.getHost(),
                copper2GoContainer.getFirstMappedPort(),
                path));
    }
}