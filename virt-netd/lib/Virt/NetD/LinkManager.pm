package Virt::NetD::LinkManager;

use 5.0100;
use Moo;
use JSON;
use Log::Log4perl;

has 'logger' => (
	is  => 'rw',
	default => sub { Log::Log4perl->get_logger('Virt::NetD::LinkManager') }
);

## TODO
has 'config' => (
    is  => 'rw'
);

sub BUILD
{
    my ($self, $opts) = @_;
    $self->config($opts->{'config'});
}


##
## Low-level execution handling for iproute2 `ip` command
##
sub ip
{
    my ($self, $command) = @_;
    my $full_command = 'ip -j -d ' . $command . ' 2>/dev/null';
    #print $log "Executing ${full_command}\n";
    my $out  = qx($full_command);
    my $exit = $?;
    $self->logger()->info("Executed $full_command => exit: $exit, stdout: '$out'");
    my $data = (length($out) > 0) ? from_json($out) : {};
    return {
        'exit' => $exit,
        'data' => $data
    };
}

##
## List links of this system
##
sub list_links
{
    my ($self, $type) = @_;
    my $command = 'link show';
    if (defined $type)
    {
        $command .= ' type ' . $type;
    }
    my $data = $self, $self->ip($command);
    return $data->{'data'};
}

##
## List the VXLAN tunnels of this system
##
sub list_vxlan_tunnels
{
    my ($self) = @_;
    return $self->list_links('vxlan');
}

##
## List the bridges of this system
##
sub list_bridges
{
    my ($self) = @_;
    return $self->list_links('bridge');
}

##
## Get a link from the given list of links with the given name and optionally of the given type
##
sub get_link
{
    my ($self, $name, $type) = @_;
    my $data = $self->list_links($type);
    foreach my $link (@{$data})
    {
        if ($link->{'ifname'} eq $name && ((! defined $type) || $link->{'linkinfo'}->{'info_kind'} eq $type))
        {
            return $link;
        }
    }
    return undef;
}

##
## Get all links from the given list of links which are in the given bridge
##
sub get_links_in_bridge
{
    my ($self, $bridge_name) = @_;
    my $data = $self->list_links();
    my $links = [];
    foreach my $link (@{$data})
    {
        if ($link->{'master'} eq $bridge_name)
        {
            push(@{$links}, $link);
        }
    }
    return $links;
}

##
## Get a bridge from the given list of links with the given name
##
sub get_bridge
{
    my ($self, $name) = @_;
    return $self->get_link($name, 'bridge');
}

##
## Does a bridge exists with in the given list of links with the given name
##
sub has_bridge
{
    my ($self, $name) = @_;
    return defined $self->get_bridge($name);
}

##
## Get a VXLAN tunnel from the given list of links with the given name
##
sub get_vxlan_tunnel
{
    my ($self, $name) = @_;
    return $self->get_link($name, 'vxlan');
}

##
## Does a VXLAN tunnel exist with in the given list of links with the given name
##
sub has_vxlan_tunnel
{
    my ($self, $data, $name) = @_;
    return defined $self->get_vxlan($data, $name);
}

##
## Construct the name for a bridge for the given network id
##
sub bridge_name
{
    my ($self, $network_id) = @_;
    return sprintf('br-%x', $network_id);
}

##
## Extract the network id from the given bridge name
##
sub from_bridge_name
{
    my ($self, $bridge_name) = @_;
    return hex(substr($bridge_name, 3));
}

##
## Construct the name for a VXLAN tunnel for the given network id
##
sub vxlan_name
{
    my ($self, $network_id) = @_;
    return sprintf('vx-%x', $network_id);
}

##
## Extract the network id from the given VXLAN tunnel name
##
sub from_vxlan_name
{
    my ($self, $vxlan_name) = @_;
    return hex(substr($vxlan_name, 3));
}

##
## Create a bridge for the given network id
##
sub create_bridge
{
    my ($self, $network_id) = @_;
    my $bridge_name = $self->bridge_name($network_id);
    my $link = $self->get_bridge($bridge_name);
    if (! defined $link)
    {
        $self->logger()->info("Creating bridge for network $network_id");
        $self->ip("link add name ${bridge_name} type bridge");
    }
    if (! (defined $link && $link->{'operstate'} eq 'UP'))
    {
        $self->logger()->info("Bringing up bridge for network $network_id");
        $self->ip("link set dev ${bridge_name} up");
    }
    return $bridge_name;
}

##
## Create a VXLAN tunnel for the given network id
##
sub create_vxlan_tunnel
{
    my ($self, $network_id) = @_;
    my $tunnel_name = $self->vxlan_name($network_id);
    my $link = $self->get_vxlan_tunnel($tunnel_name);
    if (! defined $link)
    {
        $self->logger()->info("Creating VXLAN tunnel for network $network_id");
        my $vxlan_group            = $self->config()->{'vxlan_group'};
        my $interconnect_interface = $self->config()->{'interconnect_interface'};
        my $vxlan_port             = $self->config()->{'vxlan_port'};
        $self->ip("link add ${tunnel_name} type vxlan id ${network_id} group ${vxlan_group} dev ${interconnect_interface} dstport ${vxlan_port}");
    }
    if (! (defined $link && $link->{'operstate'} eq 'UP'))
    {
        $self->logger()->info("Bringing up VXLAN tunnel for network $network_id");
        $self->ip("link set dev ${tunnel_name} up");
    }
    return $tunnel_name;
}

##
## Place the given interface into the given bridge
##
sub place_in_bridge
{
    my ($self, $bridge_name, $interface_name) = @_;
    my $link = $self->get_link($interface_name);
    if (! (defined $link && $link->{'master'} eq $bridge_name))
    {
        $self->logger()->info("Placing interface $interface_name into bridge $bridge_name");
        $self->ip("link set dev ${interface_name} master ${bridge_name}");
    }
}

##
## Remove the link of the given name
##
sub remove_link
{
    my ($self, $interface_name) = @_;
    $self->logger()->info("Removing link $interface_name");
    $self->ip("link del dev $interface_name");
}

##
## Create a virtual network, ensuring that a VXLAN tunnel and bridge is created for the given network id
##
sub create_virtual_network
{
    my ($self, $network_id) = @_;
    my $tunnel = $self->create_vxlan_tunnel($network_id);
    my $bridge = $self->create_bridge($network_id);
    $self->place_in_bridge($bridge, $tunnel);
    return $bridge;
}

##
## Release a virtual network, removing the VXLAN tunnel and bridge if the network is no longer in use
##
sub release_virtual_network
{
    my ($self, $network_id) = @_;
    my $tunnel_name = $self->vxlan_name($network_id);
    my $bridge_name = $self->bridge_name($network_id);
    my $links_in_bridge = $self->get_links_in_bridge($bridge_name);
    if (scalar(@{$links_in_bridge}) == 0 || (scalar(@{$links_in_bridge}) == 1 && $links_in_bridge->[0]->{'ifname'} eq $tunnel_name))
    {
        $self->logger()->info("No VMs connected to network $network_id, releasing network");
        $self->remove_link($bridge_name);
        $self->remove_link($tunnel_name);
    }
}

sub create_metadata_interface
{
    my ($self, $metadata_server_interface, $metadata_vms_interface, $metadata_server_address) = @_;
    $self->ip("link add name ${metadata_server_interface} type veth peer name ${metadata_vms_interface}");
    $self->ip("link set up dev ${metadata_server_interface}");
    $self->ip("link set up dev ${metadata_vms_interface}");
    $self->add_address($metadata_server_interface, $metadata_server_address);
}

sub add_address
{
    my ($self, $interface_name, $address) = @_;
    $self->ip("addr add dev $interface_name $address");
}

1; 
