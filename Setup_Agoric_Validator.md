# How to setup Agoric validator

This documents describe how we setup Agoric nodes for this testnet.

## Overall configuration

We adopted following configuration for stable operation of Agoric nodes.

1. Deployed multiple sentry nodes to hide a validator node from direct network manipulation and address various network attacks.
2. nodes are located in multiple clouds and regions using VMs.
3. Some of nodes are behind load balancer.

Please understand that we may not disclose exact number and configuration for security reasons.

### Hardware specification

- Dedicated 4cpu / 16GB
- Ubuntu 18.08LTS

Most nodes of cosmos SDK based blockchain work well with this specification.

## Node configuration 

1. Validator
	- `pex = false` 
	    - Do not share validator information with other peers
	- `laddr = "tcp://127.0.0.1:26657"`  
	    - Open RPC to localhost only
	- `unsafe = true` 
	    - Allo unsafe RPC commands. For example, adding peers without restarting node or flushing mempool when there are too many transactions.
	- `prometheus = true` 
	    - Enable prometheus metrics

2. Sentry
	- `pex = true` 
	    - Share sentry node information with other peers
	- `seeds = “312ff22094a63ae783b73e43b2732b1d02a7e544@164.90.162.220:26656,647ee57827efcc2b79741f7b95b96da5a111f882@104.236.29.108:26656”` 
	    - Connect to predefined seed nodes
	- `persistent_peers =. “my_validator_node_id@ip:port,9876c1b8f13c4ff3b2102de7b51a2d641092ab35@167.172.36.124:26656,1955d22c8fe1e612a26eeae1176eb55e4f499897@138.68.134.166:26656,7e6be50ffa15e32078e03e2f4cfaedd136237a23@157.245.11.32:26656,d0529494b41949f4d14cd4269d19a72f9c50c443@178.128.51.171:26656"` 
	    - Connect to validator node directly in addition to public peers
	- `unconditional_peer_ids = “my_validator_node_id”`
	    - Force sentry node to always connect validator node
	- `private_peer_ids = “my_validator_node_id”`
	    - Don't share validator information with other peers
	- `laddr = "tcp://127.0.0.1:26657"`  
	    - Open RPC to localhost only
	- `unsafe = true`
	    - Allo unsafe RPC commands. For example, adding peers without restarting node or flushing mempool when there are too many transactions.
        - `prometheus = true`
	    - Enable prometheus metrics
	- `minimum-gas-prices=xxuagstake`
	    - Prevent zero-fee transaction attack

### Firewall configuration

1. Validator node (allow following in-bounds)
	- TCP 26660 / 142.93.181.215/32 (prometheus.testnet.agoric.net)
	- TCP 26660 / DSRV prometheus IPs
	- TCP 9464 / 142.93.181.215/32 (prometheus.testnet.agoric.net)
	- TCP 26656 / DSRV sentry nodes

2. Sentry nodes (allow following in-bounds)
	- TCP 26660 / DSRV prometheus IPs
	- TCP 26656 / 0.0.0.0 (allow all in-bounds)

### In-house node manager developed by DSRV
This manager monitors nodes and automatically fixes issues if possible.

When monitoring nodes, the manager examines following metrics.
- Metrics
    1. process status
    2. peer connections
    3. status of syncing
    4. misc. system metrics
