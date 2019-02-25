package com.intrbiz.virt.model.metadata.network.v1;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
public abstract class NetworkConfigV1
{
    public NetworkConfigV1()
    {
        super();
    }
}
