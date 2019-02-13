package com.intrbiz.vpp.api.module;

import java.util.concurrent.Future;

public interface Info
{
    Future<String> getVersion();
}
