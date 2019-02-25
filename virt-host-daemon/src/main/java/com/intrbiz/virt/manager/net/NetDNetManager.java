package com.intrbiz.virt.manager.net;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import com.intrbiz.virt.config.NetManagerCfg;
import com.intrbiz.virt.event.model.MachineEO;
import com.intrbiz.virt.event.model.MachineInterfaceEO;
import com.intrbiz.virt.event.model.NetworkEO;
import com.intrbiz.virt.libvirt.model.definition.SourceDef.MacVTapMode;
import com.intrbiz.virt.manager.HostManagerContext;
import com.intrbiz.virt.manager.HostMetadataStoreContext;
import com.intrbiz.virt.manager.net.model.BridgedInterfaceInfo;
import com.intrbiz.virt.manager.net.model.DirectInterfaceInfo;
import com.intrbiz.virt.manager.net.model.InterfaceInfo;

/**
 * Use our Virt::NetD networking daemon.
 * 
 * NetD runs as a Libvirtd qemu hook and as such doesn't need much management here
 */
public class NetDNetManager implements NetManager
{    
    private final Set<String> supportedTypes = Collections.unmodifiableSet(new TreeSet<String>(Arrays.asList("vxlan", "public", "transfer", "service")));
    
    private NetManagerCfg config;
    
    private String interconnectAddress;
    
    private String metadataVMInterface;
    
    private int publicNetworkId;
    
    private int serviceNetworkId;
    
    private int transferNetworkId;
    
    public NetDNetManager()
    {
        super();
    }
    
    @Override
    public void configure(NetManagerCfg cfg) throws Exception
    {
        this.config = cfg;
        this.metadataVMInterface = cfg.getStringParameterValue("metadata.vm.interface", "metadata_vms");
        this.publicNetworkId = cfg.getIntParameterValue("public.network.id", 20);
        this.serviceNetworkId = cfg.getIntParameterValue("service.network.id", 30);
        this.transferNetworkId = cfg.getIntParameterValue("transfer.network.id", 40);
        this.interconnectAddress = cfg.getStringParameterValue("host.interconnect.address", "127.0.0.1");
    }

    @Override
    public NetManagerCfg getConfiguration()
    {
        return this.config;
    }
    
    @Override
    public void start(HostManagerContext managerContext, HostMetadataStoreContext metadataContext)
    {
    }

    public String getMetadataVMInterface()
    {
        return metadataVMInterface;
    }

    public int getPublicNetworkId()
    {
        return publicNetworkId;
    }

    public int getServiceNetworkId()
    {
        return serviceNetworkId;
    }

    public int getTransferNetworkId()
    {
        return transferNetworkId;
    }

    @Override
    public String getInterconnectAddress()
    {
        return this.interconnectAddress;
    }

    @Override
    public void registerRemoteVMHost(String remoteVMHostAddress)
    {
    }

    @Override
    public Set<String> getSupportedNetworkTypes()
    {
        return this.supportedTypes;
    }
    
    @Override
    public boolean isSupported(NetworkEO net)
    {
        return this.supportedTypes.contains(net.getType());
    }

    @Override
    public void setupNetwork(NetworkEO net)
    {
    }

    @Override
    public InterfaceInfo setupGuestMetadataNIC(MachineEO machine)
    {
        // For metadata we can just use a MacVTap in private mode
        String mac = machine.getCfgMac();
        return new DirectInterfaceInfo(mac, this.metadataVMInterface, MacVTapMode.PRIVATE);
    }

    @Override
    public InterfaceInfo setupGuestNIC(MachineInterfaceEO nic)
    {
        return new BridgedInterfaceInfo(nic.getMac(), this.bridgeName(nic.getNetwork()));
    }
    
    protected String bridgeName(NetworkEO network)
    {
        switch (network.getType())
        {
            case "vxlan":    return bridgeName(network.getVxlanid());
            case "service":  return bridgeName(this.serviceNetworkId);
            case "transfer": return bridgeName(this.transferNetworkId);
            case "public":   return bridgeName(this.publicNetworkId);
        }
        return null;
    }
    
    protected String bridgeName(int networkId)
    {
        return "br-" + Integer.toHexString(networkId);
    }

    @Override
    public void startGuestMetadataNIC(MachineEO machine)
    {
    }

    @Override
    public void startGuestNIC(MachineInterfaceEO nic)
    {
    }

    @Override
    public void stopGuestMetadataNIC(MachineEO machine)
    {        
    }

    @Override
    public void stopGuestNIC(MachineInterfaceEO nic)
    {
    }

    @Override
    public void removeGuestMetadataNIC(MachineEO machine)
    {        
    }

    @Override
    public void removeGuestNIC(MachineInterfaceEO nic)
    {
    }
}
