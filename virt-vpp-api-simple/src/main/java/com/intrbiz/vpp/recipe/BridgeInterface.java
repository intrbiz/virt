package com.intrbiz.vpp.recipe;

import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.vpp.api.VPPSimple;
import com.intrbiz.vpp.api.model.SplitHorizonGroup;
import com.intrbiz.vpp.api.recipe.VPPBridgeRecipe;
import com.intrbiz.vpp.api.recipe.VPPInterfaceRecipe;
import com.intrbiz.vpp.api.recipe.VPPRecipeBase;
import com.intrbiz.vpp.api.recipe.VPPRecipeContext;
import com.intrbiz.vpp.util.RecipeWriter;

@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type")
@JsonTypeName("bridge.interface")
public class BridgeInterface extends VPPRecipeBase
{
    public static final String name(String bridgeName, String interfaceName)
    {
        return bridgeName + "<>" + interfaceName;
    }
    
    @JsonProperty("bridge")
    private String bridgeName;
    
    @JsonProperty("interface")
    private String interfaceName;
    
    @JsonProperty("split_horizon_group")
    private SplitHorizonGroup splitHorizonGroup;
    
    public BridgeInterface()
    {
        super();
    }
    
    public BridgeInterface(String bridgeName, String interfaceName)
    {
        super(name(bridgeName, interfaceName));
        this.bridgeName = bridgeName;
        this.interfaceName = interfaceName;
        this.addDepends(bridgeName);
        this.addDepends(interfaceName);
    }
    
    public BridgeInterface(String bridgeName, String interfaceName, SplitHorizonGroup splitHorizonGroup)
    {
        this(bridgeName, interfaceName);
        this.splitHorizonGroup = splitHorizonGroup;
    }
    
    public BridgeInterface(VPPBridgeRecipe bridge, VPPInterfaceRecipe iface)
    {
        this(bridge.getName(), iface.getName());
    }
    
    public BridgeInterface(VPPBridgeRecipe bridge, VPPInterfaceRecipe iface, SplitHorizonGroup splitHorizonGroup)
    {
        this(bridge.getName(), iface.getName(), splitHorizonGroup);
    }

    @Override
    public void apply(VPPSimple session, VPPRecipeContext context) throws InterruptedException, ExecutionException
    {
        VPPBridgeRecipe bridge = context.getDependency(this.bridgeName);
        VPPInterfaceRecipe iface = context.getDependency(this.interfaceName);
        System.out.println("Adding interface " + this.interfaceName + "[" + iface.getCurrentInterfaceIndex() + "] to bridge " + this.bridgeName + "[" + bridge.getId().getValue() + "]");
        if (this.splitHorizonGroup != null)
        {
            session.bridge().addInterfaceToBridgeDomain(bridge.getId(), iface.getCurrentInterfaceIndex(), this.splitHorizonGroup);
        }
        else
        {
            session.bridge().addInterfaceToBridgeDomain(bridge.getId(), iface.getCurrentInterfaceIndex());  
        }
    }
    
    @Override
    public void unapply(VPPSimple session, VPPRecipeContext context) throws InterruptedException, ExecutionException
    {
        // TODO
    }
    
    public String toString()
    {
        return RecipeWriter.getDefault().toString(this);
    }
}
