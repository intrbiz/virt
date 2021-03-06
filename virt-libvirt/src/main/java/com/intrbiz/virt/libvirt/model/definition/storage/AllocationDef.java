package com.intrbiz.virt.libvirt.model.definition.storage;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.intrbiz.virt.libvirt.model.definition.BytesValue;

@XmlRootElement(name="allocation")
@XmlType(name="allocation")
public class AllocationDef extends BytesValue
{
    public AllocationDef()
    {
        super();
    }

    public AllocationDef(long value, String unit)
    {
        super(value, unit);
    }

    public AllocationDef(long value)
    {
        super(value);
    }
}
