package com.intrbiz.vpp.api.recipe;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.intrbiz.vpp.api.VPPSimple;

/**
 * A smart recipe which can create useful thing using VPP.
 * 
 * Recipes are intended to be idempotent ways of modelling what you want VPP to achieve, 
 * the recipe will then take care of ensuring VPP is in the desired state.
 */
@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type")
public abstract class VPPRecipe
{
    private String name;
    
    private Set<String> depends = new HashSet<String>();
    
    public VPPRecipe()
    {
        super();
    }
    
    public VPPRecipe(String name)
    {
        super();
        this.name = name;
    }
    
    public VPPRecipe(String name, String... depends)
    {
        this(name);
        for (String depend : depends)
        {
            this.depends.add(depend);
        }
    }
    
    public VPPRecipe(String name, Set<String> depends)
    {
        this(name);
        if (depends != null) this.depends.addAll(depends);
    }
    
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Set<String> getDepends()
    {
        return depends;
    }

    public void setDepends(Set<String> depends)
    {
        this.depends = depends;
    }
    
    public void addDepends(String depend)
    {
        if (depend != null) this.depends.add(depend);
    }

    /**
     * Apply this recipe to the given VPP session
     * @param session the VPP session to configure
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public abstract void apply(VPPSimple session) throws InterruptedException, ExecutionException;
}
