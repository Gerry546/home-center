Home-Center
-----------

TODO
====

#. Write this documentation
#. Strip DISTRO_FEATURES_DEFAULT
#. Create own toolchain file to load
#. Trim DISTRO_FEATURES_DEFAULT
#. Fix that it is possible to load the device tree via tftp
#. Light sensor always returns 0?
#. Ensure power button actually shuts down the system
#. Change password on first login
#. Check if this is yocto compatible
#. Experiment with initramFS image
#. Unchecked hardware See https://wiki.seeedstudio.com/reTerminal-hardware-interfaces-usage/#hardware-overview
   Micro SD card slot
   Micro HDMI port

Goal of the project
===================

System features
================
#. RAUC A/B partitioning

Setting up the repository
=========================

We will use Kas so we need Python 3. To not polute the host we create a virtual environment for this.::

    $ python3 -m venv .venv
    $ source .venv/bin/activate
    $ pip3 install kas

For running on the target we need to have programs capable of flashing the MMC memory of the reterminal.
Install ``rpiboot`` by following the steps detailed here: https://github.com/raspberrypi/usbboot#building

If you plan on using TFTP/NFS then install the following::
    $ sudo apt install tftpd-hpa
    $ sudo apt install nfs-kernel-server

And adjust the file `\etc\exports` to properly export the directory.

And configure your network manager on your host to host the nfsroot directory::
    $ nmcli con add type ethernet ifname en... ip4 192.168.0.1/24

Running the Qemux86-64 system
=============================

Building the system
~~~~~~~~~~~~~~~~~~~
To build the system source KAS and run the following command::

    $ kas build home-center-qemux86-64.yml

Running the system
~~~~~~~~~~~~~~~~~~
Boot the simulation image::

    $ kas shell kas/home-center-qemux86-64.yml -c 'runqemu nographic wic ovmf slirp

Updating the system
~~~~~~~~~~~~~~~~~~~
First check if RAUC is working correctly::

    # rauc status

Note:
By default using ``slirp`` will forward ports 22 and 23 on the qemu system to ports 2222 and 2323 on the host system.
If there is a collision with another runqemu instance, the script will pick the next free port.
You can define custom port forwarding by setting ``hostfwd`` in ``QB_SLIRP_OPT``. Examples::

    $ QB_SLIRP_OPT="-netdev user,id=net0,hostfwd=tcp::<host system port>-:<qemu system port>" runqemu core-image-minimal wic nographic ovmf slirp

    $ QB_SLIRP_OPT="-netdev user,id=net0,hostfwd=tcp::2222-:22,hostfwd=tcp::2323-:23" runqemu core-image-minimal wic nographic ovmf slirp

Slirp can be useful for remote access to the virtual machine without needing root access to the host machine.
Keep in mind firewalls on both the host and the qemu machines should be configured based on your needs.

Obtain an IP address on the target::

    # udhcpc -i eth0


Copy update Bundle from host to the target::

    $ scp -P 2222 -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null build/tmp/deploy/images/qemu-homely-x86-64/home-center-bundle-qemu-qemu-homely-x86-64.raucb  root@localhost:/tmp

Check Bundle on the target::

    # rauc info /tmp/qemu-demo-bundle-qemux86-64.raucb

Install the Bundle::

    # rauc install /tmp/qemu-demo-bundle-qemux86-64.raucb
    installing
      0% Installing
      0% Determining slot states
     20% Determining slot states done.
     20% Checking bundle
     20% Verifying signature
     40% Verifying signature done.
     40% Checking bundle done.
     40% Checking manifest contents
     60% Checking manifest contents done.
     60% Determining target install group
     80% Determining target install group done.
     80% Updating slots
     80% Checking slot efi.0
     85% Checking slot efi.0 done.
     85% Copying image to efi.0
     90% Copying image to efi.0 done.
     90% Checking slot rootfs.1
     95% Checking slot rootfs.1 done.
     95% Copying image to rootfs.1
     100% Copying image to rootfs.1 done.
     100% Updating slots done.
     100% Installing done.
     Installing `/tmp/qemu-demo-bundle-qemux86-64.raucb` succeeded

Reboot the system::

    # systemctl reboot

Running the Hometerminal system
===============================

Building the system
~~~~~~~~~~~~~~~~~~~
To build the system source KAS and run the following commands::

    $ kas build kas/home-center-hometerminal-cm4.yml --update

When building the system you have the option to use TFTP and NFS. If you want to use it adjust this parameter in the distro file.
Use this only for debugging since it requires a live system next to it.

..code:: console

    ENABLE_TFTP_NFS = "1"

Flashing the system
~~~~~~~~~~~~~~~~~~~

No TFTP/NFS
""""""""""""
When not building with TFTP, usually in your final image, or if you want to update the devicetree (since raspberry pi has a different boot sequence), 
you can flash it via this way:

#. Unscrew the back as dictated in on the website here: https://wiki.seeedstudio.com/reTerminal/#flash-raspberry-pi-os-64-bit-ubuntu-os-or-other-os-to-emmc
#. Connect a debug UART to the pins on the side of the reterminal (number 6, 8 and 10): https://wiki.seeedstudio.com/reTerminal/#pinout-diagram
#. Start picocom: :code:`picocom -b 115200 /dev/ttyUSB0`
#. Start rpiboot: :code:`sudo ~/tools/usbboot/rpiboot`
#. Connect a USB cable from your workstation to your reterminal. It should boot ``rpiboot`` should be able to finish.
#. Check where the drives are in ``/dev`` and unsure they are unmounted (on my station it is always ``/dev/sdb``): :code:`sudo umount /dev/sdb*`
#. Copy the new image in .wic format to the reterminal: :code:`sudo bmaptool copy tmp/deploy/images/hometerminal-cm4/hometerminal-image-hometerminal-cm4.rootfs.wic /dev/sdb`
#. If it is done, unplug the reterminal, flip the boot switch and boot again. It should now boot normally.

With TFTP/NFS
""""""""""""""
First flash the system using the system above once since the bootloader needs to be present.
After building run the ::code:`tftp_nfs.sh` script located in the utils folder to copy everything.
Now when booting the system the target should retrieve the kernel and rootfs from the host.
