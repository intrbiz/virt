package com.intrbiz.virt.libvirt.model.definition;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "memory")
@XmlType(name = "memory")
public class MemoryDef extends BytesValue
{
}
