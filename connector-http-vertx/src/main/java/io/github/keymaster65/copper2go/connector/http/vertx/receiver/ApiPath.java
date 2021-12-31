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
package io.github.keymaster65.copper2go.connector.http.vertx.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiPath {
    public static final String COPPER2GO_2_API = "/copper2go/2/api/";
    public static final String REQUEST_PATH = COPPER2GO_2_API + "request/";
    public static final String EVENT_PATH = COPPER2GO_2_API + "event/";
    public static final String COPPER2GO_3_API = "/copper2go/3/api/";
    public static final String TWOWAY_PATH = COPPER2GO_3_API + "twoway/";
    public static final String ONEWAY_PATH = COPPER2GO_3_API + "oneway/";

    private static final Logger log = LoggerFactory.getLogger(ApiPath.class);

    private ApiPath() {}

    static boolean logIfDeprecatedApiUri(final String uri) {
        if (uri.startsWith(COPPER2GO_2_API)) {
            log.warn("API v2 is DEPRECATED. Please use v3 instead.");
            return true;
        }
        return false;
    }

    static boolean isApiUri(final String uri) {
        return uri.startsWith(COPPER2GO_3_API) || uri.startsWith(COPPER2GO_2_API);
    }

    static boolean isPayloadUri(final String uri) {
        return uri.startsWith(ONEWAY_PATH) || uri.startsWith(TWOWAY_PATH) || uri.startsWith(REQUEST_PATH) || uri.startsWith(EVENT_PATH);
    }

    static boolean isOnewayUri(final String uri) {
        return uri.startsWith(ONEWAY_PATH) || uri.startsWith(EVENT_PATH);
    }
}
