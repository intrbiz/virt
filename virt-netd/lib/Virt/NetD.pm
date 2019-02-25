package Virt::NetD;

our $VERSION = '1.0.0';

use 5.0100;
use Moo;
use Virt::NetD::LinkManager;
use Virt::NetD::LibvirtdHook;

has 'logger' => (
	is  => 'rw',
	default => sub { Log::Log4perl->get_logger('Virt::NetD') }
);

has 'link_manager' => (
	is  => 'rw'
);

sub BUILD
{
    my ($self) = @_;
    $self->link_manager(Virt::NetD::LinkManager->new());
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
            $self->link_manager()->create_virtual_network($log, $network_id);
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
            $self->link_manager()->release_virtual_network($log, $network_id);
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
        process_prepare($log, $hook);
    }
    elsif ($hook->operation() eq 'migrate')
    {
        process_migrate($log, $hook);
    }
    elsif ($hook->operation() eq 'release')
    {
        process_release($log, $hook);
    }
    $self->logger()->info("Finished hook");
}

1;
