package com.intrbiz.virt.manager.virt;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import com.intrbiz.Util;
import com.intrbiz.data.DataException;
import com.intrbiz.virt.VirtError;
import com.intrbiz.virt.libvirt.model.definition.LibVirtDomainDef;

public class LibvirtMachineTypes
{
    private final File dir;
    
    public LibvirtMachineTypes(File dir)
    {
        this.dir = dir;
    }
    
    public Set<String> getAvailableMachineTypeFamilies()
    {
        Set<String> types = new TreeSet<String>();
        File[] files = this.dir.listFiles();
        if (files != null)
        {
            for (File file : files)
            {
                String name = file.getName();
                if (file.isFile() && name.endsWith(".xml"))
                {
                    name = name.substring(0, name.lastIndexOf('.'));
                    if (! Util.isEmpty(name))
                        types.add(name);
                }
            }
        }
        return types;
    }
    
    public LibVirtDomainDef loadMachineType(String family)
    {
        File template = new File(this.dir, family + ".xml");
        if (template.exists())
        {
            try
            {
                return LibVirtDomainDef.read(template);
            }
            catch (DataException de)
            {
                throw new VirtError("Failed to load machine type family '" + family + "' from " + template.getAbsolutePath(), de);
            }
        }
        return null;
    }
}
