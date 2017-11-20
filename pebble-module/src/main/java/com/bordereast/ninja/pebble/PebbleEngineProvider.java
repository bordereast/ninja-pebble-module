/**
 * Copyright (C) 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bordereast.ninja.pebble;

import java.util.Locale;
import java.util.concurrent.Executors;

import com.bordereast.ninja.pebble.extensions.NinjaExtension;
import com.bordereast.ninja.pebble.extensions.NinjaExtensionService;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.PebbleEngine.Builder;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.lexer.Syntax;

import ninja.i18n.Messages;
import ninja.utils.NinjaProperties;

/**
 * 
 * Original @author jjfidalgo, modified by agrothe to included injected
 * properties and provide configurable Pebble Engine
 */

public class PebbleEngineProvider implements Provider<PebbleEngine> {

    private Extension[] extensions;
    private final NinjaProperties ninjaProperties;
    private final Messages messages;
    private final NinjaExtensionService extensionService;

    @Inject
    public PebbleEngineProvider(NinjaProperties ninjaProperties,
                                NinjaExtensionService extensionService,
                                Messages messages) {
        this.ninjaProperties = ninjaProperties;
        this.messages = messages;
        this.extensionService = extensionService;
    }

    public PebbleEngine get() {

        Builder builder = getPebbleEngineBuilder();

        if (extensionService != null) {
            extensionService.addExtension(new NinjaExtension(messages));
        }

        if (extensionService.getExtensions().size() > 0) {
            extensions = (Extension[]) extensionService.getExtensions()
                    .toArray(new Extension[0]);
        }

        if (extensions == null) {
            return builder.build();
        } else {
            return builder.extension(extensions).build();
        }

    }

    private Builder getPebbleEngineBuilder() {

        Builder builder = new PebbleEngine.Builder();

        // init the Executor Service for multithreading features
        int poolSize = ninjaProperties.getIntegerWithDefault(
                Constants.PROP_EXECUTOR_SERVICE_SIZE,
                Constants.PROP_EXECUTOR_SERVICE_SIZE_DEFAULT);

        switch (ninjaProperties.getWithDefault(Constants.PROP_EXECUTOR_SERVICE,
                Constants.PROP_EXECUTOR_SERVICE_DEFAULT)) {

        case "FixedThreadPool":
            builder.executorService(Executors.newFixedThreadPool(poolSize));
            break;
        case "ScheduledThreadPool":
            builder.executorService(Executors.newScheduledThreadPool(poolSize));
            break;
        case "SingleThreadScheduledExecutor":
            builder.executorService(
                    Executors.newSingleThreadScheduledExecutor());
            break;
        case "WorkStealingPool":
            if (poolSize > 0)
                builder.executorService(Executors.newWorkStealingPool());
            else
                builder.executorService(
                        Executors.newWorkStealingPool(poolSize));
            break;
        case "CachedThreadPool":
        default:
            Executors.newCachedThreadPool();
            break;
        }

        // enable/disable cache
        builder.cacheActive(ninjaProperties.getBooleanWithDefault(
                Constants.PROP_CACHE_ACTIVE,
                Constants.PROP_CACHE_ACTIVE_DEFAULT));

        // enable/disable strict variables
        builder.strictVariables(ninjaProperties.getBooleanWithDefault(
                Constants.PROP_STRICT_VARIABLES,
                Constants.PROP_STRICT_VARIABLES_DEFAULT));

        // set the default locale
        String language = ninjaProperties.getWithDefault(
                Constants.PROP_DEFAULT_LOCALE,
                Constants.PROP_DEFAULT_LOCALE_DEFAULT);

        if (language.contains("-")) {
            String[] codes = language.split("-");
            builder.defaultLocale(new Locale(codes[0], codes[1]));
        } else {
            builder.defaultLocale(new Locale(language));
        }

        // setup the syntax
        Syntax syntax = new Syntax(
                ninjaProperties.getWithDefault(
                        Constants.PROP_DELIM_COMMENT_OPEN,
                        Constants.PROP_DELIM_COMMENT_OPEN_DEFAULT),
                ninjaProperties.getWithDefault(
                        Constants.PROP_DELIM_COMMENT_CLOSE,
                        Constants.PROP_DELIM_COMMENT_CLOSE_DEFAULT),
                ninjaProperties.getWithDefault(
                        Constants.PROP_DELIM_EXECUTE_OPEN,
                        Constants.PROP_DELIM_EXECUTE_OPEN_DEFAULT),
                ninjaProperties.getWithDefault(
                        Constants.PROP_DELIM_EXECUTE_CLOSE,
                        Constants.PROP_DELIM_EXECUTE_CLOSE_DEFAULT),
                ninjaProperties.getWithDefault(Constants.PROP_DELIM_PRINT_OPEN,
                        Constants.PROP_DELIM_PRINT_OPEN_DEFAULT),
                ninjaProperties.getWithDefault(Constants.PROP_DELIM_PRINT_CLOSE,
                        Constants.PROP_DELIM_PRINT_CLOSE_DEFAULT),
                ninjaProperties.getWithDefault(Constants.PROP_WHITE_SPACE_TRIM,
                        Constants.PROP_WHITE_SPACE_TRIM_DEFAULT));

        builder.syntax(syntax);

        return builder;
    }

}
