package com.intrbiz.virt.libvirt.model.definition;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "virt", namespace = "http://intrbiz.net/xml/libvirt")
@XmlType(name = "virt", namespace = "http://intrbiz.net/xml/libvirt")
public class IntrbizMetadataDef
{
    private List<IntrbizMetadataParameterDef> parameters = new LinkedList<IntrbizMetadataParameterDef>();
    
    public IntrbizMetadataDef()
    {
        super();
    }
    
    public IntrbizMetadataDef(IntrbizMetadataParameterDef... params)
    {
        super();
        for (IntrbizMetadataParameterDef param : params)
        {
            this.parameters.add(param);
        }
    }

    @XmlElementRef(type = IntrbizMetadataParameterDef.class)
    public List<IntrbizMetadataParameterDef> getParameters()
    {
        return parameters;
    }

    public void setParameters(List<IntrbizMetadataParameterDef> parameters)
    {
        this.parameters = parameters;
    }
}
