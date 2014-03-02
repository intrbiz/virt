package org.libvirt.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class virDomainBlockInfo extends Structure {
    public long capacity;
    public long allocation;
    public long physical;

    private static final List fields = Arrays.asList(
            "capacity", "allocation", "physical");

    @Override
    protected List getFieldOrder() {
        return fields;
    }
}
