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

Real name: redeemer1
Email address: redeemer1@nowhere.com
Comment:                            
You selected this USER-ID:
    "redeemer1 <redeemer1@nowhere.com>"

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
gpg: /Users/jim/ideaprojects/multibit-hd/mbhd-brit/src/test/resources/redeemer/gpg/trustdb.gpg: trustdb created
gpg: key 9200097B marked as ultimately trusted
public and secret key created and signed.

gpg: checking the trustdb
gpg: 3 marginal(s) needed, 1 complete(s) needed, PGP trust model
gpg: depth: 0  valid:   1  signed:   0  trust: 0-, 0q, 0n, 0m, 0f, 1u
pub   2048R/9200097B 2014-03-23
      Key fingerprint = 223D 552E BC17 C785 6F81  100A AF38 2FDB 9200 097B
uid                  redeemer1 <redeemer1@nowhere.com>
sub   2048R/822D18D2 2014-03-23

# Key pair is now created

# List keypair
> gpg --homedir "$(pwd)" -k
gpg: WARNING: unsafe permissions on homedir `/Users/jim/ideaprojects/multibit-hd/mbhd-brit/src/test/resources/redeemer/gpg'
/Users/jim/ideaprojects/multibit-hd/mbhd-brit/src/test/resources/redeemer/gpg/pubring.gpg
-----------------------------------------------------------------------------------------
pub   2048R/9200097B 2014-03-23
uid                  redeemer1 <redeemer1@nowhere.com>
sub   2048R/822D18D2 2014-03-23