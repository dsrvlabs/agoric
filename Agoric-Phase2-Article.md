# Agoric Testnet Phase 2 - Digging into Tendermint v0.34.9

Last week, Agoric testnet Phase 2 has been started.

We, engineers at DSRV, have prepared nodes and performed pre-checks for testnet reset.

Most validators seemed ready, and Genesis time for Agoric testnet Phase 2 has come. However, Agoric testnet was unable to bootstrap due to bugs, and Genesis time has been postponed to Friday at 16:00 UTC.

After applying fixes from the Agoric team, the Agotic testnet successfully produces blocks about 1hrs after scheduled Genesis time. It took about an hour to get 2/3 quorum of validators.

We also participated in the Phase 2 genesis event and performed a scheduled investigation for validator and sentry nodes after the network started.

During the investigation, we observed some weird behavior of validator and nodes, i.e., p2p connection between validator and nodes was unstable, and connections are established and disconnected repeatedly.

```
Err msg : 
unknown channel 0
```

![image](https://raw.githubusercontent.com/dsrvlabs/agoric/main/images/Agoric-Phase2-tendermint-issue.png)

The above image shows that the validator's number of peers is changing frequently. It should have constant three peers because the validator is connected to three persistence sentry nodes.

Due to this behavior, the average number of peers is lower than expected and Agoric testnet shows unstable behavior in overall.

We found that peers could not make a connection with `unknown channel 0` message even if p2p channels of nodes are matched each other.

```
40 BlockchainChannel
20 StateChannel
21 DataChannel
...
```

We dug into the issue and found that the root cause is changes made in the p2p module of Tendermint v0.34.9.
(https://github.com/tendermint/tendermint/commit/c9966cd6befce6efe55b1b4cb9df32781fbbc64a)

Tendermint is also aware of this issue and reverted changes to fix the issue (https://github.com/tendermint/tendermint/pull/6297) recently.

We expected a new binary for "Emergency Upgrade: Restart node Task" during Phase 2 will fix the problem.
However, this fix was not included in the new binary and we decided to share this issue with an investigation summary to the Agoric team.

Hopefully, we expect this issue fixed in the following new releases.

