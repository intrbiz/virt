package com.intrbiz.vpp.api.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.intrbiz.vpp.util.RecipeWriter;

@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type")
@JsonTypeName("detail.interface")
public class InterfaceDetail implements Comparable<InterfaceDetail>
{
    private InterfaceIndex index;
    
    private InterfaceIndex parentIndex;

    private String name;

    private boolean adminUp;

    private boolean linkUp;

    private MACAddress macAddress;

    private int mtu;

    private Tag tag;

    public InterfaceIndex getIndex()
    {
        return index;
    }

    public void setIndex(InterfaceIndex index)
    {
        this.index = index;
    }

    public InterfaceIndex getParentIndex()
    {
        return parentIndex;
    }

    public void setParentIndex(InterfaceIndex parentIndex)
    {
        this.parentIndex = parentIndex;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isAdminUp()
    {
        return adminUp;
    }

    public void setAdminUp(boolean adminUp)
    {
        this.adminUp = adminUp;
    }

    public boolean isLinkUp()
    {
        return linkUp;
    }

    public void setLinkUp(boolean linkUp)
    {
        this.linkUp = linkUp;
    }

    public MACAddress getMacAddress()
    {
        return macAddress;
    }

    public void setMacAddress(MACAddress macAddress)
    {
        this.macAddress = macAddress;
    }

    public int getMtu()
    {
        return mtu;
    }

    public void setMtu(int mtu)
    {
        this.mtu = mtu;
    }

    public Tag getTag()
    {
        return tag;
    }

    public void setTag(Tag tag)
    {
        this.tag = tag;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((index == null) ? 0 : index.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        InterfaceDetail other = (InterfaceDetail) obj;
        if (index == null)
        {
            if (other.index != null) return false;
        }
        else if (!index.equals(other.index)) return false;
        return true;
    }

    @Override
    public int compareTo(InterfaceDetail o)
    {
        return this.index.compareTo(o.index);
    }
    
    public String toString()
    {
        return RecipeWriter.getDefault().toString(this);
    }
}
