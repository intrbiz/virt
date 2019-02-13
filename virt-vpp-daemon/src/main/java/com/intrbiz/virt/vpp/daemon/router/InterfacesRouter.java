package com.intrbiz.virt.vpp.daemon.router;

import java.io.File;
import java.util.List;

import com.intrbiz.metadata.Delete;
import com.intrbiz.metadata.Get;
import com.intrbiz.metadata.IsaInt;
import com.intrbiz.metadata.JSON;
import com.intrbiz.metadata.Param;
import com.intrbiz.metadata.Post;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.vpp.api.model.IPv4CIDR;
import com.intrbiz.vpp.api.model.InterfaceDetail;
import com.intrbiz.vpp.api.model.InterfaceIndex;
import com.intrbiz.vpp.api.model.MACAddress;
import com.intrbiz.vpp.api.model.MTU;
import com.intrbiz.vpp.api.model.Tag;
import com.intrbiz.vpp.api.model.VRFIndex;
import com.intrbiz.vpp.api.model.VhostUserMode;

@Prefix("/interface")
public class InterfacesRouter extends VppBaseRouter
{
    @Get("/")
    @JSON
    public List<InterfaceDetail> listInterfaces() throws Exception
    {
        return interfaces().listInterfaces().get();
    }
    
    @Get("/index/:index")
    @JSON
    public InterfaceDetail getInterfaceByIndex(@IsaInt int index) throws Exception
    {
        return notNull(interfaces().getInterface(new InterfaceIndex(index)).get());
    }
    
    @Get("/name/:name")
    @JSON
    public InterfaceDetail getInterfaceByName(String name) throws Exception
    {
        return notNull(interfaces().getInterface(name).get());
    }
    
    @Get("/host/name/:name")
    @JSON
    public InterfaceDetail getHostInterfaceByName(String name) throws Exception
    {
        return notNull(interfaces().getHostInterface(name).get());
    }
    
    @Post("/index/:index/ipv4/:ip")
    @JSON
    public Boolean addIPAddress(@IsaInt int index, String ip) throws Exception
    {
        interfaces().addInterfaceIPv4Address(new InterfaceIndex(index), IPv4CIDR.fromString(ip)).get();
        return true;
    }
    
    @Delete("/index/:index/ipv4/:ip")
    @JSON
    public Boolean removeIPAddress(@IsaInt int index, String ip) throws Exception
    {
        interfaces().removeInterfaceIPv4Address(new InterfaceIndex(index), IPv4CIDR.fromString(ip)).get();
        return true;
    }
    
    @Delete("/index/:index/ipv4/")
    @JSON
    public Boolean removeIPAddresses(@IsaInt int index) throws Exception
    {
        interfaces().removeInterfaceIPv4Addresses(new InterfaceIndex(index)).get();
        return true;
    }
    
    @Post("/index/:index/tag")
    @JSON
    public Boolean setTag(@IsaInt int index, @Param("tag") String tag) throws Exception
    {
        interfaces().setInterfaceTag(new InterfaceIndex(index), new Tag(tag)).get();
        return true;
    }
    
    @Post("/index/:index/vrf/:vrf")
    @JSON
    public Boolean setVRF(@IsaInt int index, @IsaInt int vrf) throws Exception
    {
        interfaces().setInterfaceIPv4VRF(new InterfaceIndex(index), new VRFIndex(vrf)).get();
        return true;
    }
    
    @Post("/index/:index/up")
    @JSON
    public Boolean setUp(@IsaInt int index) throws Exception
    {
        interfaces().setInterfaceUp(new InterfaceIndex(index)).get();
        return true;
    }
    
    @Post("/index/:index/down")
    @JSON
    public Boolean setDown(@IsaInt int index) throws Exception
    {
        interfaces().setInterfaceDown(new InterfaceIndex(index)).get();
        return true;
    }
    
    @Post("/index/:index/mtu/:mtu")
    @JSON
    public Boolean setMTU(@IsaInt int index, @IsaInt int mtu) throws Exception
    {
        interfaces().setInterfaceMTU(new InterfaceIndex(index), new MTU(mtu)).get();
        return true;
    }
    
    @Post("/index/:index/mac/:mac")
    @JSON
    public Boolean setInterfaceMACAddress(@IsaInt int index, String mac) throws Exception
    {
        interfaces().setInterfaceMACAddress(new InterfaceIndex(index), MACAddress.fromString(mac)).get();
        return true;
    }
    
    @Post("/host/name/:name")
    @JSON
    public InterfaceIndex createHostInterface(String name) throws Exception
    {
        return interfaces().createHostInterface(name).get();
    }
    
    @Delete("/host/name/:name")
    @JSON
    public Boolean removeHostInterface(String name) throws Exception
    {
        interfaces().removeHostInterface(name).get();
        return true;
    }
    
    @Post("/loopback")
    @JSON
    public InterfaceIndex createLoopbackInterface() throws Exception
    {
        return notNull(interfaces().createLoopbackInterface(MACAddress.random()).get());
    }
    
    @Delete("/loopback/index/:index")
    @JSON
    public Boolean removeLoopbackInterface(@IsaInt int index) throws Exception
    {
        interfaces().removeLoopbackInterface(new InterfaceIndex(index)).get();
        return true;
    }
    
    @Post("/vhost-user")
    @JSON
    public InterfaceIndex createVhostUserInterface(@Param("socket") String socket, @Param("mode") String mode) throws Exception
    {
        return notNull(interfaces().createVhostUserInterface(new File(socket).toPath(), VhostUserMode.valueOf(mode.toUpperCase()), MACAddress.random()).get());
    }
    
    @Delete("/vhost-user/index/:index")
    @JSON
    public Boolean removeVhostUserInterface(@IsaInt int index) throws Exception
    {
        interfaces().removeVhostUserInterface(new InterfaceIndex(index)).get();
        return true;
    }
}
