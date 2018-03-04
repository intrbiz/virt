package com.intrbiz.virt.manager.vm;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.intrbiz.virt.cluster.model.MachineState;
import com.intrbiz.virt.event.model.MachineEO;
import com.intrbiz.virt.manager.net.NetManager;
import com.intrbiz.virt.manager.store.StoreManager;

public interface VirtManager
{
    void start();
    
    Set<String> getAvailableMachineTypeFamilies();
    
    void createMachine(MachineEO machine, StoreManager storeManager, NetManager netManager);
    
    void rebootMachine(UUID id);
    
    void startMachine(UUID id);
    
    void stopMachine(UUID id);
    
    void destroyMachine(MachineEO machine, StoreManager storeManager, NetManager netManager);
    
    int getHostCPUs();
    
    long getHostMemory();
    
    int getRunningMachines();
    
    long getDefinedMemory();
    
    List<MachineState> getMachineStates();
}
