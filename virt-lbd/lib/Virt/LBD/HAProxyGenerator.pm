package Virt::LBD::HAProxyGenerator;

use 5.0100;
use Moo;
use Data::Dump qw(dump);

has 'logger' => (
	is  => 'rw',
	default => sub { Log::Log4perl->get_logger('Virt::LBD::HAProxyGenerator') }
);

has 'config' => (
	is  => 'rw'
);

sub BUILD
{
    my ($self, $opts) = @_;
    $self->config($opts->{'config'});
}

sub write_config
{
    my ($self, $lb_config) = @_;
    # Create the config directories
    my $config_base_dir = '/etc/haproxy/';
    my $config_certs_dir = $config_base_dir . 'certs';
    my $config_file = $config_base_dir . 'haproxy.cfg';
    my $https_backend_map_file = $config_base_dir . 'https_backend.map';
    my $http_backend_map_file = $config_base_dir . 'http_backend.map';
    mkdir $config_base_dir;
    mkdir $config_certs_dir;
    $self->set_file_permissions($config_certs_dir, 0755);
    # Write out the certificates
    $self->write_certificates($lb_config, $config_certs_dir);
    # Write out the configuration
    $self->write_haproxy_config($lb_config, $config_file);
    # Write out maps
    $self->write_https_backend_map($lb_config, $https_backend_map_file);
    $self->write_http_backend_map($lb_config, $http_backend_map_file);
    return 1;
}

sub write_certificates
{
    my ($self, $lb_config, $certs_dir) = @_;
    foreach my $lb (@{$lb_config->{'loadBalancers'}})
    {
        if ($lb->{'mode'} eq 'https')
        {
            my $lb_id = $lb->{'id'};
            $self->write_certificate_file("${certs_dir}/50_${lb_id}_custom.pem", $self->get_acme_certificate($lb->{'certificate'}));
            $self->write_certificate_file("${certs_dir}/60_${lb_id}_generated.pem", $self->get_acme_certificate($lb->{'generated_certificate'}));
        }
    }
}

sub write_certificate_file
{
    my ($self, $cert_file, $pem) = @_;
    if (defined $pem)
    {
        $self->logger()->info("Writing certificate $cert_file");
        $self->write_file($cert_file, $pem, 0640);
    }
}

sub get_acme_certificate
{
    my ($self, $cert) = @_;
    if (defined $cert)
    {
        if (defined $cert->{'key_pair'} && defined $cert->{'certificate_bundle'})
        {
            my $pem = '';
            $pem .= $cert->{'certificate_bundle'};
            $pem .= "\n";
            $pem .= $cert->{'key_pair'};
            $pem .= "\n";
            return $pem;
        }
    }
    return undef;
}

sub write_haproxy_config
{
    my ($self, $lb_config, $config_file) = @_;
    my $haproxy_config = $self->generate_haproxy_config($lb_config);
    $self->write_file($config_file, $haproxy_config, 0644);
}

sub write_https_backend_map
{
    my ($self, $lb_config, $file) = @_;
    my $map = $self->generate_haproxy_https_backend_map($lb_config);
    $self->write_file($file, $map, 0644);
}

sub write_http_backend_map
{
    my ($self, $lb_config, $file) = @_;
    my $map = $self->generate_haproxy_http_backend_map($lb_config);
    $self->write_file($file, $map, 0644);
}

sub generate_haproxy_config
{
    my ($self, $lb_config) = @_;
    my $haproxy_config = '';
    # Gloabl configuration
    $haproxy_config .= $self->generate_haproxy_global_config($lb_config);
    $haproxy_config .= "\n";
    # Defaults
    $haproxy_config .= $self->generate_haproxy_defaults_config($lb_config);
    $haproxy_config .= "\n";
    # Stats handler
    $haproxy_config .= $self->generate_haproxy_stats_config($lb_config);
    $haproxy_config .= "\n";
    # HTTPS Frontend
    $haproxy_config .= $self->generate_haproxy_https_frontend_config($lb_config);
    $haproxy_config .= "\n";
    # HTTP Frontend
    $haproxy_config .= $self->generate_haproxy_http_frontend_config($lb_config);
    $haproxy_config .= "\n";
    # TCP Frontends
    $haproxy_config .= $self->generate_haproxy_tcp_frontends_config($lb_config);
    $haproxy_config .= "\n";
    # TODO: TCP Frontends
    # Backends
    $haproxy_config .= $self->generate_haproxy_backends_config($lb_config);
    $haproxy_config .= "\n";
    #
    return $haproxy_config;
}

sub generate_haproxy_global_config
{
    my ($self, $lb_config) = @_;
    my $config = << "EOF";
global
  log /dev/log daemon
  maxconn 32768
  chroot /var/lib/haproxy
  user haproxy
  group haproxy
  daemon
  stats socket /var/lib/haproxy/stats user haproxy group haproxy mode 0640 level operator
  tune.bufsize 32768
  tune.ssl.default-dh-param 2048
  ssl-default-bind-ciphers ALL:!aNULL:!eNULL:!EXPORT:!DES:!3DES:!MD5:!PSK:!RC4:!ADH:!LOW\@STRENGTH
  nbproc 1
  nbthread 4
  cpu-map auto:1/1-4 0-3

EOF
    return $config;
}

sub generate_haproxy_defaults_config
{
    my ($self, $lb_config) = @_;
    my $config = << "EOF";
defaults
  log     global
  mode    http
  option  log-health-checks
  option  log-separate-errors
  option  dontlog-normal
  option  dontlognull
  option  httplog
  option  socket-stats
  option tcp-smart-accept
  option splice-auto
  retries 3
  option  redispatch
  maxconn 10000
  timeout connect     5s
  timeout client     50s
  timeout client-fin 30s
  timeout server    900s
  timeout server-fin 30s
  timeout tunnel  1h

EOF
    return $config;
}

sub generate_haproxy_stats_config
{
    my ($self, $lb_config) = @_;
    my $config = << "EOF";
listen stats
  bind 0.0.0.0:79
  stats enable
  stats uri     /
  stats refresh 5s
  stats auth admin:8imXzxfvF2WgOw8L54bIxi5y8xSGYDsGmets2N8L

EOF
    return $config;
}

sub generate_haproxy_https_frontend_config
{
    my ($self, $lb_config) = @_;
    my $config = << "EOF";
frontend https
  mode http
  bind 0.0.0.0:443 ssl crt /etc/haproxy/certs/
  
  # letsencrypt HTTP auth handling
  acl letsencrypt-acl path_beg /.well-known/acme-challenge/
  use_backend acme_wellknown if letsencrypt-acl

  use_backend %[req.hdr(host),lower,map_dom(/etc/haproxy/https_backend.map,drop)]
  
  default_backend drop_http

EOF
    return $config;
}

sub generate_haproxy_http_frontend_config
{
    my ($self, $lb_config) = @_;
    my $config = << "EOF";
frontend http
  mode http
  bind 0.0.0.0:80
  
  # letsencrypt HTTP auth handling
  acl letsencrypt-acl path_beg /.well-known/acme-challenge/
  use_backend acme_wellknown if letsencrypt-acl
  
  use_backend %[req.hdr(host),lower,map_dom(/etc/haproxy/http_backend.map,drop)]

  default_backend drop_http

EOF
    return $config;
}

sub generate_haproxy_tcp_frontends_config
{
    my ($self, $lb_config) = @_;
    # Group TCP frontends by port
    my %tcps = ();
    foreach my $lb (@{$lb_config->{'loadBalancers'}})
    {
        if ($lb->{'mode'} eq 'tcp')
        {
            my $port = $lb->{'tcp_port'}->{'port'};
            if (exists $tcps{$port})
            {
                push(@{$tcps{$port}}, $lb);
            }
            else
            {
                $tcps{$port} = [ $lb ];
            }
        }
    }
    # Generate the TCP frontends for each port
    my $config = '';
    foreach my $port (keys %tcps)
    {
        my $lbs = $tcps{$port};
        # Build the front end config
        $config .= << "EOF";
frontend tcp_${port}
  mode tcp
  bind 0.0.0.0:${port}
  
  option tcpka
  option tcp-smart-accept

  default_backend drop_tcp

EOF
        # Backend mapping rules
        foreach my $lb (@{$lbs})
        {
            my $backend_name = $self->backend_name($lb);
            my $tcp_bind = $lb->{'tcp_port'}->{'bind'};
            my $acl_name = 'acl_' . $self->server_name($tcp_bind);
            $config .= << "EOF";
  acl ${acl_name} dst ${tcp_bind}
  use_backend ${backend_name} if ${acl_name}

EOF
        }
    }
    return $config;
}

sub generate_haproxy_https_backend_map
{
    my ($self, $lb_config, $mode) = @_;
    my $map = '';
    foreach my $lb (@{$lb_config->{'loadBalancers'}})
    {
        if ($lb->{'mode'} eq 'https')
        {
            my $backend_name = $self->backend_name($lb);
            $map .= '#  LB ' . $lb->{'id'} . "\n";
            foreach my $domain (@{ $self->get_lb_domains($lb) })
            {
                $map .= $self->generate_backend_map_entry($domain, $backend_name);
            }
        }
    }
    return $map;
}

sub generate_haproxy_http_backend_map
{
    my ($self, $lb_config, $mode) = @_;
    my $map = '';
    # HTTPS redirects
    foreach my $lb (@{$lb_config->{'loadBalancers'}})
    {
        if ($lb->{'mode'} eq 'https' && $lb->{'redirect_http'})
        {
            my $backend_name = $self->backend_name($lb);
            $map .= '#  LB ' . $lb->{'id'} . "\n";
            foreach my $domain (@{ $self->get_lb_domains($lb) })
            {
                $map .= $self->generate_backend_map_entry($domain, $backend_name);
            }
        }
    }
    foreach my $lb (@{$lb_config->{'loadBalancers'}})
    {
        if ($lb->{'mode'} eq 'http')
        {
            my $backend_name = $self->backend_name($lb);
            $map .= '#  LB ' . $lb->{'id'} . "\n";
            foreach my $domain (@{ $self->get_lb_domains($lb) })
            {
                $map .= $self->generate_backend_map_entry($domain, $backend_name);
            }
        }
    }
    return $map;
}

sub generate_backend_map_entry
{
    my ($self, $domain, $backend_name) = @_;
    if ($domain =~ /^[*][.]/)
    {
        my $domain_suffix = $domain =~ s/^[*][.]//r;
        return "$domain_suffix $backend_name\n";
    }
    return "$domain $backend_name\n";
}

sub get_lb_domains
{
    my ($self, $lb) = @_;
    my @domains = ();
    foreach my $domain (@{ $lb->{'domains'} })
    {
        push @domains, $domain;
    }
    if (defined $lb->{'certificate'})
    {
        foreach my $domain (@{ $lb->{'certificate'}->{'domains'} })
        {
            push @domains, $domain;
        }
    }
    if (defined $lb->{'generated_certificate'})
    {
        foreach my $domain (@{ $lb->{'generated_certificate'}->{'domains'} })
        {
            push @domains, $domain;
        }
    }
    return \@domains;
}

sub generate_haproxy_backends_config
{
    my ($self, $lb_config) = @_;
    my $config = '';
    foreach my $lb (@{$lb_config->{'loadBalancers'}})
    {
        if ($lb->{'mode'} eq 'https' || $lb->{'mode'} eq 'http')
        {
            $config .= $self->generate_haproxy_http_backend_config($lb);
            $config .= "\n";
        }
        elsif ($lb->{'mode'} eq 'tcp')
        {
            $config .= $self->generate_haproxy_tcp_backend_config($lb);
            $config .= "\n";
        }
    }
    $config .= $self->generate_haproxy_drop_backend_config();
    $config .= "\n";
    $config .= $self->generate_haproxy_acme_backend_config();
    $config .= "\n";
    return $config;
}

sub generate_haproxy_drop_backend_config
{
    my ($self) = @_;
    my $config = << "EOF";
backend drop_http
  mode http
  http-request reject

backend drop_tcp
  mode tcp
  tcp-request content reject
EOF
    return $config;
}

sub generate_haproxy_acme_backend_config
{
    my ($self) = @_;
    my $api_host = $self->config()->api_host();
    my $server_name = $self->server_name($api_host);
    my $config = << "EOF";
backend acme_wellknown
  mode http
  http-request set-path /internal/acme/%[hdr(host)]/%[path]
  http-request replace-header Host (.*) ${api_host}
  option httpchk GET /health/alive
  http-check expect rstatus 200
  default-server inter 30s fall 2 rise 3
  server ${server_name} ${api_host} check
EOF
    return $config;
}

sub generate_haproxy_http_backend_config
{
    my ($self, $lb) = @_;
    # Vars we need
    my $backend_name = $self->backend_name($lb);
    my $health_check_path = $lb->{'health_check_path'};
    my $health_check_status = $self->build_haproxy_health_check_http_status_regex($lb->{'health_check_status'});
    my $health_check_interval = $lb->{'health_check_interval'};
    my $health_check_fall = $lb->{'health_check_fall'};
    my $health_check_rise = $lb->{'health_check_rise'};
    my $health_check_timeout = $lb->{'health_check_timeout'};
    # Base backend config
    my $config = << "EOF";
backend ${backend_name}
  mode http
  balance roundrobin
  cookie SRV insert indirect httponly postonly maxidle 2h maxlife 8h
  option forwardfor
  option allbackups
  http-request add-header x-forwarded-proto http if !{ ssl_fc }
  http-request add-header x-forwarded-proto https if { ssl_fc }
  option httpchk GET ${health_check_path}
  http-check expect rstatus ${health_check_status}
  default-server inter ${health_check_interval}s fall ${health_check_fall} rise ${health_check_rise}
EOF
    # Optional HTTP redirection rules
    if ($lb->{'mode'} eq 'https' && $lb->{'redirect_http'})
    {
        $config .= "  redirect scheme https if !{ ssl_fc }\n";
    }
    # Backends
    foreach my $server (@{$lb->{'backend_servers'}})
    {
        if (defined $server->{'machine_nic'})
        {
            my $machine_id = $server->{'machine_id'};
            my $ip = $server->{'machine_nic'}->{'ipv4'};
            my $port = $server->{'port'};
            my $options = $self->generate_backend_options($server);
            my $cookie = $self->backend_cookie($server);
            $config .= "  server srv-${machine_id}-${port} ${ip}:${port} check cookie ${cookie} ${options}\n";
        }
    }
    foreach my $target (@{$lb->{'backend_targets'}})
    {
        my $name = $self->server_name($target->{'target'});
        my $ip = $self->safe_target($target->{'target'});
        my $port = $target->{'port'};
        my $options = $self->generate_backend_options($target);
        my $cookie = $self->backend_cookie($target);
        $config .= "  server trg-${name}-${port} ${ip}:${port} check cookie ${cookie} ${options}\n";
    }
    return $config;
}

sub generate_haproxy_tcp_backend_config
{
    my ($self, $lb) = @_;
    # Vars we need
    my $backend_name = $self->backend_name($lb);
    my $health_check_mode = $self->build_haproxy_health_check_mode($lb->{'health_check_mode'});
    my $health_check_interval = $lb->{'health_check_interval'};
    my $health_check_fall = $lb->{'health_check_fall'};
    my $health_check_rise = $lb->{'health_check_rise'};
    my $health_check_timeout = $lb->{'health_check_timeout'};
    # Base backend config
    my $config = << "EOF";
backend ${backend_name}
  mode tcp
  balance leastconn
  option tcpka
  option tcp-smart-connect
  option allbackups
  option ${health_check_mode}
  default-server inter ${health_check_interval}s fall ${health_check_fall} rise ${health_check_rise}
EOF
    # Backends
    foreach my $server (@{$lb->{'backend_servers'}})
    {
        if (defined $server->{'machine_nic'})
        {
            my $machine_id = $server->{'machine_id'};
            my $ip = $server->{'machine_nic'}->{'ipv4'};
            my $port = $server->{'port'};
            my $options = $self->generate_backend_options($server);
            $config .= "  server srv-${machine_id}-${port} ${ip}:${port} check ${options}\n";
        }
    }
    foreach my $target (@{$lb->{'backend_targets'}})
    {
        my $name = $self->server_name($target->{'target'});
        my $ip = $self->safe_target($target->{'target'});
        my $port = $target->{'port'};
        my $options = $self->generate_backend_options($target);
        $config .= "  server trg-${name}-${port} ${ip}:${port} check ${options}\n";
    }
    return $config;
}

sub backend_cookie
{
    my ($self, $backend) = @_;
    if ($backend->{'kind'} eq 'load_balancer_backend_target')
    {
        my $value = lc($backend->{'target'}) =~ s/[^a-z0-9]//gr;
        return $value . $backend->{'port'};
    }
    elsif ($backend->{'kind'} eq 'load_balancer_backend_server')
    {
        my $value = lc($backend->{'machine_id'}) =~ s/-//gr;
        return $value . $backend->{'port'};
    }
    return 'any'
}

sub generate_backend_options
{
    my ($self, $backend) = @_;
    my $options = '';
    if (defined $backend->{'admin_state'})
    {
        if ($backend->{'admin_state'} eq 'BACKUP')
        {
            $options .= ' backup';
        }
        elsif ($backend->{'admin_state'} eq 'DISABLED' || $backend->{'admin_state'} eq 'IN_MAINTENANCE')
        {
            $options .= ' disabled';
        }
    }
    return $options;
}

sub safe_target
{
    my ($self, $name) = @_;
    $name = lc($name);
    $name = $name =~ s/[^a-z0-9.-]//gr;
    return $name;
}

sub server_name
{
    my ($self, $name) = @_;
    $name = lc($name);
    $name = $name =~ s/[._:]/-/gr;
    $name = $name =~ s/[^a-z0-9-]//gr;
    return $name;
}

sub backend_name
{
    my ($self, $lb) = @_;
    return 'be_' . $lb->{'id'};
}

sub build_haproxy_health_check_mode
{
    my ($self, $mode) = @_;
    if ($mode eq 'pgsql')
    {
        return 'pgsql-check'
    }
    elsif ($mode eq 'tls')
    {
        return 'ssl-hello-chk'
    }
    return 'tcp-check'
}

sub build_haproxy_health_check_http_status_regex
{
    my ($self, $status) = @_;
    # TODO
    return '(2|3)[0-9][0-9]'
}

sub set_file_permissions
{
    my ($self, $path, $mode) = @_;
    chmod $mode, $path;
    my ($login,$pass,$uid,$gid) = getpwnam('haproxy');
    chown 0, $gid, $path;
}

sub write_file
{
    my ($self, $path, $content, $mode) = @_;
    $self->logger()->info("Writing file " . $path);
    open(my $fh, '>', $path);
    print $fh $content;
    close $fh;
    # chown
    $self->set_file_permissions($path, $mode);
}

1; 
 
