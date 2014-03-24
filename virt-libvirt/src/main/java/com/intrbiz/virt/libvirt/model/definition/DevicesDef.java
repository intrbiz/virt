package com.intrbiz.virt.libvirt.model.definition;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "devices")
@XmlType(name = "devices")
public class DevicesDef
{
    private String emulator;

    private List<DiskDef> disks = new LinkedList<DiskDef>();

    private List<ControllerDef> controllers = new LinkedList<ControllerDef>();

    private List<InterfaceDef> interfaces = new LinkedList<InterfaceDef>();

    private List<SerialDef> serials = new LinkedList<SerialDef>();

    private List<ConsoleDef> consoles = new LinkedList<ConsoleDef>();

    private List<InputDef> inputs = new LinkedList<InputDef>();

    private List<GraphicsDef> graphics = new LinkedList<GraphicsDef>();

    private List<SoundDef> sounds = new LinkedList<SoundDef>();

    private List<VideoDef> videos = new LinkedList<VideoDef>();

    private List<MemballonDef> memballoons = new LinkedList<MemballonDef>();
    
    private List<ChannelDef> channels = new LinkedList<ChannelDef>();

    @XmlElement(name = "emulator")
    public String getEmulator()
    {
        return emulator;
    }

    public void setEmulator(String emulator)
    {
        this.emulator = emulator;
    }

    @XmlElementRef(type = DiskDef.class)
    public List<DiskDef> getDisks()
    {
        return disks;
    }

    public void setDisks(List<DiskDef> disks)
    {
        this.disks = disks;
    }

    @XmlElementRef(type = ControllerDef.class)
    public List<ControllerDef> getControllers()
    {
        return controllers;
    }

    public void setControllers(List<ControllerDef> controllers)
    {
        this.controllers = controllers;
    }

    @XmlElementRef(type = InterfaceDef.class)
    public List<InterfaceDef> getInterfaces()
    {
        return interfaces;
    }

    public void setInterfaces(List<InterfaceDef> interfaces)
    {
        this.interfaces = interfaces;
    }

    @XmlElementRef(type = SerialDef.class)
    public List<SerialDef> getSerials()
    {
        return serials;
    }

    public void setSerials(List<SerialDef> serials)
    {
        this.serials = serials;
    }

    @XmlElementRef(type = ConsoleDef.class)
    public List<ConsoleDef> getConsoles()
    {
        return consoles;
    }

    public void setConsoles(List<ConsoleDef> consoles)
    {
        this.consoles = consoles;
    }

    @XmlElementRef(type = InputDef.class)
    public List<InputDef> getInputs()
    {
        return inputs;
    }

    public void setInputs(List<InputDef> inputs)
    {
        this.inputs = inputs;
    }

    @XmlElementRef(type = GraphicsDef.class)
    public List<GraphicsDef> getGraphics()
    {
        return graphics;
    }

    public void setGraphics(List<GraphicsDef> graphics)
    {
        this.graphics = graphics;
    }

    @XmlElementRef(type = SoundDef.class)
    public List<SoundDef> getSounds()
    {
        return sounds;
    }

    public void setSounds(List<SoundDef> sounds)
    {
        this.sounds = sounds;
    }

    @XmlElementRef(type = VideoDef.class)
    public List<VideoDef> getVideos()
    {
        return videos;
    }

    public void setVideos(List<VideoDef> videos)
    {
        this.videos = videos;
    }

    @XmlElementRef(type = MemballonDef.class)
    public List<MemballonDef> getMemballoons()
    {
        return memballoons;
    }

    public void setMemballoons(List<MemballonDef> memballoons)
    {
        this.memballoons = memballoons;
    }

    @XmlElementRef(type = ChannelDef.class)
    public List<ChannelDef> getChannels()
    {
        return channels;
    }

    public void setChannels(List<ChannelDef> channels)
    {
        this.channels = channels;
    }
}
