package com.intrbiz.virt.dash.action;

import org.apache.log4j.Logger;

import com.intrbiz.metadata.Action;
import com.intrbiz.virt.cluster.component.SchedulerEventManager;
import com.intrbiz.virt.cluster.model.MachineState;
import com.intrbiz.virt.cluster.model.MachineStatus;
import com.intrbiz.virt.dash.cfg.VirtDashCfg;
import com.intrbiz.virt.event.host.ManageMachine;
import com.intrbiz.virt.event.schedule.CreateMachine;
import com.intrbiz.virt.model.Machine;
import com.intrbiz.virt.model.MachineVolume;
import com.intrbiz.virt.model.PersistentVolume;

public class MachineActions extends ClusteredAction
{
    private static final Logger logger = Logger.getLogger(MachineActions.class);
    
    @Action("machine.create")
    public void createMachine(Machine machine)
    {
        // get the scheduler event manager
        SchedulerEventManager<VirtDashCfg> schedulerEvents = this.getSchedulerEventManager();
        // send the schedule machine event
        logger.info("Creating machine: " + machine.getId() + " " + machine.getName());
        schedulerEvents.sendZoneSchedulerEvent(machine.getZone().getName(), new CreateMachine(machine.toEvent()));
    } 
    
    @Action("machine.reboot")
    public void rebootMachine(Machine machine, boolean force)
    {
        // lookup the machine state to know which host the machine is current assigned too
        MachineState state = this.getMachineStateStore().getMachineState(machine.getId());
        // send a reboot event
        if (state != null)
        {
            this.getHostEventManager().sendEvent(state.getHost(), new ManageMachine(machine.toEvent(), ManageMachine.Action.REBOOT, force));
        }
    }
    
    @Action("machine.stop")
    public void stopMachine(Machine machine, boolean force)
    {
        // lookup the machine state to know which host the machine is current assigned too
        MachineState state = this.getMachineStateStore().getMachineState(machine.getId());
        // send a start event
        if (state != null)
        {
            this.getHostEventManager().sendEvent(state.getHost(), new ManageMachine(machine.toEvent(), ManageMachine.Action.STOP, force));
        }
    }
    
    @Action("machine.start")
    public void startMachine(Machine machine)
    {
        // lookup the machine state to know which host the machine is current assigned too
        MachineState state = this.getMachineStateStore().getMachineState(machine.getId());
        if (state != null)
        {
            // send a start event
            this.getHostEventManager().sendEvent(state.getHost(), new ManageMachine(machine.toEvent(), ManageMachine.Action.START));   
        }
        else
        {
            this.createMachine(machine);
        }
    }
    
    @Action("machine.release")
    public void relaseMachine(Machine machine)
    {
        // lookup the machine state to know which host the machine is current assigned too
        MachineState state = this.getMachineStateStore().getMachineState(machine.getId());
        if (state != null)
        {
            // send a release event
            this.getHostEventManager().sendEvent(state.getHost(), new ManageMachine(machine.toEvent(), ManageMachine.Action.RELEASE));
        }
    }
    
    @Action("machine.terminate")
    public void terminateMachine(Machine machine)
    {
        // lookup the machine state to know which host the machine is current assigned too
        MachineState state = this.getMachineStateStore().getMachineState(machine.getId());
        if (state != null && (state.getStatus() == MachineStatus.RUNNING || state.getStatus() == MachineStatus.STOPPED))
        {
            // send a terminate event
            this.getHostEventManager().sendEvent(state.getHost(), new ManageMachine(machine.toEvent(), ManageMachine.Action.TERMINATE));
        }
    }
    
    @Action("machine.cleanup")
    public void cleanupMachine(Machine machine)
    {
        // remove the machine state
        this.getMachineStateStore().removeMachineState(machine.getId());
    }
    
    @Action("machine.attach_volume")
    public void attachVolumeToMachine(Machine machine, MachineVolume toAttach, PersistentVolume volume)
    {
        // lookup the machine state to know which host the machine is current assigned too
        MachineState state = this.getMachineStateStore().getMachineState(machine.getId());
        if (state != null)
        {
            // send an attach event
            this.getHostEventManager().sendEvent(state.getHost(), new ManageMachine(machine.toEvent(), ManageMachine.Action.ATTACH_VOLUME).withVolume(toAttach.toEvent()));
        }
    }
}
