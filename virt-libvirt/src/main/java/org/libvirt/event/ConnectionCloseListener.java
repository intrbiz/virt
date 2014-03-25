package org.libvirt.event;

import org.libvirt.Connect;

public interface ConnectionCloseListener extends EventListener {
    
    void onClose(Connect connection, ConnectionCloseReason reason);

}
