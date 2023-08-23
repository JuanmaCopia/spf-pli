# Build image with:
#   `docker build -t pli .`
# Afterwards run container with:
#   `docker run -it pli:latest /bin/bash`

FROM ubuntu:16.04

RUN apt-get update -y
RUN apt-get install -y build-essential
RUN apt-get install -y git
RUN apt-get install -y ant
RUN apt-get install -y vim
RUN apt-get install -y tree

# Download java 8(!)
RUN apt-get install -y openjdk-8-jdk
RUN mkdir /usr/lib/PLI

WORKDIR /usr/lib/PLI

# Copy projects
COPY ./jpf-core /usr/lib/PLI/jpf-core
COPY ./jpf-symbc /usr/lib/PLI/jpf-symbc
COPY ./spf-pli /usr/lib/PLI/spf-pli

# Setup jpf env
RUN mkdir /root/.jpf
RUN echo 'jpf-core = /usr/lib/PLI/jpf-core' >> /root/.jpf/site.properties
RUN echo 'jpf-symbc = /usr/lib/PLI/jpf-symbc' >> /root/.jpf/site.properties
RUN echo 'spf-pli = /usr/lib/PLI/spf-pli' >> /root/.jpf/site.properties
RUN echo 'extensions=${jpf-core},${jpf-symbc}' >> /root/.jpf/site.properties

# Build projects
WORKDIR /usr/lib/PLI/jpf-core
RUN ant clean
RUN ant build
WORKDIR /usr/lib/PLI/jpf-symbc
RUN ant clean
RUN ant build
WORKDIR /usr/lib/PLI/spf-pli
RUN ant clean
RUN ant build

# Setup path
ENV JPF_HOME=/usr/lib/PLI
ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/
ENV LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$JPF_HOME/spf-pli/lib

WORKDIR /usr/lib/PLI/spf-pli
