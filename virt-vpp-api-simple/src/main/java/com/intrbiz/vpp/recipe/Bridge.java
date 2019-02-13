package com.intrbiz.vpp.recipe;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.intrbiz.vpp.api.VPPSimple;
import com.intrbiz.vpp.api.model.BridgeDomainDetail;
import com.intrbiz.vpp.api.model.BridgeDomainId;
import com.intrbiz.vpp.api.model.Tag;
import com.intrbiz.vpp.api.recipe.VPPBridgeRecipe;
import com.intrbiz.vpp.api.recipe.VPPRecipe;
import com.intrbiz.vpp.util.RecipeWriter;

@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type")
@JsonTypeName("bridge")
public class Bridge extends VPPRecipe implements VPPBridgeRecipe
{
    @JsonProperty("id")
    private BridgeDomainId id;

    @JsonProperty("mac_age")
    private int macAgeSeconds;

    @JsonProperty("tag")
    private Tag tag;

    public Bridge(String name, BridgeDomainId id, int macAgeSeconds, Tag tag)
    {
        super(name);
        this.id = Objects.requireNonNull(id);
        this.macAgeSeconds = macAgeSeconds <= 0 ? 5 : macAgeSeconds;
        this.tag = Objects.requireNonNull(tag);
    }
    
    public Bridge() {
        super();
    }

    @Override
    public BridgeDomainId getId()
    {
        return id;
    }

    public int getMacAgeSeconds()
    {
        return macAgeSeconds;
    }

    public Tag getTag()
    {
        return tag;
    }

    public void setId(BridgeDomainId id)
    {
        this.id = id;
    }

    public void setMacAgeSeconds(int macAgeSeconds)
    {
        this.macAgeSeconds = macAgeSeconds <= 0 ? 5 : macAgeSeconds;
    }

    public void setTag(Tag tag)
    {
        this.tag = tag;
    }

    protected boolean findExistingBridgeDomain(VPPSimple session) throws InterruptedException, ExecutionException
    {
        for (BridgeDomainDetail bridgeDomain : session.bridge().listBridgeDomains().get())
        {
            if (this.id.equals(bridgeDomain.getId())) return true;
        }
        return false;
    }

    protected void createBridgeDomain(VPPSimple session) throws InterruptedException, ExecutionException
    {
        System.out.println("Creating bridge: " + this.id);
        session.bridge().createBridgeDomain(this.id, true, true, true, true, false, this.macAgeSeconds, this.tag).get();
    }

    @Override
    public void apply(VPPSimple session) throws InterruptedException, ExecutionException
    {
        boolean existing = this.findExistingBridgeDomain(session);
        if (!existing) this.createBridgeDomain(session);
    }
    
    public String toString()
    {
        return RecipeWriter.getDefault().toString(this);
    }
}
