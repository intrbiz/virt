package Virt::NetD;

our $VERSION = '1.0.0';

=head1 NAME

Virt NetD - Virtual Machine Networking

=head1 DESCRIPTION

	Virt NetD is a simple framework for managing VM tenant networking.
	
	It is defined to be called as both a CLI and as a hook from libvirtd.

=over

=cut

use 5.0100;
use Moo;
use Data::Dump qw(dump);
use Log::Log4perl;
use Virt::NetD::Config;
use Virt::NetD::LinkManager;
use Virt::NetD::LibvirtdHook;

has 'logger' => (
	is  => 'rw',
	default => sub { Log::Log4perl->get_logger('Virt::NetD') }
);

has 'config' => (
	is  => 'rw'
);

has 'link_manager' => (
	is  => 'rw'
);

sub BUILD
{
    my ($self) = @_;
    # Setup logging
    my $logging_conf = q(
log4perl.rootLogger=INFO, LOGFILE
log4perl.appender.LOGFILE=Log::Log4perl::Appender::File
log4perl.appender.LOGFILE.filename=/var/log/virt/netd.log
log4perl.appender.LOGFILE.mode=append
log4perl.appender.LOGFILE.layout=PatternLayout
log4perl.appender.LOGFILE.layout.ConversionPattern=[%r] %F %L %c - %m%n
);
    Log::Log4perl::init( \$logging_conf );
    # Load our configuration
    $self->config(Virt::NetD::Config->new());
    $self->link_manager(Virt::NetD::LinkManager->new({ 'config' => $self->config() }));
}

###
### Libvirtd Hook Processing
###

##
## Process the libvirtd qemu prepare hook
##
sub process_prepare
{
    my ($self, $hook) = @_;
    # Create all bridges
    my $ifindex = 0;
    foreach my $interface (@{$hook->domain()->{'devices'}->{'interface'}})
    {
        $self->logger()->info("Preparing interface: eth$ifindex");
        if ($interface->{'type'} eq 'bridge')
        {
            my $network_id  = $self->link_manager()->from_bridge_name($interface->{'source'}->{'bridge'});
            $self->logger()->info("Preparing network $network_id");
            $self->link_manager()->create_virtual_network($network_id);
        }
        $ifindex++;
    }
}

##
## Process the libvirtd qemu migrate hook
##
sub process_migrate
{
    my ($self, $hook) = @_;
    ## Prepare the resources needed
    $self->process_prepare($hook);
}

##
## Process the libvirtd qemu release hook
##
sub process_release
{
    my ($self, $hook) = @_;
    # Create all bridges
    my $ifindex = 0;
    foreach my $interface (@{$hook->domain()->{'devices'}->{'interface'}})
    {
        $self->logger()->info("Releasing interface: eth$ifindex");
        if ($interface->{'type'} eq 'bridge')
        {
            my $network_id  = $self->link_manager()->from_bridge_name($interface->{'source'}->{'bridge'});
            $self->logger()->info("Releasing network $network_id");
            $self->link_manager()->release_virtual_network($network_id);
        }
        $ifindex++;
    }
}

##
## Process a libvirtd qemu hook
##
sub process_hook
{
    my ($self, $hook) = @_;
    $self->logger()->info("Processing hook " . $hook->operation() . " " . $hook->uuid());
    if ($hook->operation() eq 'prepare')
    {
        $self->process_prepare($hook);
    }
    elsif ($hook->operation() eq 'migrate')
    {
        $self->process_migrate($hook);
    }
    elsif ($hook->operation() eq 'release')
    {
        $self->process_release($hook);
    }
    $self->logger()->info("Finished hook");
}


##
## Virt::NetD start up 
##
sub cli_boot
{
    my ($self) = @_;
    # Create the metadata bridge
    $self->logger()->info("Creating metadata interface");
    $self->link_manager()->create_metadata_interface($self->config()->metadata_server_interface(), $self->config()->metadata_vms_interface(), $self->config()->metadata_server_address());
    print "Created metadata interface\n";
    # Create the default networks
    foreach my $net (@{$self->config()->networks()})
    {
        $self->logger()->info("Created network " . $net->{'name'} . " id " . $net->{'id'});
        my $bridge = $self->link_manager()->create_virtual_network($net->{'id'});
        print "Created network ", $net->{'name'}, " id ", $net->{'id'}, "\n";
        if (defined $net->{'address'})
        {
            $self->logger()->info("  Setting address " . $net->{'address'} . " of " . $bridge);
            $self->link_manager()->add_address($bridge, $net->{'address'});
            print "  Setting address ", $net->{'address'}, " of ", $bridge, "\n";
        }
    }
}

##
## Show the Virt::NetD configuration
##
sub cli_show_config
{
    my ($self) = @_;
    print "Virt::NetD Config\n";
    print "  VXLAN Group Address: ", $self->config()->vxlan_group(), "\n";
    print "  VXLAN Port: ", $self->config()->vxlan_port(), "\n";
    print "  Interconnect Interface: ", $self->config()->interconnect_interface(), "\n";
    print "  Metadata Interface Prefix: ", $self->config()->metadata_interface_prefix(), "\n";
    print "  Metadata Server Interface: ", $self->config()->metadata_server_interface(), "\n";
    print "  Metadata Server Address: ", $self->config()->metadata_server_address(), "\n";
    print "  Metadata VMs Interface: ", $self->config()->metadata_vms_interface(), "\n";
    print "  Networks:\n";
    foreach my $net (@{$self->config()->networks()})
    {
        print "    Network ", $net->{'name'}, " id ", $net->{'id'};
        if (defined $net->{'address'})
        {
            print " address ", $net->{'address'};
        }
        print "\n";
    }
}

sub cli_create
{
    my ($self, $network_id) = @_;
    $self->logger()->info("Creating network $network_id");
    $self->link_manager()->create_virtual_network($network_id);
    print "Virt::NetD\n";
    print "Created network $network_id\n";
}

sub cli_connect
{
    my ($self, $network_id, $vm_interface_name) = @_;
    $self->logger()->info("Creating network $network_id");
    my $bridge = $self->link_manager()->create_virtual_network($network_id);
    $self->link_manager()->place_in_bridge($bridge, $vm_interface_name);
    print "Virt::NetD\n";
    print "Connected $vm_interface_name to $network_id\n";
}

sub cli_help
{
    my ($self) = @_;
    print "Virt::NetD\n";
    print "$0: Unknown command '", dump(@ARGV), "'.  The following commands are supported\n";
    print "Commands:\n";
    print "    boot                                      - Startup Virt::NetD, setup the metadata interfaces and create the predefined networks\n";
    print "    create <network_id>                       - Create a virtual network\n";
    print "    connect <network_id> <vm_interface_name>  - Connect a virtual machine interface to a virtual network\n";
    print "    config                                    - Show Virt::NetD config\n";
}

sub cli
{
    my ($self) = @_;
    # Are we being invoked as a CLI or as a qemu hook
    if ($0 =~ /virt-netd$/ || $0 =~ /virt-netd.pl$/)
    {
        my $command = $ARGV[0];
        if (defined $command)
        {
            if ($command eq 'boot')
            {
                $self->cli_boot();
            }
            elsif ($command eq 'create')
            {
                my $network_id = $ARGV[1];
                $self->cli_create($network_id);
            }
            elsif ($command eq 'connect')
            {
                my $network_id = $ARGV[1];
                my $vm_interface_name = $ARGV[2];
                $self->cli_connect($network_id, $vm_interface_name);
            }
            elsif ($command eq 'config')
            {
                $self->cli_show_config();
            }
            else
            {
                $self->cli_help();
            }
        }
        else
        {
            $self->cli_help();
        }
    }
    else
    {
        $self->process_hook(Virt::NetD::LibvirtdHook->new());
    }
}

1;
