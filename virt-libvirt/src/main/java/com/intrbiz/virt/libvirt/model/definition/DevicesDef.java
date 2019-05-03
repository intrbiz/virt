package com.intrbiz.virt.libvirt.model.definition;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "devices")
@XmlType(name = "devices")
public class DevicesDef
{
    private String emulator;
    
    private List<DeviceDef> devices = new LinkedList<DeviceDef>();
    
    public DevicesDef()
    {
        super();
    }

    @XmlElement(name = "emulator")
    public String getEmulator()
    {
        return emulator;
    }

    public void setEmulator(String emulator)
    {
        this.emulator = emulator;
    }

    @XmlElementRefs({
        @XmlElementRef(type = ControllerDef.class),
        @XmlElementRef(type = DiskDef.class),
        @XmlElementRef(type = InterfaceDef.class),
        @XmlElementRef(type = SerialDef.class),
        @XmlElementRef(type = ConsoleDef.class),
        @XmlElementRef(type = InputDef.class),
        @XmlElementRef(type = GraphicsDef.class),
        @XmlElementRef(type = SoundDef.class),
        @XmlElementRef(type = VideoDef.class),
        @XmlElementRef(type = MemballonDef.class),
        @XmlElementRef(type = ChannelDef.class),
        @XmlElementRef(type = RNGDef.class),
    })
    public List<DeviceDef> getDevices()
    {
        return devices;
    }

    public void setDevices(List<DeviceDef> devices)
    {
        this.devices = devices;
    }
    
    public void addDevice(DeviceDef device)
    {
        this.devices.add(device);
    }
    
    public <T extends DeviceDef> List<T> getDevices(Class<T> ofType)
    {
        return Collections.unmodifiableList(this.devices.stream().filter(ofType::isInstance).map(ofType::cast).collect(Collectors.toList()));
    }

    public List<DiskDef> getDisks()
    {
        return getDevices(DiskDef.class);
    }

    public List<ControllerDef> getControllers()
    {
        return getDevices(ControllerDef.class);
    }

    public List<InterfaceDef> getInterfaces()
    {
        return getDevices(InterfaceDef.class);
    }

    public List<SerialDef> getSerials()
    {
        return getDevices(SerialDef.class);
    }

    public List<ConsoleDef> getConsoles()
    {
        return getDevices(ConsoleDef.class);
    }

    public List<InputDef> getInputs()
    {
        return getDevices(InputDef.class);
    }

    public List<GraphicsDef> getGraphics()
    {
        return getDevices(GraphicsDef.class);
    }

    public List<SoundDef> getSounds()
    {
        return getDevices(SoundDef.class);
    }

    public List<VideoDef> getVideos()
    {
        return getDevices(VideoDef.class);
    }

    public List<MemballonDef> getMemballoons()
    {
        return getDevices(MemballonDef.class);
    }

    public List<ChannelDef> getChannels()
    {
        return getDevices(ChannelDef.class);
    }

    public List<RNGDef> getRngs()
    {
        return getDevices(RNGDef.class);
    }
}
