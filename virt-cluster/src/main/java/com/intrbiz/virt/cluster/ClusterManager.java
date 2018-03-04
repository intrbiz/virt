package com.intrbiz.virt.cluster;


import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.intrbiz.Util;

public class ClusterManager
{
    public static final String DEFAULT_INSTANCE_NAME = "virt";
    
    private static final Logger logger = Logger.getLogger(ClusterManager.class);
    
    private final String instanceName;
    
    private Config hazelcastConfig;

    private HazelcastInstance hazelcastInstance;

    private boolean started = false;
    
    private Map<Class<?>, ClusterComponent> components = new ConcurrentHashMap<Class<?>, ClusterComponent>();

    public ClusterManager(String instanceName, String environment)
    {
        super();
        Objects.nonNull(instanceName);
        Objects.nonNull(environment);
        this.instanceName = instanceName + "." + environment;
        this.registerDefaultComponents();
    }
    
    public ClusterManager(String environment)
    {
        this(DEFAULT_INSTANCE_NAME, environment);
    }
    
    protected void registerDefaultComponents()
    {
    }
    
    public synchronized <T extends ClusterComponent> T registerComponent(T component)
    {
        Class<?> type = component.getClass();
        if (this.components.containsKey(type))
            throw new RuntimeException("Component " + type.getSimpleName() + " is already registered");
        this.components.put(type, component);
        return component;
    }
    
    @SuppressWarnings("unchecked")
    public <T extends ClusterComponent> T getComponent(Class<T> componentClass)
    {
        return (T) this.components.get(componentClass);
    }

    public synchronized void start()
    {
        try
        {
            if (!this.started)
            {
                // configure hazelcast
                this.hazelcastConfig = this.loadHazelcaastConfig();
                this.hazelcastConfig.setInstanceName(this.instanceName);
                this.configComponents();
                // start hazelcast
                this.hazelcastInstance = Hazelcast.getOrCreateHazelcastInstance(this.hazelcastConfig);
                this.startComponents();
                this.registerShutdownHook();
                // done
                this.started = true;
                logger.info("Hazelcast cluster started");
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to start Hazelcast Cluster Manager", e);
        }
    }
    
    private Config loadHazelcaastConfig() throws Exception
    {
        String hazelcastConfigFile = Util.coalesceEmpty(System.getProperty("hazelcast.config"), System.getenv("hazelcast_config"));
        if (hazelcastConfigFile != null)
        {
            // when using a config file, you must configure the balsa.sessions map
            return new XmlConfigBuilder(hazelcastConfigFile).build();
        }
        // setup the default configuration
        return new Config();
    }
    
    private void configComponents()
    {
        this.components.values().stream().sorted().forEach((component) -> {
            logger.info("Configuring cluster component: " + component.getClass().getSimpleName());
            component.config(this, this.hazelcastConfig);
        });
    }
    
    private void startComponents()
    {
        this.components.values().stream().sorted().forEach((component) -> {
            logger.info("Starting cluster component: " + component.getClass().getSimpleName());
            component.start(this, this.hazelcastInstance);
        });
    }
    
    private void shutdownComponents()
    {
        this.components.values().stream().sorted((a, b) -> b.compareTo(a)).forEach((component) -> {
            logger.info("Shutting down cluster component: " + component.getClass().getSimpleName());
            component.shutdown();
        });
    }
    
    public HazelcastInstance getHazelcastInstance()
    {
        return this.hazelcastInstance;
    }
    
    public synchronized void shutdown()
    {
        if (this.started)
        {
            this.shutdownComponents();
            this.hazelcastInstance.shutdown();
        }
    }
    
    private void registerShutdownHook()
    {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run()
            {
                ClusterManager.this.shutdown();
            }
        });
    }
}
