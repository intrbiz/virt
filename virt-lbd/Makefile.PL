use 5.010000;
use ExtUtils::MakeMaker;

WriteMakefile(
	NAME              => 'Virt::LBD',
	VERSION_FROM      => 'lib/Virt/LBD.pm',
	PREREQ_PM         => {
		'Data::Dump'           => 0,
		'JSON'                 => 0,
		'Moo'                  => 0,
		'XML::Simple'          => 0,
		'Log::Log4perl'        => 0
	},
	EXE_FILES         => [
		'bin/virt-lbd.pl'
	],
	ABSTRACT_FROM     => 'lib/Virt/LBD.pm',
	AUTHOR            => 'Chris Ellis',
);
