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

import org.slf4j.Logger;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.Route;
import ninja.i18n.Lang;
import ninja.i18n.Messages;
import ninja.template.TemplateEngine;
import ninja.template.TemplateEngineHelper;
import ninja.utils.NinjaProperties;
import ninja.utils.ResponseStreams;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;


/**
 * 
 * Original @author jjfidalgo, modified by agrothe to included 
 * injected properties
 */

@Singleton
public class PebbleTemplateEngine implements TemplateEngine {

    private final Messages messages;
    private final Lang lang;
    private final Logger logger;
    private final NinjaExceptionHandler exceptionHandler;
    private final PebbleEngine engine;
    private final NinjaProperties ninjaProperties;

    @Inject
    public PebbleTemplateEngine(Messages messages,
                                Lang lang,
                                Logger ninjaLogger,
                                NinjaExceptionHandler exceptionHandler,
                                NinjaProperties ninjaProperties,
                                PebbleEngine engine) {

        this.messages = messages;
        this.lang = lang;
        this.logger = ninjaLogger;
        this.exceptionHandler = exceptionHandler;
        this.ninjaProperties = ninjaProperties;
        this.engine = engine;
    }

    public void invoke(Context context, Result result) {

        String templateName = getTemplateName(context, result);
        Locale locale = getLocale(context, result);
        Map templateProperties = getTemplateProperties(context, result);

        render(context, result, templateName, templateProperties, locale);
    }

    private void render(Context context, Result result, String templateName, Map templateProperties, Locale locale) {

        logger.debug("Rendering template={} with properties={} for locale={}", templateName, templateProperties, locale);

        try {
            ResponseStreams responseStreams = context.finalizeHeaders(result);
            PrintWriter writer = new PrintWriter(responseStreams.getWriter());
            
            if(templateName.startsWith("/") || templateName.startsWith("\\")) {
                templateName=templateName.substring(1, templateName.length());
            }
            
            logger.info("Getting template: " + templateName);
            PebbleTemplate compiledTemplate = engine.getTemplate(templateName);

            compiledTemplate.evaluate(writer, templateProperties, locale);

            writer.flush();
            writer.close();
        } catch (Exception e) {
            handleServerError(context, e, locale);
        }
    }

    private Locale getLocale(Context context, Result result) {

        Optional<Result> oResult = Optional.of(result);
        Optional<String> oLanguage = lang.getLanguage(context, oResult);

        Locale locale = lang.getLocaleFromStringOrDefault(oLanguage);

        logger.debug("Browser's locale={}", locale);

        return locale;
    }

    private String getTemplateName(Context context, Result result) {

        TemplateEngineHelper templateEngineHelper = new TemplateEngineHelper();
        Route route = context.getRoute();

        String templateName = templateEngineHelper.getTemplateForResult(route, result, 
                ninjaProperties.getWithDefault(Constants.PROP_FILE_EXTENSION, Constants.PROP_FILE_EXTENSION_DEFAULT));
        if (templateName != null
            && !templateName.isEmpty()
            && templateName.charAt(0) == File.separatorChar) {

            templateName = templateName.substring(1);
        }

        logger.debug("Template name={}", templateName);

        return templateName;
    }

    /**
     * Just collect the properties and put them into a Map
     *
     **/
    private Map getTemplateProperties(Context context, Result result) {

        Object renderableResult = result.getRenderable();
        Map map = initializeTemplatePropertiesMap(renderableResult);

        insertContextPath(context, map);
        insertLanguageProperty(context, result, map);
        insertSessionProperties(context, map);
        insertFlashProperties(context, result, map);

        logger.debug("Template properties={}", map);

        return map;
    }

    /**
     * Get an initialized map with the received object
     */
    private Map initializeTemplatePropertiesMap(Object renderableResult) {

        Map map;

        if (renderableResult == null) {
            map = Maps.newHashMap();
        } else if (renderableResult instanceof Map) {
            map = (Map) renderableResult;
        } else {
            map = createTemplatePropertiesMapAndInsert(renderableResult);
        }

        return map;
    }

    /**
     * Getting an arbitrary Object, put that into the root of the template
     * properties map
     *
     * If you are rendering something like Results.ok().render(new MyObject())
     * Assume MyObject has a public String name field.
     *
     * You can then access the fields in the template like that:
     * {myObject.publicField}
     *
     */
    private Map createTemplatePropertiesMapAndInsert(Object renderableResult) {

        String realClassNameLowerCamelCase = CaseFormat.UPPER_CAMEL.to(
            CaseFormat.LOWER_CAMEL, renderableResult.getClass()
                .getSimpleName());

        Map map = Maps.newHashMap();
        map.put(realClassNameLowerCamelCase, renderableResult);

        return map;
    }

    /**
     * Put the context path into the map
     */
    private void insertContextPath(Context context, Map map) {

        String contextPath = context.getContextPath();
        map.put("contextPath", contextPath);

        logger.debug("Context path={}", contextPath);
    }

    /**
     * Set language from framework. You can access it in the templates as {lang}
     */
    private void insertLanguageProperty(Context context, Result result, Map map) {

        Optional<String> oLanguage = lang.getLanguage(context, Optional.of(result));
        if (oLanguage.isPresent()) {
            String language = oLanguage.get();
            map.put("lang", language);

            logger.debug("Language={}", language);
        }
    }

    /**
     * Put all entries of the session cookie to the map. You can access the
     * values by their key in the cookie
     *
     */
    private void insertSessionProperties(Context context, Map map) {

        if (!context.getSession().isEmpty()) {
            Map<String, String> sessionData = context.getSession().getData();
            map.put("session", sessionData);

            logger.debug("Session data={}", sessionData);
        }
    }

    /**
     * Convenience method to translate possible flash scope keys.
     *
     * If you want to set messages with placeholders please do that in your
     * controller. We only can set simple messages. Eg. A message like
     * "errorMessage=my name is: {0}" => translate in controller and pass
     * directly. A message like " errorMessage=An error occurred" => use that as
     * errorMessage.
     *
     * get keys via {flash.KEYNAME}
     */
    private void insertFlashProperties(Context context, Result result, Map map) {

        Map<String, String> flashCookieMap = Maps.newHashMap();
        Set<Entry<String, String>> currentFlashCookieDataSet = context.getFlashScope().getCurrentFlashCookieData().entrySet();

        for (Entry<String, String> entry : currentFlashCookieDataSet) {

            Optional<String> messageValueOptional = messages.get(entry.getValue(), context, Optional.of(result));

            String messageValue;
            if (!messageValueOptional.isPresent()) {
                messageValue = entry.getValue();
            } else {
                messageValue = messageValueOptional.get();
            }

            flashCookieMap.put(entry.getKey(), messageValue);
        }

        map.put("flash", flashCookieMap);

        logger.debug("Flash map={}", flashCookieMap);
    }

    private void handleServerError(Context context, Exception e, Locale locale) {

        logger.error("Server error", e);

        PebbleTemplate compiledTemplate = null;

        try {
            compiledTemplate = engine.getTemplate(
                    ninjaProperties.getWithDefault(Constants.PROP_500_ERROR_VIEW_LOCATION, 
                            Constants.PROP_500_ERROR_VIEW_LOCATION_DEFAULT));
        } catch (PebbleException intEx) {
            logger.error("Error getting error template", intEx);
            throw new IllegalStateException(intEx);
        }

        Writer writer = new StringWriter();

        try {
            compiledTemplate.evaluate(writer, null, locale);
        } catch (PebbleException  pe) {
            logger.error("Error evaluation error template", pe);
            throw new IllegalStateException(pe);
        } catch (IOException ioe) {
            logger.error("Error evaluation error template", ioe);
            throw new IllegalStateException(ioe);
        }

        String output = writer.toString();

        ResponseStreams outStream = context.finalizeHeaders(Results.internalServerError());

        exceptionHandler.handleException(e, output, outStream);
    }

    public String getContentType() {
        return ninjaProperties.getWithDefault(
                Constants.PROP_MANAGED_CONTENT_TYPE, Constants.PROP_MANAGED_CONTENT_TYPE_DEFAULT);
    }

    public String getSuffixOfTemplatingEngine() {
        return ninjaProperties.getWithDefault(Constants.PROP_FILE_EXTENSION, Constants.PROP_FILE_EXTENSION_DEFAULT);
    } 

}
