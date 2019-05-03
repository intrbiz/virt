#!/usr/bin/perl
use 5.010;
use Virt::LBD;

my $lbd = Virt::LBD->new();
$lbd->cli();
