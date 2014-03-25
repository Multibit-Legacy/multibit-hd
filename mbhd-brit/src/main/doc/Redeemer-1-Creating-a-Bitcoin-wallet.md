## Redeemer - 1 - Creating a Bitcoin wallet

## Introduction
The Redeemer needs to generate EC keys and provide them to the Matcher.
This is so that the Matcher can, in turn, give the Payers an address generator with which they will use to send the Redeemer bitcoin.

There are the following steps:


## 1. Create a MultiBit redeemer wallet
MultiBit Classic (available for download at https://multibit.org) is used to generate a wallet with the required EC private keys in.

Using MultiBit Classic create a wallet called 'redeemer.wallet'.
Store it in a convenient directory, say, `myRedeemer`.


The directory structure will then be:

myRedeemer
   redeemer.wallet      < MultiBit wallet
   redeemer.info        < MultiBit wallet info
   redeemer-data        < MultiBit wallet backups


# 2. Create the right number of private keys in the MultiBit wallet
Each PGP public key is used to protect the secrets for a single Bitcoin private key.
Thus, there should be the same number of addresses in the MultiBit wallet as there are keys in the GPG keyring.

Say you have 4 GPG keys that you have added to the redeemer/gpg keyring.
Then you want to create 4 addresses in you MultBit redeemer wallet - one for each GPG key.

You create new addresses in MultiBit using the 'New' button on the 'Request' tab.


# 3. Find the public key for each of the private keys
Each private key in your Bitcoin wallet has a public key.
This is used in the generation of payment addresses.

To find this currently you need to use an IDE do a log.debug(wallet.toString()); .

Keep the public keys handy as they are used in the PGP key generation.

NOTE: The public key is NOT the bitcoin address.


# Summary
The redeemer has created a Bitcoin wallet using MultiBit classic.
The redeemer has created the same number of private keys in this wallet as the number of PGP keys they will create
(in the next step)
The redeemer has found the public keys for the private keys in their redeemer wallet (used in the next step).
