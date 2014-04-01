## Payer - 1 - Setting up

## Introduction
This document describes the setup the Payer needs to do.

## Prerequisites

The Redeemers have performed the steps in [Creating Bitcoin wallets](Redeemer-1-Creating-Bitcoin-wallets.md).

The Matcher has performed the step in [Setting Up](Matcher-1-Setting-up.md).

The steps the Payer needs to perform are:

## Step 1 Copy the Matcher PGP public key
Copy the file `matcher/export-to-payer/matcher-key.asc` to `payer/import-from-matcher/matcher-key.asc`

Note: this public key will typically be provided in the client application so in real life this step would be done automatically.
