use 5.010000;
use ExtUtils::MakeMaker;

WriteMakefile(
	NAME              => 'Virt::NetD',
	VERSION_FROM      => 'lib/Virt/NetD.pm',
	PREREQ_PM         => {
		'Data::Dump'           => 0,
		'JSON'                 => 0,
		'Moo'                  => 0,
		'XML::Simple'          => 0,
		'Log::Log4perl'        => 0
	},
	EXE_FILES         => [
		'bin/virt-netd.pl'
	],
	ABSTRACT_FROM     => 'lib/Virt/NetD.pm',
	AUTHOR            => 'Chris Ellis',
);
