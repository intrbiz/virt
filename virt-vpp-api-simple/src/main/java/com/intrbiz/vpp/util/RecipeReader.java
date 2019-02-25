package com.intrbiz.vpp.util;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.intrbiz.vpp.api.recipe.VPPBridgeRecipe;
import com.intrbiz.vpp.api.recipe.VPPInterfaceRecipe;
import com.intrbiz.vpp.api.recipe.VPPRecipe;
import com.intrbiz.vpp.recipe.Bridge;
import com.intrbiz.vpp.recipe.BridgeInterface;
import com.intrbiz.vpp.recipe.HostInterface;
import com.intrbiz.vpp.recipe.VXLANTunnel;
import com.intrbiz.vpp.recipe.VethHostInterface;
import com.intrbiz.vpp.recipe.VhostUserInterface;

public class RecipeReader
{
    private static final RecipeReader DEFAULT = new RecipeReader(
            VPPRecipe.class, VPPInterfaceRecipe.class, VPPBridgeRecipe.class,
            Bridge.class, HostInterface.class, VethHostInterface.class, 
            VhostUserInterface.class, BridgeInterface.class, VXLANTunnel.class
    );
    
    public static final RecipeReader getDefault()
    {
        return DEFAULT;
    }
    
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID).enable(YAMLGenerator.Feature.MINIMIZE_QUOTES));
    
    public RecipeReader()
    {
        super();
    }
    
    public RecipeReader(Class<?>... classes)
    {
        super();
        this.registerSubType(classes);
    }
    
    public void registerSubType(Class<?>... classes)
    {
        this.mapper.registerSubtypes(classes);
    }

    public <T> T fromString(Class<T> type, String value)
    {
        try
        {
            return (T) this.mapper.readValue(value, type);
        }
        catch (IOException e)
        {
        }
        return null;
    }
    
    public <T> T fromFile(Class<T> type, File file) throws IOException
    {
        return (T) this.mapper.readValue(file, type);
    }
}
