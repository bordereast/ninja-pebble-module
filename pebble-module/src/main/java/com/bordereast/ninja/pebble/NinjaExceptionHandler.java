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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.utils.NinjaProperties;
import ninja.utils.ResponseStreams;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.Executors;

/**
 * A general exception handler for exceptions. Outputs a readable error in test
 * dev mode. - Outputs a error message with relevant status code in production.
 *
 * @author ra, sojin, kpacha, juan
 */
@Singleton
public class NinjaExceptionHandler {

    private static final String ERROR_MESSAGE_HEAD = "<!-- Pebble Template ERROR MESSAGE STARTS HERE -->"
            + "<script language=javascript>//\"></script>"
            + "<script language=javascript>//\'></script>"
            + "<script language=javascript>//\"></script>"
            + "<script language=javascript>//\'></script>"
            + "</title></xmp></script></noscript></style></object>"
            + "</head></pre></table>"
            + "</form></table></table></table></a></u></i></b>"
            + "<div align=left "
            + "style='background-color:#FFFF00; color:#FF0000; "
            + "display:block; border-top:double; padding:2pt; "
            + "font-size:medium; font-family:Arial,sans-serif; "
            + "font-style: normal; font-variant: normal; "
            + "font-weight: normal; text-decoration: none; "
            + "text-transform: none'>"
            + "<b style='font-size:medium'>Pebble template error!</b>"
            + "<pre><xmp>";

    private static final String ERROR_MESSAGE_TAIL = "</xmp></pre></div></html>";

    private final NinjaProperties ninjaProperties;
    private final Logger ninjaLogger;

    @Inject
    public NinjaExceptionHandler(Logger ninjaLogger,
                                 NinjaProperties ninjaProperties) {
        this.ninjaLogger = ninjaLogger;
        this.ninjaProperties = ninjaProperties;
        
        
    }

    public void handleException(Exception te,
                                String response,
                                ResponseStreams outStream) {

        try {

            Writer out = outStream.getWriter();
            PrintWriter pw = (out instanceof PrintWriter) ? (PrintWriter) out
                    : new PrintWriter(out);

            if (!ninjaProperties.isDev()) {

                String responseToPrint = response;
                if (response == null || response
                        .endsWith(ninjaProperties.getWithDefault(Constants.PROP_FILE_EXTENSION, 
                                Constants.PROP_FILE_EXTENSION_DEFAULT))) {

                    responseToPrint = "Server error!";
                }

                pw.println(responseToPrint);

                ninjaLogger.error(
                        "Templating error. This should not happen in production",
                        te);
            } else {
                // print out full stacktrace if we are in test or dev mode
                pw.println(ERROR_MESSAGE_HEAD);
                te.printStackTrace(pw);
                pw.println(ERROR_MESSAGE_TAIL);

                ninjaLogger.error("Templating error.", te);
            }

            pw.flush();
            pw.close();

        } catch (IOException e) {
            ninjaLogger.error("Error while handling error.", e);
        }
    }
}