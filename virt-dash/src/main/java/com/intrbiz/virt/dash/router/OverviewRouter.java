package com.intrbiz.virt.dash.router;

import java.util.stream.Collectors;

import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.balsa.metadata.WithDataAdapter;
import com.intrbiz.metadata.Any;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.RequireValidPrincipal;
import com.intrbiz.metadata.SessionVar;
import com.intrbiz.metadata.Template;
import com.intrbiz.virt.cluster.component.MachineStateStore;
import com.intrbiz.virt.dash.App;
import com.intrbiz.virt.dash.model.RunningMachine;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.model.Account;

// Routes for the URL root
@Prefix("/")
@Template("layout/main")
@RequireValidPrincipal()
public class OverviewRouter extends Router<App>
{
    @Any("/")
    @WithDataAdapter(VirtDB.class)
    public void index(VirtDB db, @SessionVar("currentAccount") Account currentAccount)
    {
        var("networks", db.getNetworksForAccount(currentAccount.getId()));
        var("volumes", db.getPersistentVolumesForAccount(currentAccount.getId()));
        // build list of machines
        MachineStateStore mss = app().getClusterManager().getMachineStateStore();
        var("machines", db.getMachinesForAccount(currentAccount.getId()).stream()
                .map((m) -> new RunningMachine(m, mss.getMachineState(m.getId()), mss.getMachineHealth(m.getId())))
                .collect(Collectors.toList()));
        // render
        encode("index");
    }
}
