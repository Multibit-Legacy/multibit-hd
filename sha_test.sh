#!/bin/sh
openssl sha1 target/multibit-hd-0.0.1-SNAPSHOT.jar > sha1_$1_jar.txt
mkdir target/multibit-hd-0.0.1-SNAPSHOT
cd target/multibit-hd-0.0.1-SNAPSHOT
jar xf ../multibit-hd-0.0.1-SNAPSHOT.jar
cd ../..
find ./target/multibit-hd-0.0.1-SNAPSHOT -type f -print0 | xargs -0 openssl sha1 > sha1_$1_all.txt