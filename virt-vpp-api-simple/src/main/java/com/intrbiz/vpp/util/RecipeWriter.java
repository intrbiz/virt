package com.intrbiz.vpp.util;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

public class RecipeWriter
{
    private static final RecipeWriter DEFAULT = new RecipeWriter();
    
    public static final RecipeWriter getDefault() {
        return DEFAULT;
    }
    
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID).enable(YAMLGenerator.Feature.MINIMIZE_QUOTES));
    
    public RecipeWriter() {
        super();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public String toString(Object value) {
        try
        {
            return this.mapper.writeValueAsString(value);
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public void toFile(Object value, File file) {
        try
        {
            this.mapper.writeValue(file, value);
        }
        catch (JsonProcessingException e)
        {
        }
        catch (IOException e)
        {
        }
    }
    
}
