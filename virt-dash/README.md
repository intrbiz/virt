# Virt Dash
Virt Dash is a simple web dashboard for libvirt.  It polls multiple VM hosts 
and allows for simple management of VM hosts.  Guests can be started, stoped 
and the guest console can be opened.

## Running Virt Dash
Virt Dash is a Balsa application, it requires Java 7 to run and uses nginx to 
handle HTTP requests.  Websockify (included) is needed to provide console 
support.  The libvirt client library will also need to be installed.



## Third Party
Virt Dash includes some third party components:
  * noVNC
  * websockify
  * Oxygen Icons
  * jQuery
