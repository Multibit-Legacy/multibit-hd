## Redeemer - 1 - Creating Bitcoin wallets

## Introduction
The Redeemer needs to generate EC keys and provide the corresponding Bitcoin addresses to the Matcher.
This is so that the Matcher can, in turn, give the Payers a random selection of Redeemer addresses for them to pay to.

In this test data we will assume there are two Redeemers, called redeemer-1 and redeemer-2.
The instructions below are for redeemer-1:


## 1. Create multiple MultiBit redeemer wallets
MultiBit Classic (available for download at https://multibit.org) is used to generate wallets
with the required Bitcoin addresses in.

Using MultiBit Classic create wallets called 'redeemer-1-1.wallet' and 'redeemer-1-2.wallet'.
Store it in a convenient directory, say, `myRedeemer`.


The directory structure will then be:

myRedeemer
   redeemer-1-1.wallet      < MultiBit redeemer-1-1 wallet
   redeemer-1-1.info        < MultiBit redeemer-1-1 wallet info
   redeemer-1-1-data        < MultiBit redeemer-1-1 wallet backups

   redeemer-1-2.wallet      < MultiBit redeemer-1-2 wallet
   redeemer-1-2.info        < MultiBit redeemer-1-2 wallet info
   redeemer-1-2-data        < MultiBit redeemer-1-2 wallet backups


# 2. Create many Bitcoin addresses in the MultiBit wallet
To help obscure the BRIT payments the Redeemers want to have many Bitcoin addresses that the Payers pay to.
In MultiBit Classic this can be done by selecting the number of receiving addresses to make on the
'Request' tab's  'New' button.

In this test example we will add 100 Bitcoin addresses to each of the redeemer-1-1 and redeemer-1-2 wallets.


# 3. Export the Bitcoin addresses from the Bitcoin wallets and copy to the Matcher.
The Redeemer exports all their Bitcoin addresses to files.

To find this currently you need to use an IDE do a System.out.println(wallet.toString()); .

If the Redeemer has 2 Bitcoin wallets then they will create 2 files containing each wallet's Bitcoin addresses.

myRedeemer
  export-to-matcher
    redeemer-1-1.txt
    redeemer-1-2.txt

The Redeemer puts each Bitcoin address on a single row in the export files.
They then copy them to the Matcher machine as follows:

matcher
  import-from-redeemer
    redeemer-1-1.txt
    redeemer-1-2.txt


# Summary
The redeemer has created a Bitcoin wallets using MultiBit classic.
The redeemer has exported the Bitcoin addresses from the wallets to files.
The redeemer has copied the files containing the Bitcoin addresses to the Matcher machine.
