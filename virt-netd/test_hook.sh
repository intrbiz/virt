#!/bin/sh

./qemu m-103a140e-1659-4a41-857c-15405e191315 prepare begin - <<EOF
<domain type='kvm' id='21'>
  <name>m-103a140e-1659-4a41-857c-15405e191315</name>
  <uuid>103a140e-1659-4a41-857c-15405e191315</uuid>
  <memory unit='KiB'>1048576</memory>
  <currentMemory unit='KiB'>1048576</currentMemory>
  <memoryBacking>
    <hugepages>
      <page size='2048' unit='KiB'/>
    </hugepages>
  </memoryBacking>
  <vcpu placement='static'>1</vcpu>
  <sysinfo type='smbios'>
    <bios>
      <entry name='vendor'>Intrbiz</entry>
    </bios>
    <system>
      <entry name='manufacturer'>Intrbiz</entry>
      <entry name='product'>Virt</entry>
      <entry name='version'>1.0.0</entry>
      <entry name='serial'>ds=nocloud-net;s=http://172.16.169.254:8888/</entry>
    </system>
    <baseBoard>
      <entry name='manufacturer'>Intrbiz</entry>
      <entry name='product'>c1</entry>
      <entry name='version'>nano</entry>
    </baseBoard>
    <chassis>
      <entry name='manufacturer'>Intrbiz</entry>
    </chassis>
  </sysinfo>
  <os>
    <type arch='x86_64' machine='pc-i440fx-2.11'>hvm</type>
    <boot dev='hd'/>
    <smbios mode='sysinfo'/>
  </os>
  <features>
    <acpi/>
    <apic/>
    <vmport state='off'/>
  </features>
  <cpu mode='custom' match='exact' check='partial'>
    <model fallback='allow'>Broadwell-noTSX</model>
  </cpu>
  <clock offset='utc'>
    <timer name='rtc' tickpolicy='catchup'/>
    <timer name='pit' tickpolicy='delay'/>
    <timer name='hpet' present='no'/>
  </clock>
  <on_poweroff>destroy</on_poweroff>
  <on_reboot>restart</on_reboot>
  <on_crash>destroy</on_crash>
  <pm>
    <suspend-to-mem enabled='no'/>
    <suspend-to-disk enabled='no'/>
  </pm>
  <devices>
    <emulator>/usr/bin/qemu-system-x86_64</emulator>
    <disk type='file' device='disk'>
      <driver name='qemu' type='qcow2' cache='writethrough'/>
      <source file='/srv/vms/z/uk1.a/a/b7368acd-089a-4214-b496-f5926573103d/m/103a140e-1659-4a41-857c-15405e191315.sda.qcow2'/>
      <target dev='sda' bus='scsi'/>
      <address type='drive' controller='0' bus='0' target='0' unit='0'/>
    </disk>
    <controller type='scsi' index='0' model='virtio-scsi'>
      <address type='pci' domain='0x0000' bus='0x00' slot='0x06' function='0x0'/>
    </controller>
    <controller type='pci' index='0' model='pci-root'/>
    <controller type='usb' index='0' model='piix3-uhci'>
      <address type='pci' domain='0x0000' bus='0x00' slot='0x01' function='0x2'/>
    </controller>
    <interface type='direct'>
      <mac address='52:54:00:30:bc:c2'/>
      <source dev='metadata_vms' mode='bridge'/>
      <model type='virtio'/>
      <address type='pci' domain='0x0000' bus='0x00' slot='0x03' function='0x0'/>
    </interface>
    <interface type='bridge'>
      <mac address='52:54:00:4c:1f:7c'/>
      <source bridge='br-a'/>
      <target dev='v-5254004c1f7c'/>
      <model type='virtio'/>
      <address type='pci' domain='0x0000' bus='0x00' slot='0x04' function='0x0'/>
    </interface>
    <serial type='pty'>
      <target type='isa-serial' port='0'>
        <model name='isa-serial'/>
      </target>
    </serial>
    <console type='pty'>
      <target type='serial' port='0'/>
    </console>
    <input type='mouse' bus='ps2'/>
    <input type='keyboard' bus='ps2'/>
    <graphics type='vnc' port='-1' autoport='yes' listen='0.0.0.0'>
      <listen type='address' address='0.0.0.0'/>
    </graphics>
    <video>
      <model type='qxl' ram='65536' vram='65536' vgamem='16384' heads='1' primary='yes'/>
      <address type='pci' domain='0x0000' bus='0x00' slot='0x02' function='0x0'/>
    </video>
    <memballoon model='virtio'>
      <address type='pci' domain='0x0000' bus='0x00' slot='0x05' function='0x0'/>
    </memballoon>
    <rng model='virtio'>
      <backend model='random'>/dev/urandom</backend>
      <address type='pci' domain='0x0000' bus='0x00' slot='0x07' function='0x0'/>
    </rng>
  </devices>
</domain>
EOF

