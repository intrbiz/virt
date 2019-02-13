package com.intrbiz.virt.vpp.daemon.router;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.intrbiz.balsa.engine.impl.route.Route;
import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.metadata.Any;
import com.intrbiz.metadata.Param;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.Text;
import com.intrbiz.virt.VppDaemon;

@Prefix("/help")
public class HelpRouter extends Router<VppDaemon>
{
    @Any("/")
    @Text
    public String help()
    {
        return "VPP Simple Daemon";
    }
    
    @Any("/commands")
    @Text
    public String commands()
    {
        StringBuilder commands = new StringBuilder();
        for (Router<?> router : app().getRouters())
        {
            for (Route route : Route.fromRouter(router.getPrefix(), router))
            {
                commands.append(route.getMethod()).append("\t").append(router.getPrefix()).append(route.getPattern());
                List<Param> params = getParams(route);
                if (! params.isEmpty())
                {
                    commands.append("?").append(params.stream().map(p -> p.value() + "=").collect(Collectors.joining("&")));
                }
                commands.append("\r\n");
            }
        }
        return commands.toString();
    }
    
    private static List<Param> getParams(Route route)
    {
        List<Param> params = new LinkedList<>();
        for (Annotation[] annotations : route.getHandler().getParameterAnnotations())
        {
            for (Annotation annotation : annotations)
            {
                if (annotation instanceof Param)
                {
                    params.add((Param) annotation);
                }
            }
        }
        return params;
    }
}
