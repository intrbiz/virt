package com.intrbiz.virt.data;

import java.util.List;
import java.util.UUID;

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
import com.intrbiz.virt.model.Account;
import com.intrbiz.virt.model.Config;
import com.intrbiz.virt.model.Image;
import com.intrbiz.virt.model.Machine;
import com.intrbiz.virt.model.MachineNIC;
import com.intrbiz.virt.model.MachineType;
import com.intrbiz.virt.model.MachineVolume;
import com.intrbiz.virt.model.Network;
import com.intrbiz.virt.model.PersistentVolume;
import com.intrbiz.virt.model.SSHKey;
import com.intrbiz.virt.model.User;
import com.intrbiz.virt.model.UserAccountGrant;
import com.intrbiz.virt.model.Zone;

@SQLSchema(
        name = "virt", 
        version = @SQLVersion({1, 0, 10}),
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
            PersistentVolume.class
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
    
    @SQLGetter(table = Network.class, name ="get_networks_for_account", since = @SQLVersion({1, 0, 0}), orderBy = {@SQLOrder("name"), @SQLOrder("zone_id")})
    public abstract List<Network> getNetworksForAccount(@SQLParam("account_id") UUID accountId);
    
    @SQLGetter(table = Network.class, name ="get_networks_for_account_in_zone", since = @SQLVersion({1, 0, 6}), orderBy = @SQLOrder("name"))
    public abstract List<Network> getNetworksForAccountInZone(@SQLParam("account_id") UUID accountId, @SQLParam("zone_id") UUID zoneId);
    
    @SQLGetter(table = Network.class, name = "list_networks", since = @SQLVersion({1, 0, 0}))
    public abstract List<Network> listNetworks();
    
    @SQLGetter(table = Network.class, name ="get_networks_in_zone", since = @SQLVersion({1, 0, 6}), orderBy = @SQLOrder("name"))
    public abstract List<Network> getNetworksInZone(@SQLParam("zone_id") UUID zoneId);
    
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
            query = @SQLQuery("SELECT pv.* FROM virt.persistent_volume pv WHERE pv.account_id = p_account_id AND pv.zone_id = p_zone_id")
    )
    public abstract List<PersistentVolume> getAvailablePersistentVolumesForAccountInZone(@SQLParam("account_id") UUID accountId, @SQLParam("zone_id") UUID zoneId);
    
    @SQLGetter(table = PersistentVolume.class, name = "list_persistent_volumes", since = @SQLVersion({1, 0, 6}))
    public abstract List<PersistentVolume> listPersistentVolumes();
    
    @SQLGetter(table = PersistentVolume.class, name ="get_persistent_volumes_in_zone", since = @SQLVersion({1, 0, 6}), orderBy = @SQLOrder("name"))
    public abstract List<PersistentVolume> getPersistentVolumesInZone(@SQLParam("zone_id") UUID zoneId);
}
