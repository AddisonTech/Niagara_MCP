# Safety

## Running a copied station

A station copied from a live site, such as a `config.bog` pulled from a
Supervisor or a JACE, still contains that site's driver networks. When it
starts, it tries to talk to them. If the machine can reach the site, for
example over a VPN, the copy polls and writes to the real devices. On
BACnet it can also appear as a duplicate device on the site network.

Before a copied station starts for the first time on a test machine:

1. Disable every network under `Drivers`: BACnet, Modbus, NiagaraNetwork
   and any others. Set `enabled` to false on the network component itself.
2. Confirm it in the bog. Check that every network under `Drivers` is
   saved with `enabled` false. Do not assume a disable made in an editor
   was saved.
3. Only then start the station.

If you cannot confirm that a network is disabled, do not start the station.
