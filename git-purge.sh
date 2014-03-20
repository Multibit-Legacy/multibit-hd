#!/bin/bash
echo "Removing history for *.war, *.jar, *.class files"

echo "Starting size"
git count-objects -v

echo "Removing history for *.war, *.jar, *.class files"
git filter-branch --force --index-filter 'git rm --cached --ignore-unmatch *.war' --prune-empty --tag-name-filter cat -- --all
git filter-branch --force --index-filter 'git rm --cached --ignore-unmatch *.jar' --prune-empty --tag-name-filter cat -- --all
git filter-branch --force --index-filter 'git rm --cached --ignore-unmatch *.class' --prune-empty --tag-name-filter cat -- --all

echo "Purging refs and garbage collection"
# Purge the backups
rm -Rf .git/refs/original

# Force reflog to expire now (not in the default 30 days)
git reflog expire --expire=now --all

# Prune
git gc --prune=now

# Aggressive garbage collection
git gc --aggressive --prune=now

echo
echo "Ending size (size-pack shows new size in Kb)"
git count-objects -v

# Can't do this in the script - it needs a human to be sure
echo
echo "Now use this command to force the changes into your remote repo (origin)"
echo
echo git push --all origin --force