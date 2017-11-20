package com.bordereast.ninja.pebble.extensions;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Function;

import ninja.i18n.Messages;

public class NinjaExtension extends AbstractExtension {
    

    private Messages messages;
    
    public NinjaExtension() {
    }
    
    public NinjaExtension(Messages messages) {
        this.messages = messages;
    }
    
    @Override
    public Map<String, Function> getFunctions() {
        Map<String, Function> functions = new HashMap<>();
        functions.put("n11s", new n11sFunction(messages));
        return functions;
    }
}
