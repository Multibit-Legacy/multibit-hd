
## Redeemer - 2 - Aggregation and redemption of payments

### Executive summary

The Burton-Rowe Income Technique (BRIT) generates income that is sent directly from users to redeemers.
This document describes in detail how to aggregate these small amounts of bitcoin and redeem this income.


### Introduction

The user pays a small amount for each transaction send they perform. These are so small they cannot be sent one by one
(they are smaller than the Bitcoin network dust limit) and so are bundled up into larger amounts and sent infrequently
at random intervals.

In this document the fee is set for illustration as 500 satoshi for each transaction send. At a price of USD $600 per bitcoin
(March 2014) this is a fee of 0.3 USD cents per send transaction.


### Aggregation and Redemption of Payments.

As the fees are so small they cannot be sent directly one by one so need to be aggregated.
They are aggregated in steps as follows:

1) The client bundles the client fees and attaches an extra transaction output to the user's transaction sends. To help
obfuscate this output it is attached after a random number of sends (typically 20 to 30).

With an average aggregation factor of 25 this produces transaction outputs of size of 125 microBitcoin (7.5 USD cents).


2) The only actor that can redeem the outputs produced in 1) is the Redeemer.
Because the BRIT payments are paid directly to the Redeemer's Bitcoin wallet, they can simply open their wallet,
for instance in MultiBit Classic, let it synchronize and see the incoming payments.

They regularly (typically once a day) aggregate these outputs. Typically 4 outputs take 1 KB of blockchain space so
the Redeemer may aggregate, say, 12 of the 125 microBitcoin outputs at a time in a transaction of size 3KB.

They can do this manually by simply sending bitcoin from the Bitcoin wallet that first receives the payments to an
intermediate 'aggregator wallet'. This is a wallet which is used as a stepping stone to producing larger transaction
outputs.

This produces outputs of size 1.5 milliBitcoin (90 USD cents) in the aggregator wallet.


3) To further reduce the number of unspent transaction outputs (UTXO) on the blockchai, these can be aggregated by the
redeemer by, say, another factor of 12 to produce transaction outputs of 18 milliBitcoin (approximately $10).

The Redeemer can do this by sending larger amounts of bitcoin from their aggregator wallet to their personal Bitcoin wallet.

The Redeemer can spend outputs of this size easily from their personal Bitcoin wallet using conventional Bitcoin wallet software.
