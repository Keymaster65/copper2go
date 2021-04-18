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
package io.github.keymaster65.copper2go.connector.standardio;

import io.github.keymaster65.copper2go.engine.EventChannel;

public class StandardOutEventChannelImpl implements EventChannel {
    @Override
    public void event(String event) {
        System.out.println(event); // NOSONAR
    }

    @Override
    public void errorEvent(String event) {
        System.err.println(event); // NOSONAR
    }
}
