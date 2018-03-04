package com.intrbiz.virt.libvirt.model.definition;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "interface")
@XmlType(name = "interface")
public class InterfaceDef
{
    private String type;

    private MACDef mac;

    private SourceDef source;

    private AddressDef address;

    private TargetDef target;

    private ModelDef model;
    
    public InterfaceDef()
    {
        super();
    }
    
    public InterfaceDef(String type, MACDef mac, SourceDef source, ModelDef model)
    {
        super();
        this.type = type;
        this.mac = mac;
        this.source = source;
        this.model = model;
    }
    
    public InterfaceDef(String type, MACDef mac, SourceDef source, ModelDef model, TargetDef target)
    {
        super();
        this.type = type;
        this.mac = mac;
        this.source = source;
        this.model = model;
        this.target = target;
    }
    
    public InterfaceDef(String type, MACDef mac, ModelDef model, TargetDef target)
    {
        super();
        this.type = type;
        this.mac = mac;
        this.model = model;
        this.target = target;
    }

    @XmlAttribute(name = "type")
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @XmlElementRef(type = MACDef.class)
    public MACDef getMac()
    {
        return mac;
    }

    public void setMac(MACDef mac)
    {
        this.mac = mac;
    }

    @XmlElementRef(type = SourceDef.class)
    public SourceDef getSource()
    {
        return source;
    }

    public void setSource(SourceDef source)
    {
        this.source = source;
    }

    @XmlElementRef(type = AddressDef.class)
    public AddressDef getAddress()
    {
        return address;
    }

    public void setAddress(AddressDef address)
    {
        this.address = address;
    }

    @XmlElementRef(type = TargetDef.class)
    public TargetDef getTarget()
    {
        return target;
    }

    public void setTarget(TargetDef target)
    {
        this.target = target;
    }

    @XmlElementRef(type=ModelDef.class)
    public ModelDef getModel()
    {
        return model;
    }

    public void setModel(ModelDef model)
    {
        this.model = model;
    }

    public static InterfaceDef read(String def)
    {
        try
        {
            JAXBContext ctx = JAXBContext.newInstance(InterfaceDef.class);
            Unmarshaller u = ctx.createUnmarshaller();
            return (InterfaceDef) u.unmarshal(new StringReader(def));
        }
        catch (JAXBException e)
        {
            throw new RuntimeException(e);
        }
    }

    public String toString()
    {
        try
        {
            JAXBContext ctx = JAXBContext.newInstance(InterfaceDef.class);
            Marshaller m = ctx.createMarshaller();
            m.setProperty(Marshaller.JAXB_FRAGMENT, true);
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter w = new StringWriter();
            m.marshal(this, w);
            return w.toString();
        }
        catch (JAXBException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static InterfaceDef virtioBridge(String mac, String bridge)
    {
        return new InterfaceDef("bridge", new MACDef(mac), SourceDef.bridge(bridge), ModelDef.virtio());
    }
    
    public static InterfaceDef virtioBridge(String mac, String bridge, TargetDef target)
    {
        return new InterfaceDef("bridge", new MACDef(mac), SourceDef.bridge(bridge), ModelDef.virtio(), target);
    }
    
    public static InterfaceDef virtioEthernet(String mac, String bridge, TargetDef target)
    {
        return new InterfaceDef("ethernet", new MACDef(mac), ModelDef.virtio(), target);
    }
}
