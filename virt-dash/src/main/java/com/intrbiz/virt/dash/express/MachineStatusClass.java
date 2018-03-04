package com.intrbiz.virt.dash.express;

import com.intrbiz.express.ExpressContext;
import com.intrbiz.express.ExpressException;
import com.intrbiz.express.operator.Function;
import com.intrbiz.virt.cluster.model.MachineStatus;

public class MachineStatusClass extends Function
{
    public MachineStatusClass()
    {
        super("status_class");
    }

    @Override
    public Object get(ExpressContext context, Object source) throws ExpressException
    {
        Object status = this.getParameter(0).get(context, source);
        if (status instanceof MachineStatus)
        {
            switch ((MachineStatus) status)
            {
                case RUNNING:
                    return "success";
                case STOPPED:
                    return "danger";
                case PENDING:
                    return "warning";
                default:
                    return "info";
            }
        }
        return "info";
    }
}
