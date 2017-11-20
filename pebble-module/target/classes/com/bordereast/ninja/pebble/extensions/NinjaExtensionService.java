package com.bordereast.ninja.pebble.extensions;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Singleton;
import com.mitchellbosecke.pebble.extension.Extension;

@Singleton
public class NinjaExtensionService {

    private final List<Extension> extensions = new ArrayList<Extension>();
    
    public void addExtension(Extension extension) {
        extensions.add(extension);
    }
    
    public List<Extension> getExtensions(){
        return extensions;
    }
    
}
