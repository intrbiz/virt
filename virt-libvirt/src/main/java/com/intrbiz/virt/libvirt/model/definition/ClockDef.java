package com.intrbiz.virt.libvirt.model.definition;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "clock")
@XmlType(name = "clock")
public class ClockDef
{
    private String offset;
    
    private List<TimerDef> timer = new LinkedList<TimerDef>();

    @XmlAttribute(name = "offset")
    public String getOffset()
    {
        return offset;
    }

    public void setOffset(String offset)
    {
        this.offset = offset;
    }

    @XmlElementRef(type = TimerDef.class)
    public List<TimerDef> getTimer()
    {
        return timer;
    }

    public void setTimer(List<TimerDef> timer)
    {
        this.timer = timer;
    }
}
