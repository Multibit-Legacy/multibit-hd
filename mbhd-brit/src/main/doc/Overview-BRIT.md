![BRIT Overview](Overview-brit.png)

## Description of the Burton-Rowe Income Technique (BRIT)

### Executive summary

BRIT provides:

* a means to collect extremely small amounts of bitcoin per transaction efficiently
* no requirement for a central service beyond a single anonymous initial contact
* payments via a blockchain
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
MultiBit HD and the BRIT server. This is done to increase privacy of both parties and to obfuscate the fee payments on
the blockchain.

It is envisaged that other projects will see the benefit of using BRIT and by using it in their projects will contribute
to the overall obfuscation occurring on the blockchain.

All the BRIT code - both client side and server side - is open source using the MIT licence.

### Actors

There are three actors in the BRIT protocol:

1. The entity who will redeem the fee payments, called the Redeemer.
2. The entity who pays the fees, called the Payer.
3. A service that matches up the Redeemer with the Payer, called the Matcher.

### The BRIT protocol

#### 1. A Matcher service is started

During the first initialisation the Matcher generates a GPG key pair (`matcher.PGP.private`, `matcher.PGP.public`).

The Matcher has a BRIT protocol version number (`matcher.britVersion`) to identify future changes.

The `matcher.PGP.public` is made available to popular key repositories, such as MIT. It is also hardcoded along with
the BRIT protocol version into signed applications that are expected to act as Payers.

The protocol now waits for a Redeemer.

#### 2. Redeemer prepares keys

The Redeemer uses an offline machine to create multiples of the following:

 * a GPG private key (`redeemer.PGP.private`)
 * the corresponding GPG public key (`redeemer.PGP.public`)
 * an EC private key (`redeemer.EC.private`)
 * the corresponding EC public key (`redeemer.EC.public`)

Each of these datasets is denoted a `Redeemer`.

It should be noted that at this point

```
redeemer.EC.public = G(redeemer.EC.private)    (1)
```

where `G()` is the EC generator function for the Bitcoin curve.

The individuals who want to redeem bitcoin creates multiple copies of this dataset.
You might have, say, 4 separate individuals, each of which creates 25 sets of this data creating a total of
100 Redeemers.

For each set of data above redeemer also produces the `redeemer.identifier`. This is the SHA256 hash of
the `redeemer.PGP.public`. (It is used later by the Matcher to record where payments will be sent to)


#### 3. Redeemer distributes keys

The Redeemer copies the `redeemer.PGP.public` and `redeemer.EC.public` to wherever the Matcher service is running.

The Matcher creates the `redeemer.identifier` from the `redeemer.PGP.public` using a SHA256 hash in the
same way as the Redeemer.

The protocol now waits until a Payer is introduced to the Matcher. This is done when the user first uses
the installed client software.


#### 4. Payer creates random session key

The Payer creates a random session key (`payer.sessionKey`). This is used by the Matcher so the Payer can have confidence
that the responses from the Matcher are genuine.


#### 5. Payer derives unique BRIT wallet identifier

The Payer runs a number of one way trapdoor functions over their wallet seed phrase to deterministically generate a wallet
identifier (`payer.britWalletId`). It is not computationally feasible to go backwards and derive the seed phrase from
this identifier.

This identifier is not used for any other purpose in the client application using BRIT. For example, it is
not used as part of a wallet persistence mechanism.


#### 6. Payer encrypts message for Matcher

The Payer securely obtains the Matcher's PGP public key (`matcher.PGP.public`) and generates an encrypted message as follows:

```
PGP-encrypt(matcher.britVersion | payer.britWalletId | payer.sessionKey,  matcher.PGP.public)     (2)
```

This is sent to the machine running the Matcher using a convenient transport mechanism.


#### 7. Matcher decrypts message and derives session AES encryption key

The Matcher decodes the message using `matcher.PGP.private`.

The Matcher generates an AES-256 key `matcher.AES.encryptionKey` from the `payer.sessionKey` using

```
matcher.AES.encryptionKey = Scrypt(payer.sessionKey)    (3)
```

The purpose of this encryption key is to encrypt the information returned to the Payer and validates the Matcher is actually
the BRIT Matcher. Only the Matcher has the `matcher.PGP.private` key to decode the message in (2).


#### 8. Matcher selects Redeemer

The Matcher calculates a unique `shortWalletID` using

```
shortWalletID = RIPE160(SHA256(payer.britWalletId))    (4)
```

and attempts to locate an existing Redeemer-Payer link using this identifier. If this operation does not yield a Redeemer
then one is chosen at random.

This approach ensures that if the same `payer.britWalletId` is subsequently encountered, perhaps as a result of an upgrade or a
restoration to a different machine, then the same Redeemer will be selected to provide continuity.

The `payer.britWalletId` is hashed to avoid the raw value being stored unencrypted on the Matcher file system.


#### 9. Matcher derives an address generator

The Matcher derives an EC address generator as follows:

```
addressGenerator = redeemer.EC.public + G(payer.britWalletId)    (5)
```

The Redeemer's EC public key is required to ensure the client cannot redeem their payments themselves.


#### 10. Matcher stores the Redeemer-Payer link

The Redeemer-Payer linking information is stored as follows:

```
shortWalletId | redeemer.identifier | PGP-encrypt(payer.britWalletId , redeemer.PGP.public)    (6)
```

The `shortWalletId` is stored in plaintext to allow fast lookup in large data sets.

The Redeemer's PGP key is used to encrypt the `payer.britWalletId` to hide it from the Matcher and protect the Redeemer from a
compromise of the Matcher database.


#### 11. Matcher sends the address generator to the Payer

The Matcher encrypts `addressGenerator` as follows:

```
AES-256-encrypt(addressGenerator, matcher.AES.encryptionKey)    (7)
```

The resulting message is sent to the Payer who can decrypt it since they know how `matcher.AES.encryptionKey`
was derived from the `payer.sessionKey`


#### 12. Payer creates payment Bitcoin address

Any time the Payer has to make a payment they choose an index `i`, monotonically increasing for each send payment
they make with the wallet, starting at 0.

```
payer.EC.public(i) = addressGenerator + G(i) (                   8)
```
which is identical to
```
payer.EC.public(i) = redeemer.EC.public + G(payer.britWalletId) + G(i)
```

which then leads to a Bitcoin address as follows:

```
payer.bitcoinAddress(i) = RIPE160(SHA256(payer.EC.public(i)))    (9)
```

The Payer then creates a Bitcoin transaction with an output spending to this address. It is expected that this output
would be included in a transaction that the Payer would be making for another purpose to minimise inconvenience.

The existence of this output could act as a marker in the overall blockchain for BRIT transactions. There are several
strategies available to further obfuscate the information being leaked that are discussed later.

#### 13. Redeemer synchronizes with Matcher

From time to time the Redeemer will synchronize with the Matcher store to obtain fresh `payer.britWalletId` values.
To be able to check that the proportion of total installs allocated to their
`redeemer.identifier` is correct they will typically obtain the whole list of data stored in (10) to parse.

If an attacker guesses `redeemer.identifier`, or is able to clone the Matcher database, this will not assist them
in either identifying the Redeemer or the address generator for any Redeemer-Payer relationship.


#### 14. Redeemer derives private key

Whenever a Redeemer wishes to redeem a payment they can do so because of the following relationship:

```
payer.EC.public(i) = G(redeemer.EC.private) + G(payer.britWalletId) + G(i)    (10)
				           = G(redeemer.EC.private + payer.britWalletId + i)          (11)
```

Hence the redeeming private key is:

```
payer.EC.private(i) = redeemer.EC.private + payer.britWalletId + i            (12)
```

So long as the Redeemer is able to obtain `payer.britWalletId` and track the incrementing values of `i` then they can
create the necessary private key to spend.

### Notes

There are a few alterations that could be made to the protocol described above.

#### Decreasing the visibility of BRIT payments

Redeemers wish to conceal their earnings as much as possible on a public blockchain. Some steps they could take are:

* Randomise the order of the actual payment, change address and fee addresses
* Randomise the number of payments that are bundled up
* Offer the user a small discount on the fee amount to effectively expand the amounts to fill up the total key space