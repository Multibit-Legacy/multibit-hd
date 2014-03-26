## Matcher - 1 - Setting up

## Introduction
This document describes how the person running the Matcher service sets it up.

PREREQUISITES:
The Redeemers have performed the steps in:
+ Redeemer-1-Creating-a-Bitcoin-wallet.md
+ Redeemer-2-Creating-PGP-keys.md

Specifically:
+ the Redeemers have created export files containing their PGP public keys. (These
  PGP public keys also have the EC public keys in their `Comment` field).
+ These export files have been copied to the Matcher machine.

In this document it will be assumed that there are two separate users that have prepared
export files and copied them to the directory on the Matcher machine as follows:

matcher
  import-from-redeemers
    redeemer1-export.asc
    redeemer2-export.asc


The steps to set up the Matcher service are:

1) Create a directory in which to store the PGP keypairs.
   (The keypairs are kept separate from any other PGP keypairs on the Matcher machine).
2) Create the Matcher PGP keypair that is used by the Payers to encrypt traffic sent to the Matcher.
   Export this PGP keypair so that it can be copied to the Payers' machines.
3) Import the Redeemers' export files containing their PGP public keys.
4) Tidy up
5) Start the Matcher daemon that accepts requests from the Payers.


## 1. Create a GPG directory
Create a directory where your matcher is located as follows:

matcher
   gpg                    < GPG details for Matcher

   import-from-redeemers  < Directory into which Redeemers' public keys have been copied


## 2. Create the PGP keypair that is used by the Payer

Create a terminal/ command line and cd into the gpg directory you created above.

The keyring for the redeemer tests was constructed using GPG.
This is the log of how it was constructed (on a Mac):
The password used to protect the keyrings was 'password' (obviously use a better password for your real
keyrings).

# 2.1 Start the gpg-agent
> gpg-agent --homedir "$(pwd)" --daemon

# 2.2 Generate a new keypair
> gpg --homedir "$(pwd)" --gen-key
gpg: WARNING: unsafe permissions on homedir `/Users/jim/ideaprojects/multibit-hd/mbhd-brit/src/test/resources/matcher/gpg'
gpg (GnuPG/MacGPG2) 2.0.22; Copyright (C) 2013 Free Software Foundation, Inc.
This is free software: you are free to change and redistribute it.
There is NO WARRANTY, to the extent permitted by law.

Please select what kind of key you want:
   (1) RSA and RSA (default)
   (2) DSA and Elgamal
   (3) DSA (sign only)
   (4) RSA (sign only)
Your selection? 1
RSA keys may be between 1024 and 8192 bits long.
What keysize do you want? (2048) 
Requested keysize is 2048 bits   
Please specify how long the key should be valid.
         0 = key does not expire
      <n>  = key expires in n days
      <n>w = key expires in n weeks
      <n>m = key expires in n months
      <n>y = key expires in n years
Key is valid for? (0) 0
Key does not expire at all
Is this correct? (y/N) y
                        
GnuPG needs to construct a user ID to identify your key.

Real name: matcher
Email address: matcher@nowhere.com
Comment:
You selected this USER-ID:
    "matcher <matcher@nowhere.com>"

Change (N)ame, (C)omment, (E)mail or (O)kay/(Q)uit? o
You need a Passphrase to protect your secret key.    

We need to generate a lot of random bytes. It is a good idea to perform
some other action (type on the keyboard, move the mouse, utilize the
disks) during the prime generation; this gives the random number
generator a better chance to gain enough entropy.
We need to generate a lot of random bytes. It is a good idea to perform
some other action (type on the keyboard, move the mouse, utilize the
disks) during the prime generation; this gives the random number
generator a better chance to gain enough entropy.
gpg: /users/jim/ideaprojects/multibit-hd/mbhd-brit/src/test/resources/matcher/gpg/trustdb.gpg: trustdb created
gpg: key 58614CEE marked as ultimately trusted
public and secret key created and signed.

gpg: checking the trustdb
gpg: 3 marginal(s) needed, 1 complete(s) needed, PGP trust model
gpg: depth: 0  valid:   1  signed:   0  trust: 0-, 0q, 0n, 0m, 0f, 1u
pub   2048R/58614CEE 2014-03-25
      Key fingerprint = 1B9E 0C6D 71ED 827B 3327  8012 E688 95DE 5861 4CEE
uid                  matcher <matcher@nowhere.com>
sub   2048R/64B4DEA4 2014-03-25


The new key pair is now created.


# 2.3 List keypairs
> gpg --homedir "$(pwd)" -k
gpg: WARNING: unsafe permissions on homedir `/Users/jim/ideaprojects/multibit-hd/mbhd-brit/src/test/resources/matcher/gpg'
/users/jim/ideaprojects/multibit-hd/mbhd-brit/src/test/resources/matcher/gpg/pubring.gpg
----------------------------------------------------------------------------------------
pub   2048R/58614CEE 2014-03-25
uid                  matcher <matcher@nowhere.com>
sub   2048R/64B4DEA4 2014-03-25

# 2.4 Export the Matcher public key to a file for use later by the Payers
Make a note of the public key identifier for the key you just generated.
In the key above it is "58614CEE"

Export your Payer encryption public key from your keyring using:
> gpg --homedir "$(pwd)" --armor --export 58614CEE > matcher-key.asc
(change the "58614CEE" to your key identifier).

You can check the contents of the output file (making no changes) using
> gpg --dry-run --homedir "$(pwd)" --import matcher-key.asc

Once you are happy with the Matcher public key, create a directory 'export-to-payers' as follows
and copy the matcher-key.asc to it.

Directory structure:

matcher
  import-from-redeemers
  export-to-payers        << Copy the matcher-key.asc to here
  gpg


# 3 Import the Redeemers' export files containing their PGP public keys.
For each of the Redeemer export files import the PGP public keys into the Matcher
keyring as described below.

3.1 In your terminal/ command line make sure you are in the `gpg` folder.

3.2 Import the redeemer's public keys:
>  gpg --homedir "$(pwd)" --import ../import-from-redeemers/redeemer1-export.asc

You should see an output similar to:
gpg: WARNING: unsafe permissions on homedir `/users/jim/ideaprojects/multibit-hd/mbhd-brit/src/test/resources/matcher/gpg'
gpg: key 9DA84ADF: public key "redeemer1.1 (03ff238ee490e687f4d04b3c59e9c69cc8f9abf699e46d5df2a30e3f9cf70514e2) <redeemer1.1@nowhere.com>" imported
gpg: key 9DA08B13: public key "redeemer1.2 (03f266155729dfe103f2a9314da825886b8848554eb14a2d324e5ca7887d7bf131) <redeemer1.2@nowhere.com>" imported
gpg: Total number processed: 2
gpg:               imported: 2  (RSA: 2)

Repeat steps 3.2 for each of the redeemer export files.

3.3 List the contents of the matcher's public keyring:
> gpg --homedir "$(pwd)" -k

You should see something similar to:
gpg: WARNING: unsafe permissions on homedir `/users/jim/ideaprojects/multibit-hd/mbhd-brit/src/test/resources/matcher/gpg'
/users/jim/ideaprojects/multibit-hd/mbhd-brit/src/test/resources/matcher/gpg/pubring.gpg
----------------------------------------------------------------------------------------
pub   2048R/58614CEE 2014-03-25
uid                  matcher <matcher@nowhere.com>
sub   2048R/64B4DEA4 2014-03-25

pub   2048R/E3EEA352 2014-03-25
uid                  redeemer2.1 (039111c0f5a4cb0dafdaf0437cb42e043121bb3269d46b26c70744a6f4bb4dd81d) <redeemer2.1@nowhere.com>
sub   2048R/FFC583E7 2014-03-25

pub   2048R/98361FD7 2014-03-25
uid                  redeemer2.2 (03dc2521e0fcb015647fcdb0881cef72ab393453738dc8bb21af4df9227d4739e1) <redeemer2.2@nowhere.com>
sub   2048R/D323251D 2014-03-25

pub   2048R/9DA84ADF 2014-03-25
uid                  redeemer1.1 (03ff238ee490e687f4d04b3c59e9c69cc8f9abf699e46d5df2a30e3f9cf70514e2) <redeemer1.1@nowhere.com>
sub   2048R/758D857B 2014-03-25

pub   2048R/9DA08B13 2014-03-25
uid                  redeemer1.2 (03f266155729dfe103f2a9314da825886b8848554eb14a2d324e5ca7887d7bf131) <redeemer1.2@nowhere.com>
sub   2048R/50A44D27 2014-03-25


# 4 Tidy up
Delete the S.gpg-agent file in the gpg directory (as it causes problems in Eclipse).
You will see a message:
gpg-agent[4552]: can't connect my own socket: IPC connect call failed
gpg-agent[4552]: this process is useless - shutting down
gpg-agent[4552]: gpg-agent (GnuPG/MacGPG2) 2.0.22 stopped

This is ok.


## 5. Start the Matcher daemon
TODO - Start the Matcher daemon so that incoming Payer requests are dealt with.


# Summary
By performing the tasks in this document you have:
 + constructed a PGP keypair that will be used by the Payers to encrypt their messages to the Matcher.
 + exported the PGP keypair above for copying to the Payers.
 + imported to the Matcher keyring the Redeemers' PGP public keys.
 + started the Matcher daemon service so that it can accept incoming Payer messages.
