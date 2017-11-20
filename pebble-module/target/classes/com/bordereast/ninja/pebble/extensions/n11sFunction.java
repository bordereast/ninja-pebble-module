package com.bordereast.ninja.pebble.extensions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.mitchellbosecke.pebble.extension.Function;

import ninja.i18n.Messages;

public class n11sFunction implements Function {

    private final Messages messages;
    private final List<String> argumentNames = new ArrayList<>();
    
    public n11sFunction(Messages messages) {
        this.messages = messages;
        argumentNames.add("key");
        argumentNames.add("locale");
        argumentNames.add("params");
    }
    
    @Override
    public List<String> getArgumentNames() {
        return argumentNames;
    }

    @Override
    public Object execute(Map<String, Object> args) {

        String key = (String) args.get("key");
        Optional<String> locale = Optional.of((String) args.get("locale"));
        Object paramsObject = args.get("params");

        if (key != null && locale != null && paramsObject != null) {
            if (paramsObject instanceof List) {
                List<?> list = (List<?>) paramsObject;
                return messages.get(key, locale, list.toArray()).get();
            } else {
                Object[] params = {paramsObject};
                return messages.get(key, locale, params).get();
            }
        } else if(key != null && locale != null) {
            Optional<String> str = messages.get(key, locale);
            return str==null ? null : str.get();
        }
                
        return null;
    }

}
