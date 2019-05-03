package Virt::LBD::APIClient;

use 5.0100;
use Moo;
use Sys::Hostname;
use LWP::Simple;
use JSON;
use Digest::SHA qw(sha256_hex);

has 'config' => (
	is  => 'rw'
);

has 'json' => (
    is => 'rw',
    default => sub {
        my $json = JSON->new()->utf8();
        return $json;
    }
);

sub BUILD
{
    my ($self, $opts) = @_;
    $self->config($opts->{'config'});
}

sub get_load_balancer_config
{
    my ($self) = @_;
    my $api_host = $self->config()->api_host();
    my $lb_id = $self->config()->load_balancer_pool_id();
    my $node_name = hostname();
    my $url = 'http://' . $api_host . '/internal/balancer/pool/id/' . $lb_id . '/node/' . $node_name;
    # Fetch the LB Config
    my $content = get($url);
    if (defined $content)
    {
        my $config = $self->json()->decode($content);
        my $hash = sha256_hex($content);
        return {
            'config' => $config,
            'hash'   => $hash,
            'raw'    => $content
        };
    }
    return undef;
}

1; 
 
