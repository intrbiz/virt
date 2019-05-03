package Virt::NetD::FirewallManager;

use 5.0100;
use Moo;
use JSON;
use Log::Log4perl;

has 'logger' => (
	is  => 'rw',
	default => sub { Log::Log4perl->get_logger('Virt::NetD::FirewallManager') }
);

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
sub nft
{
    my ($self, $command) = @_;
    my $full_command = 'nft ' . $command . ' 2>/dev/null';
    my $out  = qx($full_command);
    my $exit = $?;
    $self->logger()->info("Executed $full_command => exit: $exit, stdout: '$out'");
    my $data = (length($out) > 0) ? from_json($out) : {};
    return {
        'exit' => $exit,
        'data' => $data
    };
}

sub boot
{
    my ($self) = @_;
    # Apply our firewall rules
    $self->nft("-f /etc/virt/netd/fw.nft");
}

1; 
