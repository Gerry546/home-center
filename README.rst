Home-Center
-----------

TODO
====

#. Write this documentation

Goal of the project
===================

Setting up the repository
=========================

We will use Kas so we need Python 3. To not polute the host we create a virtual environment for this.

.. code:: console
    python3 -m venv .venv
    source .venv/bin/activate
    pip3 install kas

Building the system
===================

To build the system using kas use the following (ensure you sources the environment first):

.. code:: console
    kas build home-center-distro.yml

Flashing the system
===================