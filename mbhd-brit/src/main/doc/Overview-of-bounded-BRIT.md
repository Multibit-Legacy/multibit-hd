![BRIT Overview](Overview-of-BRIT.png)

## Description of the Burton-Rowe Income Technique (BRIT)


### Executive summary

BRIT provides:

* a means to collect extremely small amounts of bitcoin per transaction efficiently
* no requirement for a central service beyond a single anonymous initial contact
* payments via the Bitcoin blockchain
* encrypted messages to avoid snooping, man-in-the-middle and replay attacks
* income can be allocated to other parties efficiently and anonymously


### Background

A popular application needs to be monetised to continue its growth. The options available are selling equity,
showing in-app adverts, acquiring and selling private data or charging for use. The developers opt for charging for
use but realise that their application is used all over the world and what is cheap to one person is expensive to
another. They decide to make the fee as low as possible and spread the cost.

They also realise that they have built their application on the work of others who are not in a position to
monetise their work. Rather than exploit those upstream, the developers make the decision to create a system where
some of the income from their application goes directly to to upstream developers.

This is intended to create a virtuous circle where upstream developers continue to improve upon their work, but it
comes with an administrative burden and a point of centralisation.

To overcome this the developers create a distributed income stream that is centralised at the point of initial
anonymous contact. This contact occurs when the application first starts but subsequently provides an income stream
in perpetuity without any further involvement from the central server.


### Introduction

This document describes the technique used to receive payments in MultiBit HD.

The user pays a small amount for each transaction send they perform. These are so small they cannot be sent one by one
(they are smaller than the Bitcoin network dust limit) and so are bundled up into larger amounts and sent infrequently
at random intervals.

The fees are sent to a deterministically generated address that uses a secret that is shared between the user's copy of
MultiBit HD and the users wanting to redeem bitcoin. This is done to increase privacy of both parties and to obfuscate
the fee payments on the blockchain.

It is envisaged that other projects will see the benefit of using BRIT and by using it in their projects will contribute
to the overall obfuscation occurring on the blockchain.

All the BRIT code - both client side and server side - is open source using the MIT licence.


### Actors

There are three actors in the BRIT protocol:

1. The entity who will redeem the fee payments, called the Redeemer.
2. The entity who pays the fees, called the Payer.
3. A service that matches up the Redeemer with the Payer, called the Matcher.


### The BRIT protocol

#### 1. Constants

The number of EC keys per Redeemer is denoted `numberOfECKeysPerRedeemer`.
The Redeemer and Matcher have this constant hardwired.
The Payer is sent this this via this protocol spthat it can be updated at runtime.


#### 2. A Matcher service is started

During the first initialisation the Matcher generates a GPG key pair (`matcher.PGP.private`, `matcher.PGP.public`).

The Matcher has a BRIT protocol version number (`matcher.britVersion`) to identify future changes.

The `matcher.PGP.public`  is hardcoded along with the BRIT protocol version into signed applications that are expected to act as Payers.

The protocol now waits for a Redeemer.


#### 3. Redeemer prepares keys

The actor who wishes to redeem bitcoin using BRIT uses an offline machine to create multiples of the following:

 * an EC private key (`redeemer.EC.private`)
 * the corresponding EC public key (`redeemer.EC.public`)

Each of these keypairs is denoted a `Redeemer`.

It should be noted that at this point

```
redeemer.EC.public = G(redeemer.EC.private)       (1)
```

where `G()` is the EC generator function for the Bitcoin curve.

The individuals who want to redeem bitcoin creates multiple instances of these keypairs.
You might have, say, 4 separate individuals who wish to redeem, each of which creates 5 keypairs creating a total of
20 Redeemers.


#### 4. Redeemer distributes keys

The Redeemer copies the `redeemer.EC.public` to wherever the Matcher service is running.

The Matcher now waits until a Payer is introduced to it. This is done when the user first uses
the installed client software.


#### 5. Payer creates random session key

The Payer creates a random session key (`payer.sessionKey`). This is a 16 byte random number. This is used by the
Matcher so that the Payer can have confidence that the responses from the Matcher are genuine.


#### 6. Payer derives unique BRIT wallet identifier

The Payer runs a number of one way trapdoor functions over their wallet seed phrase to deterministically generate a wallet
identifier (`payer.britWalletId`). This is 20 bytes long. It is not computationally feasible to go backwards and derive
the seed phrase from this identifier.

This identifier is NOT used for any other purpose in the client application using BRIT.


#### 7. Payer encrypts message for Matcher

The Payer uses the Matcher's PGP public key (`matcher.PGP.public`) and generates an encrypted message as follows:

```
PGP-encrypt(matcher.britVersion | payer.britWalletId | payer.sessionKey,  matcher.PGP.public)     (2)
```

This is sent to the machine running the Matcher using a convenient transport mechanism.


#### 8. Matcher decrypts message and derives session AES encryption key

The Matcher decodes the message using `matcher.PGP.private`.

The Matcher generates an AES256 key `matcher.AES.encryptionKey` as follows:

```
matcher.AES.encryptionKey = payer.britWalletId          (3.1)
matcher.AES.initialisationVector = payer.sessionKey     (3.2)
```

The purpose of this step is to encrypt the information returned to the Payer to prevent eavesdropping.
It also validates the Matcher is actually the BRIT Matcher and has not been man-in-the-middled.
Only the Matcher has the `matcher.PGP.private` key to decode the message in equation (2).


#### 9. Matcher selects Redeemer and stores the Payer-Redeemer link

The Matcher attempts to locate an existing Payer-Redeemer link using this the `payer.britWalletId`.
If this operation does not yield a Redeemer then one is selected at random.

This approach ensures that if the same `payer.britWalletId` is subsequently encountered, perhaps as a result of an upgrade or a
restoration to a different machine, then the same Redeemer will be selected to provide continuity.

For each Payer the selected Redeemer's `redeemer.EC.public` key is denoted the `payer.addressGenerator`.
The Payer-Redeemer linking information is stored as follows:

```
britWalletId | payer.addressGenerator                   (4)
```

This information can be stored on the Matcher machine for future lookup.


#### 10. Matcher sends the address generator to the Payer

The Matcher encrypts the `addressGenerator` and `numberOfECKeysPerRedeemer` as follows:

```
AES256-encrypt(addressGenerator | numberOfECKeysPerRedeemer, matcher.AES.encryptionKey, matcher.AES.initialisationVector)    (5)
```

The resulting message is sent to the Payer who can decrypt it since they know how `matcher.AES.encryptionKey` and
`matcher.AES.initialisationVector` were generated from the `payer.britWalletId` and `payer.sessionKey` (Equations 3.1 and 3.2).


#### 11. Payer creates payment Bitcoin address and pays bitcoin to it

Any time the Payer has to make a payment they randomly choose an index `i` where 0 <= `i` < `numberOfECKeysPerRedeemer`

```
payer.EC.public(i) = addressGenerator + G(i)                     (8)
```
This is identical by construction to
```
payer.EC.public(i) = redeemer.EC.public + G(i)
```

which then leads to a Bitcoin address as follows:

```
payer.bitcoinAddress(i) = RIPE160(SHA256(payer.EC.public(i)))    (9)
```

All addition is done modulo the size of the Bitcoin EC curve group.

The Payer then creates a Bitcoin transaction with an output spending to this address. It is expected that this output
would be included in a transaction that the Payer would be making for another purpose to minimise inconvenience.

The existence of this output could act as a marker in the overall blockchain for BRIT transactions. There are several
strategies available to further obfuscate the information being leaked that are discussed later.


#### 12. Redeemer redeems bitcoin

The Redeemer can redeem their bitcoin because of the following relationship:

```
payer.EC.public(i) = G(redeemer.EC.private) + G(i)      (10)
				           = G(redeemer.EC.private + i)         (11)
```

Hence the redeeming private key is:

```
payer.EC.private(i) = redeemer.EC.private + i            (12)
```

Because the index `i` is limited to 0 <= `i` < `numberOfECKeysPerRedeemer` the Redeemer can construct all the private keys
for all values of `i`. Typically these would be added to a single wallet which is regularly synchronized.


### Notes

There are a few alterations that could be made to the protocol described above.


#### Decreasing the visibility of BRIT payments

Redeemers wish to conceal their earnings as much as possible on a public blockchain. Strategies to do this are:

* The Matcher can add and remove Redeemers to the list of candidate Redeemers over time.
* The Payer can randomise the order of the actual payment, change address and fee addresses
* The Payer can randomise the number of payments that are bundled up
* The Payer the user a small discount on the fee amount to effectively expand the amounts to fill up the total key space

Note that because the space of possible `payer.bitcoinAddress(i)` is bounded the complete list of bitcoin addresses
for each Redeemer can be computed from the `addressGenerator`. This is done to make redeeming the payments more efficient.
