package com.intrbiz.virt.libvirt.model.definition;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.intrbiz.data.DataException;

public abstract class DeviceDef
{
    @SuppressWarnings("unchecked")
    public static <T extends DeviceDef> T read(String def)
    {
        try
        {
            JAXBContext ctx = JAXBContext.newInstance(DevicesDef.class);
            Unmarshaller u = ctx.createUnmarshaller();
            T d = (T) u.unmarshal(new StringReader(def));
            return d;
        }
        catch (JAXBException e)
        {
            throw new DataException("Failed to parse domain definition XML", e);
        }
    }

    public String toString()
    {
        try
        {
            JAXBContext ctx = JAXBContext.newInstance(DevicesDef.class);
            Marshaller m = ctx.createMarshaller();
            m.setProperty(Marshaller.JAXB_FRAGMENT, true);
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter w = new StringWriter();
            m.marshal(this, w);
            return w.toString();
        }
        catch (JAXBException e)
        {
            throw new DataException("Failed to serialise domain definition to XML", e);
        }
    }
}
