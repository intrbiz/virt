#!/usr/bin/perl
use 5.010;

$vxlan_group = '239.1.1.1';
$vxlan_port  = '4789';
$interconnect_interface = 'enp0s31f6';

sub ip
{
    my ($command) = @_;
    my $full_command = 'ip ' . $command;
    print "Executing ${full_command}\n";
    qx($full_command);
}

sub bridge_name
{
    my ($network_id) = @_;
    return sprintf('br-%x', $network_id);
}

sub vxlan_name
{
    my ($network_id) = @_;
    return sprintf('vx-%x', $network_id);
}

sub create_bridge
{
    my ($network_id) = @_;
    
    my $bridge_name = bridge_name($network_id);
    
    ip("link add name ${bridge_name} type bridge");
    ip("link set dev ${bridge_name} up");
    
    return $bridge_name;
}

sub create_vxlan_tunnel
{
    my ($network_id) = @_;
    
    my $tunnel_name = vxlan_name($network_id);
    
    ip("link add ${tunnel_name} type vxlan id ${network_id} group ${vxlan_group} dev ${interconnect_interface} dstport ${vxlan_port}");
    ip("link set dev ${tunnel_name} up");
    
    return $tunnel_name;
}

sub place_in_bridge
{
    my ($bridge_name, $interface_name) = @_;
    
    ip("link set dev ${interface_name} master ${bridge_name}");
}

sub create_virtual_network
{
    my ($network_id) = @_;
    
    my $tunnel = create_vxlan_tunnel($network_id);
    
    my $bridge = create_bridge($network_id);
    
    place_in_bridge($bridge, $tunnel);
    
    return $bridge;
}

sub connect_vm_to_virtual_network
{
    my ($network_id, $vm_interface_name) = @_;
    
    my $bridge = create_virtual_network($network_id);
    
    place_in_bridge($bridge, $vm_interface_name);
}




## Command Parsing

print "Virt Net\n";

my $command = shift(@ARGV);

if ($command eq 'create')
{
    my $network_id = shift(@ARGV);
    create_virtual_network($network_id);
}
elsif ($command eq 'connect')
{
    my $network_id = shift(@ARGV);
    my $vm_interface_name = shift(@ARGV);
    connect_vm_to_virtual_network($network_id, $vm_interface_name);
}
else
{
    print "$0: Unknown command '${command}'.  The following commands are supported\n";
    print "Commands:\n";
    print "    create <network_id>\n";
    print "    connect <network_id> <vm_interface_name>  - Connect a virtual machine interface of the given virtual network\n";
}

