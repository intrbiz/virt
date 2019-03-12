#!/usr/bin/perl
use 5.010;
use Virt::NetD;

my $netd = Virt::NetD->new();
$netd->cli();
