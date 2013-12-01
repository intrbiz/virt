package com.intrbiz.virt.libvirt.model.definition.storage;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.intrbiz.virt.libvirt.model.definition.BytesValue;

@XmlRootElement(name="capacity")
@XmlType(name="capacity")
public class CapacityDef extends BytesValue
{

}
