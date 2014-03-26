
## Redeemer - 3 - Redeeming bitcoin

### Executive summary

The Burton-Rowe Income Technique (BRIT) generates income that is sent directly from users to redeemers.
This document describes in detail how to redeem this income.


### Introduction

The user pays a small amount for each transaction send they perform. These are so small they cannot be sent one by one
(they are smaller than the Bitcoin network dust limit) and so are bundled up into larger amounts and sent infrequently
at random intervals.

In this document the fee is set for illustration as 500 satoshi for each transaction send. At a price of USD $600 per bitcoin
(March 2014) this is a fee of 0.3 USD cents per send transaction.

As the fees are so small, they are aggregated in steps as follows:

1) The client bundles the client fees and attaches an extra transaction output to the user's transaction sends. To help
obfuscate this output it is attached after a random number of sends (typically 20 to 30).

With an average aggregation factor of 25 this produces transaction outputs of size of 125 microBitcoin (7.5 USD cents).

2) The only actor that can redeem the outputs produced in 1) is the Redeemer.
They regularly (typically once a day) aggregate these outputs. Typically 4 outputs take 1 KB of blockchain space so
the Redeemer may aggregate, say, 12 of the 125 microBitcoin outputs at a time in a transaction of size 3KB.

This produces outputs of size 1.5 milliBitcoin (90 USD cents).

To reduce the number of unspent transaction outputs (UTXO) on the blockchain, these can be further aggregated by the
redeemer by, say, another factor of 12 to produce transaction outputs of 18 milliBitcoin (approximately $10).

The Redeemer can spend outputs of this size easily using conventional Bitcoin wallet software.


### Redemption steps

There are the following steps in the BRIT redemption process.

1. The Redeemer obtains the latest encrypted Redeemer-Payer linking information from the Matcher. This provides the
information required by the Redeemer to construct the private keys of all of their incoming payments.
Typically this information will be produced daily by the Matcher and have 'thousands' of records.

2. The Redeemer constructs a Bitcoin wallet with the private keys for their incoming payments. The private keys are generated
deterministically from combining their EC private key, the britWalletId and an index.

3. The Redeemer synchronises with the blockchain all of their previously created wallets plus the new one. Individual wallets
are created daily to avoid scaling issues.

4. Aggregation is done by the Redeemer eventually resulting in 'tens' of aggregated outputs being sent to their personal Bitcoin
wallet.

The code to perform these steps is packaged as a command line utility that can be run as a scheduled task i.e. a 'cron job'.
It is important to aggregate all the small Bitcoin transaction outputs promptly or else they start cluttering up the UTXO set.
This is poor Bitcoin etiquette as it imposes an exogeneous cost on other Bitcoin users.

