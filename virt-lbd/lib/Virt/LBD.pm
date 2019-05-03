package Virt::LBD;

our $VERSION = '1.0.0';

=head1 NAME

Virt LBD - Load Balancer management daemon

=head1 DESCRIPTION

	Virt LBD manages a load balancer node

=over

=cut

use 5.0100;
use Moo;
use Data::Dump qw(dump);
use Log::Log4perl;
use Virt::LBD::Config;
use Virt::LBD::APIClient;
use Virt::LBD::HAProxyGenerator;

has 'logger' => (
	is  => 'rw',
	default => sub { Log::Log4perl->get_logger('Virt::LBD') }
);

has 'config' => (
	is  => 'rw'
);

sub BUILD
{
    my ($self) = @_;
    # Setup logging
    my $logging_conf = q(
log4perl.rootLogger=INFO, LOGFILE
log4perl.appender.LOGFILE=Log::Log4perl::Appender::File
log4perl.appender.LOGFILE.filename=/var/log/virt/lbd.log
log4perl.appender.LOGFILE.mode=append
log4perl.appender.LOGFILE.layout=PatternLayout
log4perl.appender.LOGFILE.layout.ConversionPattern=[%r] %F %L %c - %m%n
);
    Log::Log4perl::init( \$logging_conf );
    # Load our configuration
    $self->config(Virt::LBD::Config->new());
}

sub systemctl
{
    my ($self, $command) = @_;
    my $full_command = '/usr/bin/systemctl ' . $command . ' 2>/dev/null';
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
## Low-level execution handling for nftables `nft` command
##
sub nft
{
    my ($self, $command) = @_;
    my $full_command = '/usr/sbin/nft ' . $command . ' 2>/dev/null';
    my $out  = qx($full_command);
    my $exit = $?;
    $self->logger()->info("Executed $full_command => exit: $exit, stdout: '$out'");
    my $data = (length($out) > 0) ? from_json($out) : {};
    return {
        'exit' => $exit,
        'data' => $data
    };
}


sub cli_boot
{
    my ($self) = @_;
    # Apply our firewall rules
    $self->nft("-f /etc/virt/lbd/fw.nft");
    # Fetch our configuration
    my $client = Virt::LBD::APIClient->new({ 'config' => $self->config() });
    my $lb_config = $client->get_load_balancer_config();
    if (defined $lb_config)
    {
        $self->logger()->info("Got load balancer config hash=" . $lb_config->{'hash'});
        $self->logger()->debug(dump($lb_config->{'config'}));
        # Write the HAProxy configuration
        my $jenny = Virt::LBD::HAProxyGenerator->new({ 'config' => $self->config });
        my $config_version = $jenny->write_config($lb_config->{'config'});
    }
    else
    {
        $self->logger()->info("Failed to get load balancer config");
    }
}

sub cli_update
{
    my ($self) = @_;
    # Fetch our configuration
    my $client = Virt::LBD::APIClient->new({ 'config' => $self->config() });
    my $lb_config = $client->get_load_balancer_config();
    if (defined $lb_config)
    {
        $self->logger()->info("Got load balancer config hash=" . $lb_config->{'hash'});
        $self->logger()->debug(dump($lb_config->{'config'}));
        # Write the HAProxy configuration
        my $jenny = Virt::LBD::HAProxyGenerator->new({ 'config' => $self->config });
        my $config_version = $jenny->write_config($lb_config->{'config'});
        # Reload the configuration
        $self->logger()->info("Reloading HAProxy");
        $self->systemctl("reload haproxy");
    }
    else
    {
        $self->logger()->info("Failed to get load balancer config");
    }
}

sub cli_daemon
{
    my ($self) = @_;
    # Main configuration processing loop
    my $last_hash = '';
    while (1)
    {
        sleep(int(rand(10)) + 110);
        # Fetch our configuration
        my $client = Virt::LBD::APIClient->new({ 'config' => $self->config() });
        my $lb_config = $client->get_load_balancer_config();
        if (defined $lb_config)
        {
            $self->logger()->info("Got load balancer config hash=" . $lb_config->{'hash'});
            if ($last_hash ne $lb_config->{'hash'})
            {
                $self->logger()->info("Load balancer configuration changed, generating new HAProxy configuration");
                $self->logger()->debug(dump($lb_config->{'config'}));
                # Write the HAProxy configuration
                my $jenny = Virt::LBD::HAProxyGenerator->new({ 'config' => $self->config });
                my $config_version = $jenny->write_config($lb_config->{'config'});
                # Reload the configuration
                $self->logger()->info("Reloading HAProxy");
                $self->systemctl("reload haproxy");
                # Keep track of last applied configuration hash
                $last_hash = $lb_config->{'hash'};
            }
        }
        else
        {
            $self->logger()->info("Failed to get load balancer config");
        }
    }
}

##
## Show the Virt::LBD configuration
##
sub cli_show_config
{
    my ($self) = @_;
    print "Virt::LBD Config\n";
    print "  API Host: ", $self->config()->api_host(), "\n";
    print "  Load Balancer Pool ID: ", $self->config()->load_balancer_pool_id(), "\n";
}

sub cli_help
{
    my ($self) = @_;
    print "Virt::LBD\n";
    print "$0: Unknown command '", dump(@ARGV), "'.  The following commands are supported\n";
    print "Commands:\n";
    print "    boot                                      - Update the load balancer configuration on boot\n";
    print "    update                                    - Update the load balancer configuration\n";
    print "    daemon                                    - Manage the load balancer configuration\n";
    print "    config                                    - Show Virt::LBD config\n";
}

sub cli
{
    my ($self) = @_;
    # Are we being invoked as a CLI
    if ($0 =~ /virt-lbd$/ || $0 =~ /virt-lbd.pl$/)
    {
        my $command = $ARGV[0];
        if (defined $command)
        {
            if ($command eq 'boot')
            {
                $self->cli_boot();
            }
            elsif ($command eq 'update')
            {
                $self->cli_update();
            }
            elsif ($command eq 'daemon')
            {
                $self->cli_daemon();
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
}

1;
