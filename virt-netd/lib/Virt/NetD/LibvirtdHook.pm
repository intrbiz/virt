package Virt::NetD::LibvirtdHook;

use 5.0100;
use Moo;
use XML::Simple;

has 'operation' => (
	is  => 'rw'
);

has 'sub_operation' => (
	is  => 'rw'
);

has 'extra' => (
	is  => 'rw'
);

has 'object' => (
	is  => 'rw'
);

has 'domain' => (
	is  => 'rw'
);

##
## Read the hook event data for this process
##
sub BUILD
{
    my ($self) = @_;
    # Read hook command line
    $self->object(@ARGV[0]);
    $self->operation(@ARGV[1]);
    $self->sub_operation(@ARGV[2]);
    $self->extra(@ARGV[3]);
    # Read the domain XML from std in
    my $xs = XML::Simple->new();
    $self->domain($xs->XMLin('-'));
}

sub uuid
{
    my ($self) = @_;
    return $self->domain()->{'uuid'};
}

1; 
 
