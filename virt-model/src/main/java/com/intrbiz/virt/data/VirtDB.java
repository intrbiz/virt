package com.intrbiz.virt.data;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.intrbiz.data.DataManager;
import com.intrbiz.data.cache.Cache;
import com.intrbiz.data.db.DatabaseAdapter;
import com.intrbiz.data.db.DatabaseConnection;
import com.intrbiz.data.db.compiler.DatabaseAdapterCompiler;
import com.intrbiz.data.db.compiler.meta.SQLGetter;
import com.intrbiz.data.db.compiler.meta.SQLOrder;
import com.intrbiz.data.db.compiler.meta.SQLParam;
import com.intrbiz.data.db.compiler.meta.SQLQuery;
import com.intrbiz.data.db.compiler.meta.SQLRemove;
import com.intrbiz.data.db.compiler.meta.SQLSchema;
import com.intrbiz.data.db.compiler.meta.SQLSetter;
import com.intrbiz.data.db.compiler.meta.SQLVersion;
import com.intrbiz.virt.model.ACMEAccount;
import com.intrbiz.virt.model.ACMECertificate;
import com.intrbiz.virt.model.ACMEWellKnown;
import com.intrbiz.virt.model.Account;
import com.intrbiz.virt.model.Config;
import com.intrbiz.virt.model.DNSRecord;
import com.intrbiz.virt.model.DNSZone;
import com.intrbiz.virt.model.DNSZoneRecord;
import com.intrbiz.virt.model.Image;
import com.intrbiz.virt.model.LoadBalancer;
import com.intrbiz.virt.model.LoadBalancerBackendServer;
import com.intrbiz.virt.model.LoadBalancerBackendTarget;
import com.intrbiz.virt.model.LoadBalancerPool;
import com.intrbiz.virt.model.LoadBalancerPoolTCPPort;
import com.intrbiz.virt.model.Machine;
import com.intrbiz.virt.model.MachineNIC;
import com.intrbiz.virt.model.MachineType;
import com.intrbiz.virt.model.MachineTypeFamily;
import com.intrbiz.virt.model.MachineVolume;
import com.intrbiz.virt.model.Network;
import com.intrbiz.virt.model.PersistentVolume;
import com.intrbiz.virt.model.SSHKey;
import com.intrbiz.virt.model.User;
import com.intrbiz.virt.model.UserAccountGrant;
import com.intrbiz.virt.model.Zone;

@SQLSchema(
        name = "virt", 
        version = @SQLVersion({1, 0, 32}),
        tables = {
            Config.class,
            User.class,
            Account.class,
            UserAccountGrant.class,
            SSHKey.class,
            Network.class,
            Image.class,
            MachineType.class,
            Machine.class,
            MachineNIC.class,
            MachineVolume.class,
            Zone.class,
            PersistentVolume.class,
            DNSRecord.class,
            DNSZone.class,
            DNSZoneRecord.class,
            ACMEAccount.class,
            ACMECertificate.class,
            ACMEWellKnown.class,
            LoadBalancerPool.class,
            LoadBalancerPoolTCPPort.class,
            LoadBalancer.class,
            LoadBalancerBackendServer.class,
            LoadBalancerBackendTarget.class,
            MachineTypeFamily.class
        }
)
public abstract class VirtDB extends DatabaseAdapter
{
    /**
     * Compile and register the KanbanDB Database Adapter
     */
    static
    {
        DataManager.getInstance().registerDatabaseAdapter(
                VirtDB.class, 
                DatabaseAdapterCompiler.defaultPGSQLCompiler().compileAdapterFactory(VirtDB.class)
        );
    }
    
    public static void load()
    {
        // do nothing
    }
    
    /**
     * Install the Virt schema into the default database
     */
    public static void install()
    {
        Logger logger = Logger.getLogger(VirtDB.class);
        DatabaseConnection database = DataManager.getInstance().connect();
        DatabaseAdapterCompiler compiler =  DatabaseAdapterCompiler.defaultPGSQLCompiler().setDefaultOwner("virt");
        // check if the schema is installed
        if (! compiler.isSchemaInstalled(database, VirtDB.class))
        {
            logger.info("Installing database schema");
            compiler.installSchema(database, VirtDB.class);
        }
        else
        {
            // check the installed schema is upto date
            if (! compiler.isSchemaUptoDate(database, VirtDB.class))
            {
                logger.info("The installed database schema is not upto date");
                compiler.upgradeSchema(database, VirtDB.class);
            }
            else
            {
                logger.info("The installed database schema is upto date");
            }
        }
    }

    /**
     * Connect to the Bergamot database
     */
    public static VirtDB connect()
    {
        return DataManager.getInstance().databaseAdapter(VirtDB.class);
    }
    
    /**
     * Connect to the Bergamot database
     */
    public static VirtDB connect(DatabaseConnection connection)
    {
        return DataManager.getInstance().databaseAdapter(VirtDB.class, connection);
    }

    public VirtDB(DatabaseConnection connection, Cache cache)
    {
        super(connection, cache);
    }
    
    // Config
    
    @SQLSetter(table = Config.class, name = "set_config", since = @SQLVersion({1, 0, 0}))
    public abstract void setConfig(Config b);
    
    @SQLGetter(table = Config.class, name = "get_config", since = @SQLVersion({1, 0, 0}))
    public abstract Config getConfig(@SQLParam("name") String name);
    
    public Config getOrCreateConfig(String name, String defaultValue)
    {
        Config config = this.getConfig(name);
        if (config == null)
        {
            config = new Config(name, defaultValue);
            this.setConfig(config);
        }
        return config;
    }
    
    public Config getOrCreateConfig(String name)
    {
        return this.getOrCreateConfig(name, null);
    }

    @SQLRemove(table = Config.class, name = "remove_config", since = @SQLVersion({1, 0, 0}))
    public abstract void removeConfig(@SQLParam("name") String name);
    
    @SQLGetter(table = Config.class, name = "list_configs", since = @SQLVersion({1, 0, 0}))
    public abstract List<Config> listConfig();
    
    // Account
    
    @SQLSetter(table = Account.class, name = "set_account", since = @SQLVersion({1, 0, 0}))
    public abstract void setAccount(Account b);
    
    @SQLGetter(table = Account.class, name = "get_account", since = @SQLVersion({1, 0, 0}))
    public abstract Account getAccount(@SQLParam("id") UUID id);
    
    @SQLGetter(table = Account.class, name ="get_account_by_name", since = @SQLVersion({1, 0, 0}))
    public abstract Account getAccountByName(@SQLParam("name") String accountName);
    
    @SQLGetter(table = Account.class, name ="get_active_accounts_for_user", since = @SQLVersion({1, 0, 0}),
            query = @SQLQuery("SELECT a.* FROM virt.account a JOIN virt.user_account_grant uag ON (uag.account_id = a.id) WHERE uag.user_id = p_user_id AND a.active"))
    public abstract List<Account> getActiveAccountsForUser(@SQLParam(virtual = true, value = "user_id") UUID userId);
    
    @SQLGetter(table = Account.class, name ="get_accounts_for_user", since = @SQLVersion({1, 0, 3}),
            query = @SQLQuery("SELECT a.* FROM virt.account a JOIN virt.user_account_grant uag ON (uag.account_id = a.id) WHERE uag.user_id = p_user_id"),
            orderBy = @SQLOrder("name"))
    public abstract List<Account> getAccountsForUser(@SQLParam(virtual = true, value = "user_id") UUID userId);
    
    @SQLGetter(table = Account.class, name ="get_accounts_owned_by_user", since = @SQLVersion({1, 0, 1}),
            query = @SQLQuery("SELECT a.* FROM virt.account a JOIN virt.user_account_grant uag ON (uag.account_id = a.id) WHERE uag.user_id = p_user_id AND uag.role = 0"))
    public abstract List<Account> getAccountsOwnedByUser(@SQLParam(virtual = true, value = "user_id") UUID userId);

    @SQLRemove(table = Account.class, name = "remove_account", since = @SQLVersion({1, 0, 0}))
    public abstract void removeAccount(@SQLParam("id") UUID id);
    
    @SQLGetter(table = Account.class, name = "list_accounts", since = @SQLVersion({1, 0, 0}))
    public abstract List<Account> listAccounts();
    
    // User
    
    @SQLSetter(table = User.class, name = "set_user", since = @SQLVersion({1, 0, 0}))
    public abstract void setUser(User b);
    
    @SQLGetter(table = User.class, name = "get_user", since = @SQLVersion({1, 0, 0}))
    public abstract User getUser(@SQLParam("id") UUID id);
    
    @SQLGetter(table = User.class, name ="get_user_by_email", since = @SQLVersion({1, 0, 0}))
    public abstract User getUserByName(@SQLParam("email") String email);

    @SQLRemove(table = User.class, name = "remove_user", since = @SQLVersion({1, 0, 0}))
    public abstract void removeUser(@SQLParam("id") UUID id);
    
    @SQLGetter(table = User.class, name = "list_users", since = @SQLVersion({1, 0, 0}))
    public abstract List<User> listUsers();
    
    @SQLGetter(table = User.class, name ="get_users_for_account_with_role", since = @SQLVersion({1, 0, 1}),
        query = @SQLQuery("SELECT u.* FROM virt.user u JOIN virt.user_account_grant uag ON (u.id = uag.user_id) WHERE uag.account_id = p_account_id AND uag.role = p_role")
    )
    public abstract List<User> getUserForAccountWithRole(@SQLParam(virtual = true, value = "account_id") UUID accountId, @SQLParam(virtual = true, value="role") int role);
    
    // User Account Grant
    
    @SQLSetter(table = UserAccountGrant.class, name = "set_user_account_grant", since = @SQLVersion({1, 0, 0}))
    public abstract void setUserAccountGrant(UserAccountGrant b);
    
    @SQLGetter(table = UserAccountGrant.class, name ="get_user_account_grants_for_user", since = @SQLVersion({1, 0, 0}))
    public abstract List<UserAccountGrant> getUserAccountGrantsForUser(@SQLParam("user_id") UUID userId);

    @SQLRemove(table = UserAccountGrant.class, name = "remove_user_account_grant", since = @SQLVersion({1, 0, 0}))
    public abstract void removeUserAccountGrant(@SQLParam("user_id") UUID userId, @SQLParam("account_id") UUID accountId);
    
    // SSHKey
    
    @SQLSetter(table = SSHKey.class, name = "set_ssh_key", since = @SQLVersion({1, 0, 0}))
    public abstract void setSSHKey(SSHKey b);
    
    @SQLGetter(table = SSHKey.class, name = "get_ssh_key", since = @SQLVersion({1, 0, 0}))
    public abstract SSHKey getSSHKey(@SQLParam("id") UUID id);

    @SQLRemove(table = SSHKey.class, name = "remove_ssh_key", since = @SQLVersion({1, 0, 0}))
    public abstract void removeSSHKey(@SQLParam("id") UUID id);
    
    @SQLGetter(table = SSHKey.class, name ="get_ssh_keys_for_account", since = @SQLVersion({1, 0, 0}), orderBy = @SQLOrder("name"))
    public abstract List<SSHKey> getSSHKeysForAccount(@SQLParam("account_id") UUID accountId);
    
    @SQLGetter(table = SSHKey.class, name = "list_ssh_keys", since = @SQLVersion({1, 0, 0}))
    public abstract List<SSHKey> listSSHKeys();
    
    // Network
    
    @SQLSetter(table = Network.class, name = "set_network", since = @SQLVersion({1, 0, 0}))
    public abstract void setNetwork(Network b);
    
    @SQLGetter(table = Network.class, name = "get_network", since = @SQLVersion({1, 0, 0}))
    public abstract Network getNetwork(@SQLParam("id") UUID id);

    @SQLRemove(table = Network.class, name = "remove_network", since = @SQLVersion({1, 0, 0}))
    public abstract void removeNetwork(@SQLParam("id") UUID id);
    
    @SQLGetter(table = Network.class, name ="get_networks_for_account", since = @SQLVersion({1, 0, 0}), orderBy = {@SQLOrder("name"), @SQLOrder("zone_id")},
            query = @SQLQuery("SELECT * FROM virt.network WHERE (account_id = p_account_id OR account_id IS NULL)"))
    public abstract List<Network> getNetworksForAccount(@SQLParam("account_id") UUID accountId);
    
    @SQLGetter(table = Network.class, name ="get_networks_for_account_in_zone", since = @SQLVersion({1, 0, 6}), orderBy = @SQLOrder("name"),
            query = @SQLQuery("SELECT * FROM virt.network WHERE (account_id = p_account_id OR account_id IS NULL) AND (zone_id = p_zone_id OR zone_id IS NULL)"))
    public abstract List<Network> getNetworksForAccountInZone(@SQLParam("account_id") UUID accountId, @SQLParam("zone_id") UUID zoneId);
    
    @SQLGetter(table = Network.class, name = "list_networks", since = @SQLVersion({1, 0, 0}))
    public abstract List<Network> listNetworks();
    
    @SQLGetter(table = Network.class, name ="get_networks_in_zone", since = @SQLVersion({1, 0, 6}), orderBy = @SQLOrder("name"),
            query = @SQLQuery("SELECT * FROM virt.network WHERE (zone_id = p_zone_id OR zone_id IS NULL)"))
    public abstract List<Network> getNetworksInZone(@SQLParam("zone_id") UUID zoneId);
    
    @SQLGetter(table = Network.class, name ="get_shared_networks", since = @SQLVersion({1, 0, 15}), orderBy = {@SQLOrder("name")},
            query = @SQLQuery("SELECT * FROM virt.network WHERE account_id IS NULL"))
    public abstract List<Network> getSharedNetworks();
    
    // Image
    
    @SQLSetter(table = Image.class, name = "set_image", since = @SQLVersion({1, 0, 0}))
    public abstract void setImage(Image b);
    
    @SQLGetter(table = Image.class, name = "get_image", since = @SQLVersion({1, 0, 0}))
    public abstract Image getImage(@SQLParam("id") UUID id);

    @SQLRemove(table = Image.class, name = "remove_image", since = @SQLVersion({1, 0, 0}))
    public abstract void removeImage(@SQLParam("id") UUID id);
    
    @SQLGetter(table = Image.class, name ="get_images_for_account", since = @SQLVersion({1, 0, 0}), orderBy = @SQLOrder("name"),
            query = @SQLQuery("SELECT i.* FROM virt.image i WHERE i.account_id = p_account_id OR i.open"))
    public abstract List<Image> getImagesForAccount(@SQLParam("account_id") UUID accountId);
    
    @SQLGetter(table = Image.class, name = "list_images", since = @SQLVersion({1, 0, 0}))
    public abstract List<Image> listImages();
    
    // Machine Type
    
    @SQLSetter(table = MachineType.class, name = "set_machine_type", since = @SQLVersion({1, 0, 0}))
    public abstract void setMachineType(MachineType b);
    
    @SQLGetter(table = MachineType.class, name = "get_machine_type", since = @SQLVersion({1, 0, 0}))
    public abstract MachineType getMachineType(@SQLParam("id") UUID id);

    @SQLRemove(table = MachineType.class, name = "remove_machine_type", since = @SQLVersion({1, 0, 0}))
    public abstract void removeMachineType(@SQLParam("id") UUID id);
    
    @SQLGetter(table = MachineType.class, name = "list_machine_types", orderBy = {@SQLOrder("family"), @SQLOrder("cpus"), @SQLOrder("memory")}, since = @SQLVersion({1, 0, 0}))
    public abstract List<MachineType> listMachineTypes();
    
    // Machine Type Family
    
    @SQLSetter(table = MachineTypeFamily.class, name = "set_machine_type_family", since = @SQLVersion({1, 0, 32}))
    public abstract void setMachineTypeFamily(MachineTypeFamily b);
    
    @SQLGetter(table = MachineTypeFamily.class, name = "get_machine_type_family", since = @SQLVersion({1, 0, 32}))
    public abstract MachineTypeFamily getMachineTypeFamily(@SQLParam("family") String family);

    @SQLRemove(table = MachineTypeFamily.class, name = "remove_machine_type_family", since = @SQLVersion({1, 0, 32}))
    public abstract void removeMachineTypeFamily(@SQLParam("family") String family);
    
    @SQLGetter(table = MachineTypeFamily.class, name = "list_machine_type_families", orderBy = {@SQLOrder("family")}, since = @SQLVersion({1, 0, 32}))
    public abstract List<MachineTypeFamily> listMachineTypeFamilies();
    
    // Zone
    
    @SQLSetter(table = Zone.class, name = "set_zone", since = @SQLVersion({1, 0, 4}))
    public abstract void setZone(Zone b);
    
    @SQLGetter(table = Zone.class, name = "get_zone", since = @SQLVersion({1, 0, 4}))
    public abstract Zone getZone(@SQLParam("id") UUID id);

    @SQLRemove(table = Zone.class, name = "remove_zone", since = @SQLVersion({1, 0, 4}))
    public abstract void removeZone(@SQLParam("id") UUID id);
    
    @SQLGetter(table = Zone.class, name = "list_zones", orderBy = @SQLOrder("name"), since = @SQLVersion({1, 0, 4}))
    public abstract List<Zone> listZones();
    
    // Machine
    
    @SQLSetter(table = Machine.class, name = "set_machine", since = @SQLVersion({1, 0, 0}))
    public abstract void setMachine(Machine b);
    
    @SQLGetter(table = Machine.class, name = "get_machine", since = @SQLVersion({1, 0, 0}))
    public abstract Machine getMachine(@SQLParam("id") UUID id);

    @SQLRemove(table = Machine.class, name = "remove_machine", since = @SQLVersion({1, 0, 0}))
    public abstract void removeMachine(@SQLParam("id") UUID id);
    
    @SQLGetter(table = Machine.class, name ="get_machine_by_name", since = @SQLVersion({1, 0, 11}))
    public abstract Machine getMachineByName(@SQLParam("account_id") UUID accountId, @SQLParam("name") String name);
    
    @SQLGetter(table = Machine.class, name ="get_machines_for_account", since = @SQLVersion({1, 0, 0}), orderBy = @SQLOrder("name"))
    public abstract List<Machine> getMachinesForAccount(@SQLParam("account_id") UUID accountId);
    
    @SQLGetter(table = Machine.class, name ="get_machines_for_account_in_zone", since = @SQLVersion({1, 0, 6}), orderBy = @SQLOrder("name"))
    public abstract List<Machine> getMachinesForAccountInZone(@SQLParam("account_id") UUID accountId, @SQLParam("zone_id") UUID zoneId);
    
    @SQLGetter(table = Machine.class, name ="get_machine_by_cfg_mac", since = @SQLVersion({1, 0, 0}), orderBy = @SQLOrder("name"))
    public abstract Machine getMachineByCfgMAC(@SQLParam("cfg_mac") String cfgMAC);
    
    @SQLGetter(table = Machine.class, name = "list_machines", since = @SQLVersion({1, 0, 0}))
    public abstract List<Machine> listMachines();
    
    @SQLGetter(table = Machine.class, name ="get_machines_in_zone", since = @SQLVersion({1, 0, 6}), orderBy = @SQLOrder("name"))
    public abstract List<Machine> getMachinesInZone(@SQLParam("zone_id") UUID zoneId);
    
    @SQLGetter(table = Machine.class, name ="get_machines_for_account_on_network", since = @SQLVersion({1, 0, 22}), orderBy = @SQLOrder("name"),
            query = @SQLQuery("SELECT m.* FROM virt.machine m "
                    + "JOIN virt.machine_nic mn ON (m.id = mn.machine_id) "
                    + "WHERE m.account_id = p_account_id AND mn.network_id = p_network_id"))
    public abstract List<Machine> getMachinesForAccountOnNetwork(@SQLParam("account_id") UUID accountId, @SQLParam(value = "network_id", virtual = true) UUID networkId);
    
    // Machine NIC
    
    @SQLSetter(table = MachineNIC.class, name = "set_machine_nic", since = @SQLVersion({1, 0, 0}))
    public abstract void setMachineNIC(MachineNIC b);
    
    @SQLGetter(table = MachineNIC.class, name = "get_machine_nic", since = @SQLVersion({1, 0, 0}))
    public abstract MachineNIC getMachineNIC(@SQLParam("machine_id") UUID machineId);

    @SQLRemove(table = MachineNIC.class, name = "remove_machine_nic", since = @SQLVersion({1, 0, 0}))
    public abstract void removeMachineNIC(@SQLParam("machine_id") UUID machineId, @SQLParam("network_id") UUID networkId);
    
    @SQLGetter(table = MachineNIC.class, name ="get_machine_nics_of_machine", since = @SQLVersion({1, 0, 0}), orderBy = @SQLOrder("name"))
    public abstract List<MachineNIC> getMachineNICsOfMachine(@SQLParam("machine_id") UUID machineId);
    
    @SQLGetter(table = MachineNIC.class, name ="get_machine_nics_in_network", since = @SQLVersion({1, 0, 5}), orderBy = @SQLOrder("ipv4"))
    public abstract List<MachineNIC> getMachineNICsInNetwork(@SQLParam("network_id") UUID networkId);
    
    @SQLGetter(table = MachineNIC.class, name = "list_machine_nics", since = @SQLVersion({1, 0, 0}))
    public abstract List<MachineNIC> listMachineNICs();
    
    @SQLGetter(table = MachineNIC.class, name ="lookup_machine_nic", since = @SQLVersion({1, 0, 14}),
            query = @SQLQuery("SELECT mn.* FROM virt.machine_nic mn JOIN virt.machine m ON (mn.machine_id = m.id) WHERE m.account_id = p_account_id AND m.name = p_name AND mn.name = 'eth1'"))
    public abstract List<MachineNIC> lookupMachineNIC(@SQLParam(value = "account_id", virtual = true) UUID accountId, @SQLParam(value = "name", virtual = true) String name);
    
    @SQLGetter(table = MachineNIC.class, name ="lookup_machine_nic_on_network", since = @SQLVersion({1, 0, 14}),
            query = @SQLQuery("SELECT mn.* FROM virt.machine_nic mn JOIN virt.machine m ON (mn.machine_id = m.id) JOIN virt.network n ON (mn.network_id = n.id) WHERE m.account_id = p_account_id AND m.name = p_name AND n.name = p_network_name"))
    public abstract List<MachineNIC> lookupMachineNICOnNetwork(@SQLParam(value = "account_id", virtual = true) UUID accountId, @SQLParam(value = "name", virtual = true) String name, @SQLParam(value = "network_name", virtual = true) String networkName);
    
    // Machine Volume
    
    @SQLSetter(table = MachineVolume.class, name = "set_machine_volume", since = @SQLVersion({1, 0, 0}))
    public abstract void setMachineVolume(MachineVolume b);
    
    @SQLGetter(table = MachineVolume.class, name = "get_machine_volume", since = @SQLVersion({1, 0, 0}))
    public abstract MachineVolume getMachineVolume(@SQLParam("machine_id") UUID machineId, @SQLParam("name") String name);

    @SQLRemove(table = MachineVolume.class, name = "remove_machine_volume", since = @SQLVersion({1, 0, 0}))
    public abstract void removeMachineVolume(@SQLParam("machine_id") UUID machineId, @SQLParam("name") String name);
    
    @SQLGetter(table = MachineVolume.class, name ="get_machine_volumes_of_machine", since = @SQLVersion({1, 0, 0}), orderBy = @SQLOrder("name"))
    public abstract List<MachineVolume> getMachineVolumesOfMachine(@SQLParam("machine_id") UUID machineId);
    
    @SQLGetter(table = MachineVolume.class, name ="get_machine_volumes_attached_to", since = @SQLVersion({1, 0, 7}), orderBy = @SQLOrder("name"))
    public abstract List<MachineVolume> getMachineVolumesAttachedTo(@SQLParam("persistent_volume_id") UUID persistentVolumeId);
    
    @SQLGetter(table = MachineVolume.class, name = "list_machine_volumes", since = @SQLVersion({1, 0, 0}))
    public abstract List<MachineVolume> listMachineVolumes();
    
    // Persistent Volumes
    
    @SQLSetter(table = PersistentVolume.class, name = "set_persistent_volume", since = @SQLVersion({1, 0, 6}))
    public abstract void setPersistentVolume(PersistentVolume b);
    
    @SQLGetter(table = PersistentVolume.class, name = "get_persistent_volume", since = @SQLVersion({1, 0, 6}))
    public abstract PersistentVolume getPersistentVolume(@SQLParam("id") UUID id);

    @SQLRemove(table = PersistentVolume.class, name = "remove_persistent_volume", since = @SQLVersion({1, 0, 6}))
    public abstract void removePersistentVolume(@SQLParam("id") UUID id);
    
    @SQLGetter(table = PersistentVolume.class, name ="get_persistent_volumes_for_account", since = @SQLVersion({1, 0, 6}), orderBy = @SQLOrder("name"))
    public abstract List<PersistentVolume> getPersistentVolumesForAccount(@SQLParam("account_id") UUID accountId);
    
    @SQLGetter(table = PersistentVolume.class, name ="get_persistent_volumes_for_account_in_zone", since = @SQLVersion({1, 0, 6}), orderBy = @SQLOrder("name"))
    public abstract List<PersistentVolume> getPersistentVolumesForAccountInZone(@SQLParam("account_id") UUID accountId, @SQLParam("zone_id") UUID zoneId);
    
    @SQLGetter(table = PersistentVolume.class, name ="get_available_persistent_volumes_for_account_in_zone", since = @SQLVersion({1, 0, 7}), orderBy = @SQLOrder("name"),
            query = @SQLQuery("SELECT pv.* FROM virt.persistent_volume pv, "
                    + "LATERAL (SELECT count(mv.*) FROM virt.machine_volume mv WHERE mv.persistent_volume_id = pv.id) q "
                    + "WHERE pv.account_id = p_account_id AND pv.zone_id = p_zone_id AND (pv.shared OR q.count = 0)")
    )
    public abstract List<PersistentVolume> getAvailablePersistentVolumesForAccountInZone(@SQLParam("account_id") UUID accountId, @SQLParam("zone_id") UUID zoneId);
    
    @SQLGetter(table = PersistentVolume.class, name = "list_persistent_volumes", since = @SQLVersion({1, 0, 6}))
    public abstract List<PersistentVolume> listPersistentVolumes();
    
    @SQLGetter(table = PersistentVolume.class, name ="get_persistent_volumes_in_zone", since = @SQLVersion({1, 0, 6}), orderBy = @SQLOrder("name"))
    public abstract List<PersistentVolume> getPersistentVolumesInZone(@SQLParam("zone_id") UUID zoneId);
    
    // DNS
    
    @SQLSetter(table = DNSRecord.class, name = "set_dns_record", since = @SQLVersion({1, 0, 13}))
    public abstract void setDNSRecord(DNSRecord b);
    
    @SQLGetter(table = DNSRecord.class, name = "get_dns_record", since = @SQLVersion({1, 0, 13}))
    public abstract DNSRecord getDNSRecord(@SQLParam("id") UUID id);

    @SQLRemove(table = DNSRecord.class, name = "remove_dns_record", since = @SQLVersion({1, 0, 13}))
    public abstract void removeDNSRecord(@SQLParam("id") UUID id);
    
    @SQLGetter(table = DNSRecord.class, name ="get_dns_records_for_account", since = @SQLVersion({1, 0, 12}), orderBy = {@SQLOrder("scope"), @SQLOrder("type"), @SQLOrder("name")},
            query = @SQLQuery("SELECT * FROM virt.dns_record WHERE account_id = p_account_id AND (scope = p_scope OR p_scope IS NULL) AND (type = p_type OR p_type IS NULL OR p_type = 'ANY')"))
    public abstract List<DNSRecord> getDNSRecordsForAccount(@SQLParam("account_id") UUID accountId, @SQLParam("scope") String scope, @SQLParam("type") String type);
    
    public List<DNSRecord> getDNSRecordsForAccount(UUID accountId, String scope)
    {
        return this.getDNSRecordsForAccount(accountId, scope, null);
    }
    
    @SQLGetter(table = DNSRecord.class, name ="lookup_dns_records_for_account", since = @SQLVersion({1, 0, 12}), orderBy = {@SQLOrder("scope"), @SQLOrder("type"), @SQLOrder("name")},
            query = @SQLQuery("SELECT * FROM virt.dns_record WHERE account_id = p_account_id AND (scope = p_scope OR p_scope IS NULL) AND (type = p_type OR p_type IS NULL OR p_type = 'ANY') AND (name = p_name OR p_name IS NULL)"))
    public abstract List<DNSRecord> lookupDNSRecordsForAccount(@SQLParam("account_id") UUID accountId, @SQLParam("scope") String scope, @SQLParam("type") String type, @SQLParam("name") String name);
    
    @SQLGetter(table = DNSRecord.class, name = "list_dns_records", since = @SQLVersion({1, 0, 13}))
    public abstract List<DNSRecord> listDNSRecords();
    
    // Zoned DNS
    
    @SQLSetter(table = DNSZone.class, name = "set_dns_zone", since = @SQLVersion({1, 0, 16}))
    public abstract void setDNSZone(DNSZone b);
    
    @SQLGetter(table = DNSZone.class, name = "get_dns_zone", since = @SQLVersion({1, 0, 16}))
    public abstract DNSZone getDNSZone(@SQLParam("id") UUID id);
    
    @SQLGetter(table = DNSZone.class, name = "get_dns_zone_by_name", since = @SQLVersion({1, 0, 16}),
            query = @SQLQuery("SELECT * FROM virt.dns_zone WHERE zone_name = p_zone_name OR aliases @> ARRAY[p_zone_name]"))
    public abstract DNSZone getDNSZoneByName(@SQLParam("zone_name") String zoneName);

    @SQLRemove(table = DNSZone.class, name = "remove_dns_zone", since = @SQLVersion({1, 0, 16}))
    public abstract void removeDNSZone(@SQLParam("id") UUID id);
    
    @SQLGetter(table = DNSZone.class, name ="get_dns_zones_for_account", since = @SQLVersion({1, 0, 16}), orderBy = {@SQLOrder("zone_name")})
    public abstract List<DNSZone> getDNSZonesForAccount(@SQLParam("owning_account_id") UUID accountId);
    
    @SQLGetter(table = DNSZone.class, name = "list_dns_zones", since = @SQLVersion({1, 0, 16}))
    public abstract List<DNSZone> listDNSZones();
    
    @SQLSetter(table = DNSZoneRecord.class, name = "set_dns_zone_record", since = @SQLVersion({1, 0, 16}))
    public abstract void setDNSZoneRecord(DNSZoneRecord b);
    
    @SQLGetter(table = DNSZoneRecord.class, name = "get_dns_zone_record", since = @SQLVersion({1, 0, 16}))
    public abstract DNSZoneRecord getDNSZoneRecord(@SQLParam("id") UUID id);

    @SQLRemove(table = DNSZoneRecord.class, name = "remove_dns_zone_record", since = @SQLVersion({1, 0, 16}))
    public abstract void removeDNSZoneRecord(@SQLParam("id") UUID id);
    
    @SQLGetter(table = DNSZoneRecord.class, name ="get_dns_zone_records_for_zone", since = @SQLVersion({1, 0, 16}), orderBy = {@SQLOrder("name")})
    public abstract List<DNSZoneRecord> getDNSZoneRecordsForZone(@SQLParam("zone_id") UUID zoneId);
    
    @SQLGetter(table = DNSZoneRecord.class, name ="lookup_dns_zone_records", since = @SQLVersion({1, 0, 16}),
            query = @SQLQuery("SELECT r.* FROM virt.dns_zone_record r JOIN virt.dns_zone z ON (r.zone_id = z.id) WHERE z.active AND qualified_names @> ARRAY[p_qualified_name] AND (type = p_type OR p_type IS NULL OR p_type = 'ANY')"))
    public abstract List<DNSZoneRecord> lookupDNSZoneRecords(@SQLParam("type") String type, @SQLParam(value = "qualified_name", virtual = true) String qualifiedName);
    
    @SQLGetter(table = DNSZoneRecord.class, name = "list_dns_zone_records", since = @SQLVersion({1, 0, 16}))
    public abstract List<DNSZoneRecord> listDNSZoneRecords();
    
    /**
     * Search to find the DNSZone which contains the given name
     * @param accountId
     * @param domain
     * @return the matching DNSZone or null
     */
    public DNSZone findDNSZoneForNameInAccount(UUID accountId, String domain)
    {
        List<String> parts = new LinkedList<>(Arrays.asList(domain.split("[.]")));
        while (! parts.isEmpty())
        {
            String name = parts.stream().collect(Collectors.joining(".")) + ".";
            logger.info("Looking for DNS Zone " + name);
            DNSZone zone = this.getDNSZoneByName(name);
            if (zone != null && accountId.equals(zone.getOwningAccountId())) 
                return zone;
            parts.remove(0);
        }
        return null;
    }
    
    // ACME
    
    @SQLSetter(table = ACMEAccount.class, name = "set_acme_account", since = @SQLVersion({1, 0, 17}))
    public abstract void setACMEAccount(ACMEAccount b);
    
    @SQLGetter(table = ACMEAccount.class, name = "get_acme_account", since = @SQLVersion({1, 0, 17}))
    public abstract ACMEAccount getACMEAccount(@SQLParam("id") UUID id);

    @SQLRemove(table = ACMEAccount.class, name = "remove_acme_account", since = @SQLVersion({1, 0, 17}))
    public abstract void removeACMEAccount(@SQLParam("id") UUID id);
    
    @SQLGetter(table = ACMEAccount.class, name ="get_acme_account_for_account", since = @SQLVersion({1, 0, 17}),
            query = @SQLQuery("SELECT * FROM virt.acme_account WHERE account_id IS NULL OR account_id = p_account_id ORDER BY account_id NULLS LAST LIMIT 1"))
    public abstract ACMEAccount getACMEAccountsForAccount(@SQLParam("account_id") UUID accountId);
    
    @SQLGetter(table = ACMEAccount.class, name = "list_acme_accounts", since = @SQLVersion({1, 0, 17}))
    public abstract List<ACMEAccount> listACMEAccounts();
    
    @SQLSetter(table = ACMECertificate.class, name = "set_acme_certificate", since = @SQLVersion({1, 0, 17}))
    public abstract void setACMECertificate(ACMECertificate b);
    
    @SQLGetter(table = ACMECertificate.class, name = "get_acme_certificate", since = @SQLVersion({1, 0, 17}))
    public abstract ACMECertificate getACMECertificate(@SQLParam("id") UUID id);

    @SQLRemove(table = ACMECertificate.class, name = "remove_acme_certificate", since = @SQLVersion({1, 0, 17}))
    public abstract void removeACMECertificate(@SQLParam("id") UUID id);
    
    @SQLGetter(table = ACMECertificate.class, name ="get_acme_certificate_for_account", since = @SQLVersion({1, 0, 17}))
    public abstract List<ACMECertificate> getACMECertificatesForAccount(@SQLParam("account_id") UUID accountId);
    
    @SQLGetter(table = ACMECertificate.class, name = "list_acme_certificates", since = @SQLVersion({1, 0, 17}))
    public abstract List<ACMECertificate> listACMECertificates();
    
    // ACME Well Known
    
    @SQLSetter(table = ACMEWellKnown.class, name = "set_acme_well_known", since = @SQLVersion({1, 0, 28}))
    public abstract void setACMEWellKnown(ACMEWellKnown b);
    
    @SQLGetter(table = ACMEWellKnown.class, name = "get_acme_well_known", since = @SQLVersion({1, 0, 28}))
    public abstract ACMEWellKnown getACMEWellKnown(@SQLParam("host") String host, @SQLParam("name") String name);

    @SQLRemove(table = ACMEWellKnown.class, name = "remove_acme_well_known", since = @SQLVersion({1, 0, 28}))
    public abstract void removeACMEWellKnown(@SQLParam("host") String host, @SQLParam("name") String name);
    
    @SQLGetter(table = ACMEWellKnown.class, name = "list_acme_well_known", since = @SQLVersion({1, 0, 28}))
    public abstract List<ACMEWellKnown> listACMEWellKnown();
    
    // Load balancers
    
    @SQLSetter(table = LoadBalancerPool.class, name = "set_load_balancer_pool", since = @SQLVersion({1, 0, 21}))
    public abstract void setLoadBalancerPool(LoadBalancerPool b);
    
    @SQLGetter(table = LoadBalancerPool.class, name = "get_load_balancer_pool", since = @SQLVersion({1, 0, 21}))
    public abstract LoadBalancerPool getLoadBalancerPool(@SQLParam("id") UUID id);

    @SQLRemove(table = LoadBalancerPool.class, name = "remove_load_balancer_pool", since = @SQLVersion({1, 0, 21}))
    public abstract void removeLoadBalancerPool(@SQLParam("id") UUID id);
    
    @SQLGetter(table = LoadBalancerPool.class, name = "list_load_balancer_pools", since = @SQLVersion({1, 0, 21}))
    public abstract List<LoadBalancerPool> listLoadBalancerPools();
    
    
    @SQLSetter(table = LoadBalancerPoolTCPPort.class, name = "set_load_balancer_pool_tcp_port", since = @SQLVersion({1, 0, 21}))
    public abstract void setLoadBalancerPoolTCPPort(LoadBalancerPoolTCPPort b);
    
    @SQLGetter(table = LoadBalancerPoolTCPPort.class, name = "get_load_balancer_pool_tcp_port", since = @SQLVersion({1, 0, 21}))
    public abstract LoadBalancerPoolTCPPort getLoadBalancerPoolTCPPort(@SQLParam("id") UUID id);
    
    @SQLGetter(table = LoadBalancerPoolTCPPort.class, name = "allocate_load_balancer_pool_tcp_ports", since = @SQLVersion({1, 0, 30}),
            query = @SQLQuery("WITH available AS (\n" + 
                    "    SELECT * FROM virt.load_balancer_pool_tcp_port\n" + 
                    "    WHERE pool_id = p_pool_id AND port = p_port AND (allocated IS NULL OR NOT allocated)\n" + 
                    "    LIMIT 1\n" + 
                    "    FOR UPDATE SKIP LOCKED\n" + 
                    ")\n" + 
                    "UPDATE virt.load_balancer_pool_tcp_port t\n" + 
                    "SET allocated = true \n" + 
                    "FROM available\n" + 
                    "WHERE t.id = available.id\n" + 
                    "RETURNING t.*"))
    public abstract LoadBalancerPoolTCPPort allocateLoadBalancerTCPPorts(@SQLParam("pool_id") UUID loadBalancerPoolId, @SQLParam("port") int port);
    
    @SQLGetter(table = LoadBalancerPoolTCPPort.class, name = "get_available_load_balancer_pool_tcp_ports", since = @SQLVersion({1, 0, 30}),
            query = @SQLQuery("SELECT * FROM virt.load_balancer_pool_tcp_port WHERE pool_id = p_pool_id AND port = p_port AND (allocated IS NULL OR NOT allocated)"))
    public abstract List<LoadBalancerPoolTCPPort> getAvailableLoadBalancerTCPPorts(@SQLParam("pool_id") UUID loadBalancerPoolId, @SQLParam("port") int port);
    
    @SQLGetter(table = LoadBalancerPoolTCPPort.class, name = "get_load_balancer_pool_tcp_ports_for_load_balancer_pool", since = @SQLVersion({1, 0, 21}))
    public abstract List<LoadBalancerPoolTCPPort> getLoadBalancerPoolTCPPortsForLoadBalancerPool(@SQLParam("pool_id") UUID loadBalancerPoolId);

    @SQLRemove(table = LoadBalancerPoolTCPPort.class, name = "remove_load_balancer_pool_tcp_port", since = @SQLVersion({1, 0, 21}))
    public abstract void removeLoadBalancerPoolTCPPort(@SQLParam("id") UUID id);
    
    @SQLGetter(table = LoadBalancerPoolTCPPort.class, name = "list_load_balancer_pool_tcp_ports", since = @SQLVersion({1, 0, 21}))
    public abstract List<LoadBalancerPoolTCPPort> listLoadBalancerPoolTCPPorts();
    
    
    @SQLSetter(table = LoadBalancer.class, name = "set_load_balancer", since = @SQLVersion({1, 0, 21}))
    public abstract void setLoadBalancer(LoadBalancer b);
    
    @SQLGetter(table = LoadBalancer.class, name = "get_load_balancer", since = @SQLVersion({1, 0, 21}))
    public abstract LoadBalancer getLoadBalancer(@SQLParam("id") UUID id);
    
    @SQLGetter(table = LoadBalancer.class, name = "get_load_balancers_for_account", since = @SQLVersion({1, 0, 21}))
    public abstract List<LoadBalancer> getLoadBalancersForAccount(@SQLParam("account_id") UUID accountId);
    
    @SQLGetter(table = LoadBalancer.class, name = "get_load_balancers_for_load_balancer_pool", since = @SQLVersion({1, 0, 24}))
    public abstract List<LoadBalancer> getLoadBalancersForLoadBalancerPool(@SQLParam("pool_id") UUID poolId);

    @SQLRemove(table = LoadBalancer.class, name = "remove_load_balancer", since = @SQLVersion({1, 0, 21}))
    public abstract void removeLoadBalancer(@SQLParam("id") UUID id);
    
    @SQLGetter(table = LoadBalancer.class, name = "list_load_balancers", since = @SQLVersion({1, 0, 21}))
    public abstract List<LoadBalancer> listLoadBalancers();
    
    
    @SQLSetter(table = LoadBalancerBackendServer.class, name = "set_load_balancer_backend_server", since = @SQLVersion({1, 0, 21}))
    public abstract void setLoadBalancerBackendServer(LoadBalancerBackendServer b);
    
    @SQLGetter(table = LoadBalancerBackendServer.class, name = "get_load_balancer_backend_server", since = @SQLVersion({1, 0, 21}))
    public abstract LoadBalancerBackendServer getLoadBalancerBackendServer(@SQLParam("load_balancer_id") UUID loadBalancerId, @SQLParam("machine_id") UUID machineId, @SQLParam("port") int port);
    
    @SQLGetter(table = LoadBalancerBackendServer.class, name = "get_load_balancer_backend_servers_for_load_balancer", since = @SQLVersion({1, 0, 21}))
    public abstract List<LoadBalancerBackendServer> getLoadBalancerBackendServersForLoadBalancer(@SQLParam("load_balancer_id") UUID loadBalancerId);

    @SQLRemove(table = LoadBalancerBackendServer.class, name = "remove_load_balancer_backend_server", since = @SQLVersion({1, 0, 21}))
    public abstract void removeLoadBalancerBackendServer(@SQLParam("load_balancer_id") UUID loadBalancerId, @SQLParam("machine_id") UUID machineId, @SQLParam("port") int port);
    
    @SQLGetter(table = LoadBalancerBackendServer.class, name = "list_load_balancer_backend_servers", since = @SQLVersion({1, 0, 21}))
    public abstract List<LoadBalancerBackendServer> listLoadBalancerBackendServers();
    
    @SQLGetter(table = Machine.class, name ="get_possible_backend_machines_for_load_balancer", since = @SQLVersion({1, 0, 22}), orderBy = @SQLOrder("name"),
            query = @SQLQuery("SELECT m.* FROM virt.machine m "
                    + "JOIN virt.machine_nic mn ON (m.id = mn.machine_id) "
                    + "JOIN virt.load_balancer_pool lbp ON (mn.network_id = lbp.network_id) "
                    + "JOIN virt.load_balancer lb ON (lb.pool_id = lbp.id AND m.account_id = lb.account_id) "
                    + "WHERE lb.id = p_load_balancer_id"))
    public abstract List<Machine> getPossibleBackendMachinesForLoadBalancer(@SQLParam(value = "load_balancer_id", virtual = true) UUID loadBalancerId);
    
    @SQLGetter(table = MachineNIC.class, name ="get_load_balancer_backend_server_machine_nic", since = @SQLVersion({1, 0, 25}), orderBy = @SQLOrder("ipv4"),
            query = @SQLQuery("SELECT mn.* FROM virt.machine_nic mn "
                    + "WHERE mn.machine_id = p_machine_id "
                    + "AND mn.network_id = ("
                    + " SELECT network_id "
                    + " FROM virt.load_balancer_pool lbp "
                    + " JOIN virt.load_balancer lb ON (lb.pool_id = lbp.id) "
                    + " WHERE lb.id = p_load_balancer_id"
                    + ")"))
    public abstract MachineNIC getLoadBalancerBackendServerMachineNic(@SQLParam(value = "load_balancer_id", virtual = true) UUID loadBalancerId, @SQLParam(value = "machine_id", virtual = true) UUID machineId);
    
    
    @SQLSetter(table = LoadBalancerBackendTarget.class, name = "set_load_balancer_backend_target", since = @SQLVersion({1, 0, 27}))
    public abstract void setLoadBalancerBackendTarget(LoadBalancerBackendTarget b);
    
    @SQLGetter(table = LoadBalancerBackendTarget.class, name = "get_load_balancer_backend_target", since = @SQLVersion({1, 0, 27}))
    public abstract LoadBalancerBackendTarget getLoadBalancerBackendTarget(@SQLParam("load_balancer_id") UUID loadBalancerId, @SQLParam("target") String target, @SQLParam("port") int port);
    
    @SQLGetter(table = LoadBalancerBackendTarget.class, name = "get_load_balancer_backend_targets_for_load_balancer", since = @SQLVersion({1, 0, 27}))
    public abstract List<LoadBalancerBackendTarget> getLoadBalancerBackendTargetsForLoadBalancer(@SQLParam("load_balancer_id") UUID loadBalancerId);

    @SQLRemove(table = LoadBalancerBackendTarget.class, name = "remove_load_balancer_backend_target", since = @SQLVersion({1, 0, 27}))
    public abstract void removeLoadBalancerBackendTarget(@SQLParam("load_balancer_id") UUID loadBalancerId, @SQLParam("target") String target, @SQLParam("port") int port);
    
    @SQLGetter(table = LoadBalancerBackendTarget.class, name = "list_load_balancer_backend_targets", since = @SQLVersion({1, 0, 27}))
    public abstract List<LoadBalancerBackendTarget> listLoadBalancerBackendTargets();
    
}
