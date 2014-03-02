# Intrbiz Virt
A basic wrapper library around the LibVirt Java bindings.

This provides a nicer, cleaner library around the rawer libvirt Java bindings.  It 
only covers simple use cases currently, but is sufficient for most usage.  It provides 
a semi complete definition data model which can parse and write the libvirt XML.  It 
also solves problems with connections not closing properly, by tracking and freeing 
underlying objects when the connection is closed.

## Usage

	try (LibVirtAdapter lv = LibVirtAdapter.sshConnect("localhost"))
	{
		for (LibVirtDomain dom : lv.listDomains())
		{
			System.out.println("Domain: " + dom.getName() + " {" + dom.getUUID() + "} running: " + dom.isRunning());
			for (LibVirtDisk disk : dom.getDisks())
			{
				System.out.println("  Disk: " + disk.getDevice() + " " + disk.getTargetBus() + "." + disk.getTargetName() + " -> " + disk.getSourceUrl());
			}
			for (LibVirtInterface iface : dom.getInterfaces())
			{
				System.out.println("  Interface: " + iface.getType() + " " + iface.getMacAddress() + " -> " + iface.getBridge() + "::" + iface.getName());
			}
			System.out.println();
		}
	}

## License
Intrbiz Virt
Copyright (c) 2013, Chris Ellis
All rights reserved.

Intrbiz Virt is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Intrbiz Virt is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Intrbiz Virt.  If not, see <http://www.gnu.org/licenses/>.

## Thirdparty

Note, this library currently includes a fork of the Libvirt-Java bindings, 
these are also licensed under the LGPL.  See the http://libvirt.org for more 
detail.

Author
------
Chris Ellis

Twitter: @intrbiz

Web: intrbiz.com

Copyright (c) Chris Ellis 2013
