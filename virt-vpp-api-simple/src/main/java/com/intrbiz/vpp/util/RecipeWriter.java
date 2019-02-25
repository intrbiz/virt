package com.intrbiz.vpp.util;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import static java.nio.file.StandardOpenOption.*;

public class RecipeWriter
{
    private static final Charset UTF8 = Charset.forName("UTF8");
    
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
        }
        return null;
    }
    
    public void toFile(Object value, File file) throws IOException {
        try
        {
            String yaml = this.toString(value);
            try (FileChannel channel = FileChannel.open(file.toPath(), WRITE, CREATE, TRUNCATE_EXISTING, DSYNC))
            {
                channel.write(UTF8.encode(yaml));
                channel.force(false);
            }
        }
        catch (JsonProcessingException e)
        {
            throw new IOException("Failed to serailize to YAML", e);
        }
    }
    
}
