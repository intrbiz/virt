package Virt::LBD::Config;

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
    $self->raw_config($xs->XMLin('/etc/virt/lbd.xml'));
}

sub api_host
{
    my ($self) = @_;
    return $self->raw_config()->{'api'}->{'host'};
}

sub load_balancer_pool_id
{
    my ($self) = @_;
    return $self->raw_config()->{'load-balancer-pool'}->{'id'};
}

1;
