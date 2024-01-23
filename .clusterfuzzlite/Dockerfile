FROM ghcr.io/tdrozdovsky/base-builder-jvm

RUN curl -L https://archive.apache.org/dist/maven/maven-3/3.8.6/binaries/apache-maven-3.8.6-bin.zip -o maven.zip && \
    unzip maven.zip -d $SRC/maven && \
    rm -rf maven.zip

ENV MVN $SRC/maven/apache-maven-3.8.6/bin/mvn

# RUN apt update && apt install -y openjdk-17-jdk

RUN rm -rf $SRC/lpvs

RUN git clone --depth 1 https://github.com/tdrozdovsky/lpvs

COPY . $SRC

#COPY build.sh $SRC/
WORKDIR $SRC

# WORKDIR clusterfuzzlite-example
COPY .clusterfuzzlite/build.sh $SRC
