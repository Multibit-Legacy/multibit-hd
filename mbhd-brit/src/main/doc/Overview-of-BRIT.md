![BRIT Overview](Overview-of-BRIT.png)

# Description of the Burton-Rowe Income Technique (BRIT)


## Executive summary

BRIT provides:

* a means to collect extremely small amounts of bitcoin per transaction efficiently
* no requirement for a central service beyond a single anonymous initial contact
* payments via the Bitcoin blockchain
* encrypted messages to avoid snooping, man-in-the-middle and replay attacks
* income can be allocated to other parties efficiently and anonymously
* the wallet replay date (used in restoring wallets) is stored centrally, anonymously, to improve wallet recovery.

## Background

A popular application needs to be monetised to continue its growth. The options available are selling equity,
showing in-app adverts, acquiring and selling private data or charging for use. The developers opt for charging for use
with a fee that is as low as possible and spread across all users.

They also realise that they have built their application on the work of others who are not in a position to
monetise their work. The developers create a system where some of the income from their application goes
directly to to upstream developers.

BRIT creates a distributed income stream that is centralised at the point of initial anonymous contact.
This contact occurs when the application first starts but subsequently provides an income stream
forever without any further involvement from the central server.

## Introduction

This document describes the technique used to receive payments in MultiBit HD.

The user pays a small amount for each transaction send they perform. These are so small they cannot be sent one by one
(they are smaller than the Bitcoin network dust limit) and so are bundled up into larger amounts and sent infrequently
at random intervals.

The fees are sent to a list of addresses retrieved from multibit.org at wallet creation.

It is envisaged that other projects will see the benefit of using BRIT in their projects.
All the BRIT code is open source using the MIT licence.

## Actors

There are three actors in the BRIT protocol:

1. The entity who will redeem the fee payments, called the Redeemer.
2. The entity who pays the fees, called the Payer.
3. A service that matches up the Redeemer with the Payer, called the Matcher.

## The BRIT protocol

### 1. Redeemer prepares EC keys and Bitcoin addresses.

The actor who wishes to redeem bitcoin using BRIT uses an offline machine to create multiples of the following:

 * an EC private key
 * the corresponding EC public key
 * the corresponding Bitcoin address (`redeemer.bitcoinAddress`)

Each of these sets of data is denoted a `Redeemer`.

The individuals who want to redeem bitcoin creates multiple instances of these.
You might have, say, 4 separate individuals who wish to redeem, each of which creates 500 keypairs and Bitcoin addresses
creating a total of 2000 Redeemers.


### 2. Redeemer copies Bitcoin addresses to Matcher.

The actor who wishes to redeem bitcoin copies their `redeemer.bitcoinAddress` list to wherever the Matcher service is running.


### 3. A Matcher service is started

The Matcher has a BRIT protocol version number (`matcher.britVersion`) to identify future changes.

During the first initialisation the Matcher generates a GPG key pair (`matcher.PGP.private`, `matcher.PGP.public`).
The `matcher.PGP.public` is hardcoded along with the BRIT protocol version into signed applications that are expected to act as Payers.

At the beginning of each UTC day Bitcoin addresses are selected at random from the total list of
all Redeemers' bitcoin addresses. The number of Bitcoin addresses selected is denoted `numberOfBitcoinAddressesPerDay`
These are stored, with a reference to the day the selection was made, for later lookup.
This data is denoted as:

    `matcher.dateEncountered`        << The date the bitcoinAddresses were chosen
    `matcher.bitcoinAddressList`     << The list of bitcoinAddresses chosen that day


### 4. Payer creates random session key

The Payer creates a random session key (`payer.sessionKey`). This is a 16 byte random number. This is used by the
Matcher so that the Payer can have confidence that the responses from the Matcher are genuine.


### 5. Payer derives a unique BRIT wallet identifier

The Payer runs a number of one way trapdoor functions over their wallet seed phrase to deterministically generate a wallet
identifier (`payer.britWalletId`). This is 20 bytes long. It is not computationally feasible to go backwards and derive
the seed phrase from this identifier.

This identifier is NOT used for any other purpose except for BRIT in the Payer's application.

### 6. Payer derives date of first transaction

MultiBit HD wallets are restored from the wallet's creation date.
Users typically do not know this information so the BRIT system stores this information, anonymously, centrally.

In this step if there are any transactions in the Payer's wallet, the Payer determines the date of the first transaction, denoted
`payer.firstTransactionDate`.

### 7. Payer encrypts message for Matcher

The Payer uses the Matcher's PGP public key (`matcher.PGP.public`) and generates an encrypted message as follows:

    PGP-encrypt(matcher.britVersion | payer.britWalletId | payer.sessionKey | payer.firstTransactionDate,  matcher.PGP.public)     (1)

This is sent to the machine running the Matcher using a convenient transport mechanism.

### 8. Matcher decrypts message and derives session AES encryption key

The Matcher decodes the message using `matcher.PGP.private`.

The Matcher generates an AES256 key `matcher.AES.encryptionKey` as follows:

    matcher.AES.encryptionKey = SHA256(payer.britWalletId)          (2)
    matcher.AES.initialisationVector = payer.sessionKey             (3)

The SHA256 is used to stretch the britWalletId to 32 bytes, suitable for use as an AES encryption key.

The purpose of this step is to encrypt the information returned to the Payer to prevent eavesdropping.
It also validates the Matcher is actually the BRIT Matcher and has not been man-in-the-middled.
Only the Matcher has the `matcher.PGP.private` key to decode the message in equation (1).


### 9. Matcher stores the Payer-date encountered link

The Matcher sees if the britWalletId has been encountered before.
If it has, it will look up the date it first encountered it.

Otherwise it will create a new link containing:

    britWalletId | matcher.dateEncountered | payer.firstTransactionDate

### 10. Matcher sends replay date and Bitcoin address list to Payer

The Matcher works out the date the Payer needs to replay their wallet from as:

    matcher.replayDate = the earlier of (matcher.dateEncountered, payer.firstTransactionDate)

The matcher encrypts the`matcher.replayDate` and the `matcher.bitcoinAddressList` as follows:

    AES256-encrypt(matcher.replayDate | matcher.bitcoinAddressList, matcher.AES.encryptionKey, matcher.AES.initialisationVector)

The resulting message is sent to the Payer who can decrypt it since they know how `matcher.AES.encryptionKey` and
`matcher.AES.initialisationVector` were generated from the `payer.britWalletId` and `payer.sessionKey` (Equations 2 and 3).

### 11. Payer selects payment Bitcoin address and pays bitcoin to it

Any time the Payer has to make a payment they randomly choose a Bitcoin address from `matcher.bitcoinAddressList`.

The Payer then creates a Bitcoin transaction with an output spending to this address. It is expected that this output
would be included in a transaction that the Payer would be making for another purpose.

The existence of this output could act as a marker in the overall blockchain for BRIT transactions. There are several
strategies available to further obfuscate the information being leaked that are discussed later.

### 12. Redeemer redeems bitcoin

The Redeemer can monitors all the Bitcoin addresses in their `redeemer.bitcoinAddress` list. They can redeem these
payments directly as they have the private keys for all of these addresses.

## Notes

There are a few additions that could be made to the protocol described above.

### Decreasing the visibility of BRIT payments

Redeemers wish to conceal their earnings as much as possible on a public blockchain. Strategies to do this are:

* The Matcher can add and remove Redeemers to the list of candidate Redeemers over time.
* The Payer can randomise the order of the actual payment, change address and fee addresses
* The Payer can randomise the number of payments that are bundled up
* The Payer the user a small discount on the fee amount to effectively expand the amounts to fill up the total key space