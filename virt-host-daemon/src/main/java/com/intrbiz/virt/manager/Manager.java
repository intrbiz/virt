package com.intrbiz.virt.manager;

import com.intrbiz.configuration.Configurable;
import com.intrbiz.configuration.Configuration;

public interface Manager<C extends Configuration> extends Configurable<C>
{
    void start(HostManagerContext managerContext, HostMetadataStoreContext metadataContext);
}
