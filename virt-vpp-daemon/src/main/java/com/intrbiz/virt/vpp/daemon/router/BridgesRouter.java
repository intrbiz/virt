package com.intrbiz.virt.vpp.daemon.router;

import java.util.List;

import com.intrbiz.metadata.CoalesceMode;
import com.intrbiz.metadata.Delete;
import com.intrbiz.metadata.Get;
import com.intrbiz.metadata.IsaBoolean;
import com.intrbiz.metadata.IsaInt;
import com.intrbiz.metadata.JSON;
import com.intrbiz.metadata.Param;
import com.intrbiz.metadata.Post;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.vpp.api.model.BridgeDomainDetail;
import com.intrbiz.vpp.api.model.BridgeDomainId;
import com.intrbiz.vpp.api.model.InterfaceIndex;
import com.intrbiz.vpp.api.model.SplitHorizonGroup;
import com.intrbiz.vpp.api.model.Tag;

@Prefix("/bridge")
public class BridgesRouter extends VppBaseRouter
{
    @Get("/")
    @JSON
    public List<BridgeDomainDetail> listBridgeDomains() throws Exception
    {
        return bridge().listBridgeDomains().get();
    }
    
    @Post("/id/:id")
    @JSON
    public Boolean createBridgeDomain(
            @IsaInt int id, 
            @Param("learn") @IsaBoolean(defaultValue = true, coalesce = CoalesceMode.ALWAYS) Boolean learn, 
            @Param("forward") @IsaBoolean(defaultValue = true, coalesce = CoalesceMode.ALWAYS) Boolean forward, 
            @Param("uu_flood") @IsaBoolean(defaultValue = true, coalesce = CoalesceMode.ALWAYS) Boolean uuFlood, 
            @Param("flood") @IsaBoolean(defaultValue = true, coalesce = CoalesceMode.ALWAYS) Boolean flood, 
            @Param("arp_term") @IsaBoolean(defaultValue = true, coalesce = CoalesceMode.ALWAYS) Boolean arpTerm, 
            @Param("mac_age") @IsaInt(defaultValue = 5, coalesce = CoalesceMode.ALWAYS) int macAge, 
            @Param("tag") String bdTag
    ) throws Exception
    {
        bridge().createBridgeDomain(new BridgeDomainId(id), learn, forward, uuFlood, flood, arpTerm, macAge, Tag.fromString(bdTag)).get();
        return true;
    }
    
    @Delete("/id/:id")
    @JSON
    public Boolean createBridgeDomain(@IsaInt int id) throws Exception
    {
        bridge().removeBridgeDomain(new BridgeDomainId(id)).get();
        return true;
    }
    
    @Post("/id/:id/interface/:index")
    @JSON
    public Boolean addInterfaceToBridgeDomain(
            @IsaInt int id,
            @IsaInt int index,
            @Param("bvi") @IsaBoolean(defaultValue = false, coalesce = CoalesceMode.ALWAYS) Boolean bridgeVirtualInterface,
            @Param("shg") @IsaInt(defaultValue = 0, coalesce = CoalesceMode.ALWAYS) int splitHorizonGroup
    ) throws Exception
    {
        bridge().addInterfaceToBridgeDomain(new BridgeDomainId(id), new InterfaceIndex(index), bridgeVirtualInterface, new SplitHorizonGroup(splitHorizonGroup)).get();
        return true;
    }
    
    @Delete("/id/:id/interface/:index")
    @JSON
    public Boolean removeInterfaceFromBridgeDomain(
            @IsaInt int id,
            @IsaInt int index,
            @Param("bvi") @IsaBoolean(defaultValue = false, coalesce = CoalesceMode.ALWAYS) Boolean bridgeVirtualInterface,
            @Param("shg") @IsaInt(defaultValue = 0, coalesce = CoalesceMode.ALWAYS) int splitHorizonGroup
    ) throws Exception
    {
        bridge().removeInterfaceFromBridgeDomain(new BridgeDomainId(id), new InterfaceIndex(index), bridgeVirtualInterface, new SplitHorizonGroup(splitHorizonGroup)).get();
        return true;
    }
    
}
