## Creation og PGP keys

## Introduction
This document describes how the redeemer sets up everything for BRIT.
The steps are:

1) Create a MultiBit Classic wallet containing the EC private keys you need for VRIT
2) Create the PGP keys that are used to protect the Redeemer's secrets. You add the
   EC public keys into the comment field of the PGP keys when you create them.
3) Export the PGP keys to an armored ASCII file.
4) The armored ASCII file can then be copied to the Matcher machine and imported into
   the Matcher's key ring.
   

## Details
The keyring for the redeemer tests was constructed using GPG.

This is the log of how it was constructed (on a Mac):
The password used was 'password'.

# Start the gpg-agent
> gpg-agent --homedir "$(pwd)" --daemon

# Generate a new keypair
> gpg --homedir "$(pwd)" --gen-key
gpg: WARNING: unsafe permissions on homedir `/Users/jim/ideaprojects/multibit-hd/mbhd-brit/src/test/resources/redeemer/gpg'
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

Real name: redeemer4
Email address: redeemer4@nowhere.com
Comment: 032ce746e4fbf75c0b0b2364f054b7a917d9567509a802c27adb1dc91ab21a07c2
You selected this USER-ID:
    "redeemer4  (032ce746e4fbf75c0b0b2364f054b7a917d9567509a802c27adb1dc91ab21a07c2) <redeemer4@nowhere.com>"

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
gpg: key 8C394BFD marked as ultimately trusted
public and secret key created and signed.

gpg: checking the trustdb
gpg: 3 marginal(s) needed, 1 complete(s) needed, PGP trust model
gpg: depth: 0  valid:   4  signed:   0  trust: 0-, 0q, 0n, 0m, 0f, 4u
pub   2048R/8C394BFD 2014-03-24
      Key fingerprint = 4ACF 793A 9E37 806F 4E89  AEAE 75AA FBA9 8C39 4BFD
uid                  redeemer4 (032ce746e4fbf75c0b0b2364f054b7a917d9567509a802c27adb1dc91ab21a07c2) <redeemer4@nowhere.com>
sub   2048R/67326E94 2014-03-24

# Key pair is now created

# List keypairs
> gpg --homedir "$(pwd)" -k
gpg: WARNING: unsafe permissions on homedir `/Users/jim/ideaprojects/multibit-hd/mbhd-brit/src/test/resources/redeemer/gpg'
/Users/jim/ideaprojects/multibit-hd/mbhd-brit/src/test/resources/redeemer/gpg/pubring.gpg
-----------------------------------------------------------------------------------------
pub   2048R/9200097B 2014-03-23
uid                  redeemer1 <redeemer1@nowhere.com>    << IGNORE
sub   2048R/822D18D2 2014-03-23

pub   2048R/CFA34F33 2014-03-24
uid                  redeemer2 <redeemer2@nowhere.com>    << IGNORE
sub   2048R/76508849 2014-03-24

pub   2048R/B3F52657 2014-03-24
uid                  redeemer3 (03b5fcda72f7177e396ad72978985b28320050cf1ece5983d55cfb9bd6d490468d) <redeemer3@nowhere.com>
sub   2048R/65B5A2F0 2014-03-24

pub   2048R/8C394BFD 2014-03-24
uid                  redeemer4 (032ce746e4fbf75c0b0b2364f054b7a917d9567509a802c27adb1dc91ab21a07c2) <redeemer4@nowhere.com>
sub   2048R/67326E94 2014-03-24

# Export the public keys to a file
You can export all your public keys from your keyring using:
>gpg --homedir "$(pwd)" --armor --export > export.asc

You can check the contents of the output file (making no changes) using
>gpg --dry-run --homedir "$(pwd)" --import export.asc