# Redeemer: Part 1 - Creating Bitcoin wallets

## Introduction
The Redeemer needs to generate EC keys and provide the corresponding Bitcoin addresses to the Matcher.
This is so that the Matcher can provide the Payers with a random selection of Redeemer addresses for
them to pay to.

In this test data we will assume there are two Redeemers, called Redeemer-1 and Redeemer-2.

## Step 1 Create multiple MultiBit Redeemer wallets
MultiBit Classic (available for download at https://multibit.org) can be used to generate wallets
with the required Bitcoin addresses and subsequently maintain them.

Redeemer-1 will create their first wallet called `redeemer-1-1.wallet`.
Redeemer-2 will create their first wallet called `redeemer-2-1.wallet`.

These are stored in directories, `myRedeemer-1` and `myRedeemer-2`.

The directory structure will then be:

    myRedeemer-1
       redeemer-1-1.wallet      < MultiBit redeemer-1-1 wallet
       redeemer-1-1.info        < MultiBit redeemer-1-1 wallet info
       redeemer-1-1-data        < MultiBit redeemer-1-1 wallet backups

    myRedeemer-2
       redeemer-2-1.wallet      < MultiBit redeemer-2-1 wallet
       redeemer-2-1.info        < MultiBit redeemer-2-1 wallet info
       redeemer-2-1-data        < MultiBit redeemer-2-1 wallet backups

## Step 2 Create many Bitcoin addresses in the MultiBit wallet
To help obscure the BRIT payments the Redeemers want to have many Bitcoin addresses that the Payers pay to.
In MultiBit Classic this can be done by selecting the number of receiving addresses to make on the
'Request' tab's 'New' button.

In this test example we will add 100 Bitcoin addresses to each of the `redeemer-1-1` and `redeemer-2-1` wallets.

## Step 3 Export the Bitcoin addresses from the Bitcoin wallets and copy to the Matcher.
The Redeemers export all their Bitcoin addresses to files.

You can produce a list of all the Bitcoin addresses in a wallet using the Multibit test utility:
`org.multibit.utils.ListAddresses`, passing it the filename of the wallet you want a listing for.

The Redeemers have two Bitcoin wallets and so they will create two files containing each wallet's Bitcoin addresses as follows:

    myRedeemer-1
      export-to-matcher
        redeemer-1-1.txt

    myRedeemer-2
      export-to-matcher
        redeemer-2-1.txt

The Redeemers put each Bitcoin address on a single row in the export files. (Lines beginning with a '#' are ignored as comments).
The Redeemers then copy the export files to the Matcher machine as follows:

    matcher
      import-from-redeemer
        redeemer-1-1.txt
        redeemer-2-1.txt

## Summary

* The Redeemers have created a Bitcoin wallets using MultiBit Classic.
* The Redeemers have exported the Bitcoin addresses from the wallets to files.
* The Redeemers have copied the files containing the Bitcoin addresses to the Matcher machine.
