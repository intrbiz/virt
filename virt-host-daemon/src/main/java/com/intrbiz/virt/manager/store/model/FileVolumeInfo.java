package com.intrbiz.virt.manager.store.model;

public class FileVolumeInfo extends VolumeInfo
{
    public enum Format { QCOW2, RAW };
    
    private final String path;
    
    private final Format format;
    
    public FileVolumeInfo(long size, Format format, String path)
    {
        super(size);
        this.format = format;
        this.path = path;
    }

    public String getPath()
    {
        return path;
    }

    public Format getFormat()
    {
        return format;
    }

    @Override
    public String toString()
    {
        return "FileVolumeInfo [path=" + path + ", format=" + format + ", size=" + getSize() + "]";
    }
    
    
}
