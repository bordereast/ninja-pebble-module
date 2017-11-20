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

import java.util.concurrent.Executors;

public interface Constants {
    
    public static final String PROP_FILE_EXTENSION = "ninja.template.pebble.fileExt";
    public static final String PROP_FILE_EXTENSION_DEFAULT = ".html";
    
    public static final String PROP_MANAGED_CONTENT_TYPE = "ninja.template.pebble.managedContentType";
    public static final String PROP_MANAGED_CONTENT_TYPE_DEFAULT = "text/html";
    
    public static final String PROP_500_ERROR_VIEW_LOCATION = "ninja.template.pebble.500ErrorViewLocation";
    public static final String PROP_500_ERROR_VIEW_LOCATION_DEFAULT = "views/error/500internalServerError.html";
    
    public static final String PROP_CACHE_ACTIVE = "ninja.template.pebble.cacheActive";
    public static final boolean PROP_CACHE_ACTIVE_DEFAULT = true;
    
    // one of (CachedThreadPool, FixedThreadPool(int), ScheduledThreadPool(int), SingleThreadScheduledExecutor, WorkStealingPool(optional int))
    public static final String PROP_EXECUTOR_SERVICE = "ninja.template.pebble.executorServiceClass";
    public static final String PROP_EXECUTOR_SERVICE_DEFAULT = "CachedThreadPool"; // 
    
    // -1 if one of (CachedThreadPool, SingleThreadScheduledExecutor) or > 0 if one of (FixedThreadPool, ScheduledThreadPool) and either -1 or > 0 for (WorkStealingPool)
    public static final String PROP_EXECUTOR_SERVICE_SIZE = "ninja.template.pebble.poolSize";
    public static final int PROP_EXECUTOR_SERVICE_SIZE_DEFAULT = -1; // 
    
    public static final String PROP_STRICT_VARIABLES = "ninja.template.pebble.strictVariables";
    public static final boolean PROP_STRICT_VARIABLES_DEFAULT = false;
    
    public static final String PROP_DEFAULT_LOCALE = "ninja.template.pebble.defaultLocale";
    public static final String PROP_DEFAULT_LOCALE_DEFAULT = "en-US";
    

    public static final String PROP_DELIM_COMMENT_OPEN = "ninja.template.pebble.delimiterCommentOpen";
    public static final String PROP_DELIM_COMMENT_CLOSE = "ninja.template.pebble.delimiterCommentClose";

    public static final String PROP_DELIM_EXECUTE_OPEN = "ninja.template.pebble.delimiterExecuteOpen";
    public static final String PROP_DELIM_EXECUTE_CLOSE = "ninja.template.pebble.delimiterExecuteClose";

    public static final String PROP_DELIM_PRINT_OPEN = "ninja.template.pebble.delimiterPrintOpen";
    public static final String PROP_DELIM_PRINT_CLOSE = "ninja.template.pebble.delimiterPrintClose";

    public static final String PROP_WHITE_SPACE_TRIM = "ninja.template.pebble.whitespaceTrim";
    
    public static final String PROP_DELIM_COMMENT_OPEN_DEFAULT = "{#";
    public static final String PROP_DELIM_COMMENT_CLOSE_DEFAULT = "#}";
    public static final String PROP_DELIM_EXECUTE_OPEN_DEFAULT = "{%";
    public static final String PROP_DELIM_EXECUTE_CLOSE_DEFAULT = "%}";
    public static final String PROP_DELIM_PRINT_OPEN_DEFAULT = "{{";
    public static final String PROP_DELIM_PRINT_CLOSE_DEFAULT = "}}";
    public static final String PROP_WHITE_SPACE_TRIM_DEFAULT = "-";
    

    /*
     *                 delimiterCommentOpen, 
                delimiterCommentClose, 
                delimiterExecuteOpen, 
                delimiterExecuteClose, 
                delimiterPrintOpen, 
                delimiterPrintClose, 
                whitespaceTrim
     */
}
