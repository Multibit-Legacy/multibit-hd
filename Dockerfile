# docker build -t mbhd-buildenv .
# docker run -i -t -v $(pwd):/multibit-hd mbhd-buildenv /bin/sh -c "cd /multibit-hd; mvn clean install"
# java -jar mbhd-swing/target/multibit-hd.jar
FROM fedora:21
RUN yum install -y deltarpm yum-plugin-fastestmirror
RUN yum update -y
RUN yum install -y maven
