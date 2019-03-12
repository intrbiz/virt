package Virt::NetD::Config;

use 5.0100;
use Moo;
use XML::Simple;
use Data::Dump qw(dump);

has 'raw_config' => (
	is  => 'rw'
);

##
## Read our configuration file
##
sub BUILD
{
    my ($self) = @_;
    my $xs = XML::Simple->new();
    $self->raw_config($xs->XMLin('/etc/virt/netd.xml'));
}

sub vxlan_group
{
    my ($self) = @_;
    return $self->raw_config()->{'vxlan'}->{'group'};
}

sub vxlan_port
{
    my ($self) = @_;
    return $self->raw_config()->{'vxlan'}->{'port'};
}

sub interconnect_interface
{
    my ($self) = @_;
    return $self->raw_config()->{'interconnect'}->{'interface'};
}

sub metadata_interface_prefix
{
    my ($self) = @_;
    return $self->raw_config()->{'metadata'}->{'interface_prefix'};
}

sub metadata_server_address
{
    my ($self) = @_;
    return $self->raw_config()->{'metadata'}->{'address'};
}

sub metadata_server_interface
{
    my ($self) = @_;
    return $self->metadata_interface_prefix() . '_srv';
}

sub metadata_vms_interface
{
    my ($self) = @_;
    return $self->metadata_interface_prefix() . '_vms';
}

sub networks
{
    my ($self) = @_;
    my @nets = ();
    foreach my $name (keys %{$self->raw_config()->{'network'}})
    {
        push(@nets, {
            'name'    => $name,
            'id'      => $self->raw_config()->{'network'}->{$name}->{'id'},
            'address' => $self->raw_config()->{'network'}->{$name}->{'address'}
        });
    }
    return \@nets;
}


1;
