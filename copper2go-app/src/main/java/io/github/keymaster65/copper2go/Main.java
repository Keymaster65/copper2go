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

import io.github.keymaster65.copper2go.application.ApplicationLauncher;
import io.github.keymaster65.copper2go.application.Copper2GoApplicationFactory;
import io.github.keymaster65.copper2go.application.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static final String ENV_C2G_CONFIG = "C2G_CONFIG";

    // tested in system or integrationtest
    public static void main(String[] args) throws Exception {
        new ApplicationLauncher(new Copper2GoApplicationFactory(createConfig()).create()).start();
    }

    static Config createConfig() throws IOException {
        return createConfig(System.getenv(ENV_C2G_CONFIG));
    }

    static Config createConfig(final String config) throws IOException {
        if (config != null) {
            log.info("Using config defined in environment variable {}.", config);
            return Config.createFromString(config);
        }
        log.info("Use default config.");
        return Config.createDefault();
    }

    private Main() {
    }
}
