package com.intrbiz.virt.libvirt.model.wrapper;

public class LibVirtHostInterface implements Comparable<LibVirtHostInterface>
{
    private final String name;
    
    private final String macAddress;
    
    public LibVirtHostInterface(String name, String macAddress)
    {
        this.name = name;
        this.macAddress = macAddress;
    }

    public String getName()
    {
        return name;
    }

    public String getMacAddress()
    {
        return macAddress;
    }

    @Override
    public int compareTo(LibVirtHostInterface o)
    {
        return this.name.compareTo(o.name);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        LibVirtHostInterface other = (LibVirtHostInterface) obj;
        if (name == null)
        {
            if (other.name != null) return false;
        }
        else if (!name.equals(other.name)) return false;
        return true;
    }
}
