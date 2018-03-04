package com.intrbiz.virt.dash.action;

import static com.intrbiz.balsa.BalsaContext.*;

import org.apache.log4j.Logger;

import com.intrbiz.metadata.Action;
import com.intrbiz.virt.cluster.ClusterComponent;
import com.intrbiz.virt.cluster.component.HostEventManager;
import com.intrbiz.virt.cluster.component.MachineStateStore;
import com.intrbiz.virt.cluster.component.SchedulerEventManager;
import com.intrbiz.virt.cluster.model.MachineState;
import com.intrbiz.virt.dash.App;
import com.intrbiz.virt.event.host.DestroyMachine;
import com.intrbiz.virt.event.host.RebootMachine;
import com.intrbiz.virt.event.host.StartMachine;
import com.intrbiz.virt.event.host.StopMachine;
import com.intrbiz.virt.event.schedule.ScheduleMachine;
import com.intrbiz.virt.model.Machine;

public class MachineActions
{
    private static final Logger logger = Logger.getLogger(MachineActions.class);
    
    private <T extends ClusterComponent> T getClusterComponent(Class<T> componentClass)
    {
        return ((App) Balsa().app()).getClusterManager().getComponent(componentClass);
    }
    
    @Action("machine.create")
    public void createMachine(Machine machine)
    {
        // get the scheduler event manager
        SchedulerEventManager schedulerEvents = this.getClusterComponent(SchedulerEventManager.class);
        // send the schedule machine event
        logger.info("Creating machine: " + machine.getId() + " " + machine.getName());
        schedulerEvents.sendZoneSchedulerEvent("uk1.a", new ScheduleMachine(machine.toEvent()));
    } 
    
    @Action("machine.reboot")
    public void rebootMachine(Machine machine, boolean force)
    {
        // lookup the machine state to know which host the machine is current assigned too
        MachineState state = this.getClusterComponent(MachineStateStore.class).getMachineState(machine.getId());
        // send a reboot event
        this.getClusterComponent(HostEventManager.class).sendEvent(state.getHost(), new RebootMachine(machine.getId(), force));
    }
    
    @Action("machine.start")
    public void startMachine(Machine machine)
    {
        // lookup the machine state to know which host the machine is current assigned too
        MachineState state = this.getClusterComponent(MachineStateStore.class).getMachineState(machine.getId());
        // send a start event
        this.getClusterComponent(HostEventManager.class).sendEvent(state.getHost(), new StartMachine(machine.getId()));
    }
    
    @Action("machine.stop")
    public void stopMachine(Machine machine)
    {
        // lookup the machine state to know which host the machine is current assigned too
        MachineState state = this.getClusterComponent(MachineStateStore.class).getMachineState(machine.getId());
        // send a start event
        this.getClusterComponent(HostEventManager.class).sendEvent(state.getHost(), new StopMachine(machine.getId()));
    }
    
    @Action("machine.terminate")
    public void terminateMachine(Machine machine)
    {
        // lookup the machine state to know which host the machine is current assigned too
        MachineState state = this.getClusterComponent(MachineStateStore.class).getMachineState(machine.getId());
        // send a start event
        this.getClusterComponent(HostEventManager.class).sendEvent(state.getHost(), new DestroyMachine(machine.toEvent()));
    }
}
