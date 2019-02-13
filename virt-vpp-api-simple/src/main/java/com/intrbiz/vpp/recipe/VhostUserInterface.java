package com.intrbiz.vpp.recipe;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.intrbiz.vpp.api.VPPSimple;
import com.intrbiz.vpp.api.model.InterfaceDetail;
import com.intrbiz.vpp.api.model.InterfaceIndex;
import com.intrbiz.vpp.api.model.MACAddress;
import com.intrbiz.vpp.api.model.MTU;
import com.intrbiz.vpp.api.model.Tag;
import com.intrbiz.vpp.api.model.VhostUserMode;
import com.intrbiz.vpp.api.recipe.VPPInterfaceRecipe;
import com.intrbiz.vpp.util.RecipeWriter;

/**
 * Create an vhost-user based host interface
 */
@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type")
@JsonTypeName("interface.vhostuser")
public class VhostUserInterface implements VPPInterfaceRecipe
{
    @JsonProperty("socket")
    private String socket;

    @JsonProperty("mode")
    private VhostUserMode mode;

    @JsonProperty("mac")
    private MACAddress macAddress;

    @JsonProperty("mtu")
    private MTU mtu;

    @JsonProperty("tag")
    private Tag tag;

    private transient InterfaceIndex currentInterfaceIndex;

    public VhostUserInterface(String socket, VhostUserMode mode, MACAddress macAddress, MTU mtu, Tag tag)
    {
        this.socket = Objects.requireNonNull(socket);
        this.mode = mode;
        this.macAddress = Objects.requireNonNull(macAddress);
        this.mtu = Objects.requireNonNull(mtu);
        this.tag = Objects.requireNonNull(tag);
    }
    
    public VhostUserInterface()
    {
        super();
    }

    public String getSocket()
    {
        return socket;
    }

    public VhostUserMode getMode()
    {
        return mode;
    }

    public Tag getTag()
    {
        return tag;
    }

    public MACAddress getMacAddress()
    {
        return macAddress;
    }

    public MTU getMtu()
    {
        return mtu;
    }

    @Override
    @JsonIgnore
    public InterfaceIndex getCurrentInterfaceIndex()
    {
        return currentInterfaceIndex;
    }

    public void setSocket(String socket)
    {
        this.socket = socket;
    }

    public void setMode(VhostUserMode mode)
    {
        this.mode = mode;
    }

    public void setMacAddress(MACAddress macAddress)
    {
        this.macAddress = macAddress;
    }

    public void setMtu(MTU mtu)
    {
        this.mtu = mtu;
    }

    public void setTag(Tag tag)
    {
        this.tag = tag;
    }

    protected InterfaceIndex findExistingInterface(VPPSimple session) throws InterruptedException, ExecutionException
    {
        for (InterfaceDetail iface : session.interfaces().listInterfaces().get())
        {
            if (this.tag.equals(iface.getTag())) return iface.getIndex();
        }
        return null;
    }

    protected InterfaceIndex createInterface(VPPSimple session) throws InterruptedException, ExecutionException
    {
        System.out.println("Creating vhost-user interface: " + this.tag + " with socket " + this.socket);
        InterfaceIndex iface = session.interfaces().createVhostUserInterface(new File(this.socket).toPath(), this.mode, this.macAddress).get();
        session.interfaces().setInterfaceTag(iface, this.tag);
        return iface;
    }

    protected void setupInterface(VPPSimple session) throws InterruptedException, ExecutionException
    {
        System.out.println("Configuring vhost-user interface: " + this.tag);
        session.interfaces().setInterfaceMTU(this.currentInterfaceIndex, this.mtu);
        session.interfaces().setInterfaceUp(this.currentInterfaceIndex);
    }

    @Override
    public void apply(VPPSimple session) throws InterruptedException, ExecutionException
    {
        this.currentInterfaceIndex = this.findExistingInterface(session);
        if (this.currentInterfaceIndex == null) this.currentInterfaceIndex = this.createInterface(session);
        this.setupInterface(session);
    }
    
    public String toString()
    {
        return RecipeWriter.getDefault().toString(this);
    }
    
    // Factory stuff
    
    public static final String getVMSocket(MACAddress vmMACAddress)
    {
        return new File(getVMSocketDir(), "vm-" + vmMACAddress.toCompactString() + ".sock").getAbsolutePath();
    }
    
    public static final File getVMSocketDir()
    {
        return new File(System.getProperty("vm.interface.dir", "/var/run/vms"));
    }
    
    public static VhostUserInterface forVM(MACAddress vmMACAddress)
    {
        return new VhostUserInterface(getVMSocket(vmMACAddress), VhostUserMode.CLIENT, MACAddress.random(), MTU.DEFAULT, Tag.getVMInterfaceTag(vmMACAddress));
    }
}
