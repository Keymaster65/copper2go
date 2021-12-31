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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ApiPathTest {

    @ParameterizedTest
    @ValueSource(strings = {
            ApiPath.COPPER2GO_2_API,
            ApiPath.EVENT_PATH,
            ApiPath.REQUEST_PATH
    })
    void logIfDeprecatedApiUriTrue(final String uri) {
        Assertions.assertThat(ApiPath.logIfDeprecatedApiUri(uri)).isTrue();
        Assertions.assertThat(ApiPath.logIfDeprecatedApiUri(uri + "x")).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            ApiPath.COPPER2GO_3_API,
            ApiPath.ONEWAY_PATH,
            ApiPath.TWOWAY_PATH
    })
    void logIfDeprecatedApiUriFalse(final String uri) {
        Assertions.assertThat(ApiPath.logIfDeprecatedApiUri(uri)).isFalse();
        Assertions.assertThat(ApiPath.logIfDeprecatedApiUri(uri + "x")).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            ApiPath.COPPER2GO_2_API,
            ApiPath.COPPER2GO_3_API,
            ApiPath.EVENT_PATH,
            ApiPath.REQUEST_PATH,
            ApiPath.ONEWAY_PATH,
            ApiPath.TWOWAY_PATH
    })
    void isApiUriTrue(final String uri) {
        Assertions.assertThat(ApiPath.isApiUri(uri)).isTrue();
        Assertions.assertThat(ApiPath.isApiUri(uri + "x")).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "/",
            "/.",
            "/license"
    })
    void isApiUriFalse(final String uri) {
        Assertions.assertThat(ApiPath.isApiUri(uri)).isFalse();
        Assertions.assertThat(ApiPath.isApiUri(uri + "x")).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            ApiPath.EVENT_PATH,
            ApiPath.REQUEST_PATH,
            ApiPath.ONEWAY_PATH,
            ApiPath.TWOWAY_PATH
    })
    void isPayloadUriTrue(final String uri) {
        Assertions.assertThat(ApiPath.isPayloadUri(uri)).isTrue();
        Assertions.assertThat(ApiPath.isPayloadUri(uri + "x")).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            ApiPath.COPPER2GO_2_API,
            ApiPath.COPPER2GO_3_API
    })
    void isPayloadUriFalse(final String uri) {
        Assertions.assertThat(ApiPath.isPayloadUri(uri)).isFalse();
        Assertions.assertThat(ApiPath.isPayloadUri(uri + "x")).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            ApiPath.EVENT_PATH,
            ApiPath.ONEWAY_PATH
    })
    void isOnewayUriTrue(final String uri) {
        Assertions.assertThat(ApiPath.isOnewayUri(uri)).isTrue();
        Assertions.assertThat(ApiPath.isOnewayUri(uri + "x")).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            ApiPath.COPPER2GO_2_API,
            ApiPath.COPPER2GO_3_API,
            ApiPath.REQUEST_PATH,
            ApiPath.TWOWAY_PATH
    })
    void isOnewayUriFalse(final String uri) {
        Assertions.assertThat(ApiPath.isOnewayUri(uri)).isFalse();
        Assertions.assertThat(ApiPath.isOnewayUri(uri + "x")).isFalse();
    }
}