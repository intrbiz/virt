package com.intrbiz.vpp.util;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.intrbiz.vpp.api.recipe.VPPBridgesRecipe;
import com.intrbiz.vpp.api.recipe.VPPInterfaceRecipe;
import com.intrbiz.vpp.recipe.Bridge;
import com.intrbiz.vpp.recipe.HostInterface;
import com.intrbiz.vpp.recipe.VMBridges;
import com.intrbiz.vpp.recipe.VMHost;
import com.intrbiz.vpp.recipe.VMInterconnect;
import com.intrbiz.vpp.recipe.VMInterface;
import com.intrbiz.vpp.recipe.VMMetadata;
import com.intrbiz.vpp.recipe.VMMetadataBridge;
import com.intrbiz.vpp.recipe.VMNetworks;
import com.intrbiz.vpp.recipe.VXLANMesh;
import com.intrbiz.vpp.recipe.VXLANTunnel;
import com.intrbiz.vpp.recipe.VethHostInterface;
import com.intrbiz.vpp.recipe.VhostUserInterface;

public class RecipeReader
{
    private static final RecipeReader DEFAULT = new RecipeReader(Bridge.class, HostInterface.class, VethHostInterface.class, VhostUserInterface.class, VMBridges.class, VMHost.class, VMMetadata.class, VMNetworks.class, VMInterconnect.class,
            VMInterface.class, VMMetadataBridge.class, VXLANMesh.class, VXLANTunnel.class, VPPBridgesRecipe.class, VPPInterfaceRecipe.class);
    
    public static final RecipeReader getDefault() {
        return DEFAULT;
    }
    
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID).enable(YAMLGenerator.Feature.MINIMIZE_QUOTES));
    
    public RecipeReader() {
        super();
    }
    
    public RecipeReader(Class<?>... classes) {
        super();
        this.registerSubType(classes);
    }
    
    public void registerSubType(Class<?>... classes) {
        this.mapper.registerSubtypes(classes);
    }

    public <T> T fromString(Class<T> type, String value) {
        try
        {
            return (T) this.mapper.readValue(value, type);
        }
        catch (IOException e)
        {
        }
        return null;
    }
    
    public <T> T fromFile(Class<T> type, File file) {
        try
        {
            return (T) this.mapper.readValue(file, type);
        }
        catch (IOException e)
        {
        }
        return null;
    }
}
