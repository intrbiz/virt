package com.intrbiz.virt.dash.router.api;

import java.util.List;

import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.balsa.metadata.WithDataAdapter;
import com.intrbiz.metadata.CurrentPrincipal;
import com.intrbiz.metadata.Get;
import com.intrbiz.metadata.JSON;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.RequireValidPrincipal;
import com.intrbiz.virt.VirtDashApp;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.model.Account;
import com.intrbiz.virt.model.PersistentVolume;
import com.intrbiz.virt.model.User;

@Prefix("/api/volume")
@RequireValidPrincipal()
public class VolumeAPIRouter extends Router<VirtDashApp>
{    
    @Get("/account/name/:name/")
    @JSON
    @WithDataAdapter(VirtDB.class)
    public List<PersistentVolume> listVolumes(VirtDB db, @CurrentPrincipal User user, String accountName)
    {
        Account account = notNull(user.getOwnedAccounts().stream().filter(a -> a.getName().equals(accountName)).findFirst().orElse(null));
        return db.getPersistentVolumesForAccount(account.getId());
    }
}
