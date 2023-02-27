Home-Center
-----------

TODO
====

#. Write this documentation
#. Strip DISTRO_FEATURES_DEFAULT

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