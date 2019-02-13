package com.intrbiz.vpp.api.module;

import java.util.concurrent.Future;

public interface Graph
{
    Future<Integer> nodeIndex(String nodeName);
}
