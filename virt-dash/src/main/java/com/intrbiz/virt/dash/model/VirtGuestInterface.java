package com.intrbiz.virt.dash.model;

public class VirtGuestInterface
{
    private String macAddress;
    
    private String type;
    
    private String bridge;
    
    public VirtGuestInterface()
    {
        super();
    }

    public String getMacAddress()
    {
        return macAddress;
    }

    public void setMacAddress(String macAddress)
    {
        this.macAddress = macAddress;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getBridge()
    {
        return bridge;
    }

    public void setBridge(String bridge)
    {
        this.bridge = bridge;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((macAddress == null) ? 0 : macAddress.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        VirtGuestInterface other = (VirtGuestInterface) obj;
        if (macAddress == null)
        {
            if (other.macAddress != null) return false;
        }
        else if (!macAddress.equals(other.macAddress)) return false;
        return true;
    }
}
