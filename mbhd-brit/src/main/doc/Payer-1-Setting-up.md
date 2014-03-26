## Payer - 1 - Setting up

## Introduction
This document describes the setup the Payer needs to do.

PREREQUISITES:
The Redeemers have performed the steps in:
+ Redeemer-1-Creating-a-Bitcoin-wallet.md
+ Redeemer-2-Creating-PGP-keys.md

The Matcher needs to have performed the steps in:
+ Matcher-1-Setting-up.md


The steps the Payer needs to perform are:


## 1. Copy the Matcher PGP public key
Copy the file:
    matcher/export-to-payer/matcher-key.asc
    to
    payer/import-from-matcher/matcher-key.asc

Note: this public key will typically be provided in the client application so
      in real life this step would be done automatically.
