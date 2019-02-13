package com.intrbiz.virt.manager.virt;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.intrbiz.virt.cluster.model.MachineState;
import com.intrbiz.virt.config.VirtManagerCfg;
import com.intrbiz.virt.event.model.MachineEO;
import com.intrbiz.virt.manager.Manager;
import com.intrbiz.virt.manager.virt.model.HostInfo;

public interface VirtManager extends Manager<VirtManagerCfg>
{   
    Set<String> getAvailableMachineTypeFamilies();
    
    HostInfo getHostInfo();
    
    List<MachineState> discoverMachines();
    
    MachineState getMachine(UUID id);
    
    void createMachine(MachineEO machine);
    
    void start(MachineEO machine);
    
    void stop(MachineEO machine, boolean force);
    
    void reboot(MachineEO machine, boolean force);
    
    void releaseMachine(MachineEO machine);
    
    void terminateMachine(MachineEO machine);
}
