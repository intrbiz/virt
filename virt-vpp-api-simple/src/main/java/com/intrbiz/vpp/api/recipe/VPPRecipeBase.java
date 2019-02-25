package com.intrbiz.vpp.api.recipe;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * A smart recipe which can create useful thing using VPP.
 * 
 * Recipes are intended to be idempotent ways of modelling what you want VPP to achieve, 
 * the recipe will then take care of ensuring VPP is in the desired state.
 */
@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type")
public abstract class VPPRecipeBase implements VPPRecipe
{
    public static final String getType(Class<? extends VPPRecipe> clazz)
    {
        JsonTypeName typeName = clazz.getAnnotation(JsonTypeName.class);
        return typeName != null ? typeName.value() : clazz.getSimpleName();
    }
    
    @JsonIgnore
    private final String type;
    
    private String name;
    
    private Set<String> depends = new HashSet<String>();
    
    public VPPRecipeBase()
    {
        super();
        this.type = getType(this.getClass());
    }
    
    public VPPRecipeBase(String name)
    {
        this();
        this.name = name;
    }
    
    public VPPRecipeBase(String name, String... depends)
    {
        this(name);
        for (String depend : depends)
        {
            this.depends.add(depend);
        }
    }
    
    public VPPRecipeBase(String name, Set<String> depends)
    {
        this(name);
        if (depends != null) this.depends.addAll(depends);
    }
    
    @Override
    @JsonIgnore
    public String getType()
    {
        return this.type;
    }
    
    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public Set<String> getDepends()
    {
        return depends;
    }

    @Override
    public void setDepends(Set<String> depends)
    {
        this.depends = depends;
    }
    
    @Override
    public void addDepends(String depend)
    {
        if (depend != null) this.depends.add(depend);
    }
    
    @Override
    public void addDepends(VPPRecipe depend)
    {
        if (depend != null) this.addDepends(depend.getName());
    }
    
    @Override
    @JsonIgnore
    public int getDependCount()
    {
        return this.depends.size();
    }
}
