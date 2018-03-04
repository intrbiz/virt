package com.intrbiz.virt.model;

import com.intrbiz.data.db.compiler.meta.SQLColumn;
import com.intrbiz.data.db.compiler.meta.SQLPrimaryKey;
import com.intrbiz.data.db.compiler.meta.SQLTable;
import com.intrbiz.data.db.compiler.meta.SQLVersion;
import com.intrbiz.virt.data.VirtDB;

@SQLTable(schema = VirtDB.class, name = "config", since = @SQLVersion({ 1, 0, 0 }) )
public class Config
{
    public static final String FIRST_INSTALL_COMPLETE = "first_install_complete";
    
    @SQLColumn(index = 1, name = "name", since = @SQLVersion({ 1, 0, 0 }) )
    @SQLPrimaryKey()
    private String name;

    @SQLColumn(index = 2, name = "value", since = @SQLVersion({ 1, 0, 0 }) )
    private String value;

    public Config()
    {
        super();
    }

    public Config(String name, String value)
    {
        super();
        this.name = name;
        this.value = value;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
    
    public boolean getBooleanValue()
    {
        return "true".equals(this.value);
    }
    
    public void setBooleanValue(boolean bool)
    {
        this.value = String.valueOf(bool);
    }
}
