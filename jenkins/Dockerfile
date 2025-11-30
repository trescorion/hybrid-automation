FROM jenkins/jenkins:lts

USER root

# Java 17 kurulumu
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk curl unzip && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Java 17'yi otomatik bul (ARM64/AMD64 uyumlu)
RUN update-alternatives --install /usr/bin/java java /usr/lib/jvm/java-17-openjdk-*/bin/java 1 && \
    update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/java-17-openjdk-*/bin/javac 1

# JAVA_HOME'u otomatik bul
ENV JAVA_HOME=$(readlink -f /usr/bin/java | sed "s:bin/java::")
ENV PATH="${JAVA_HOME}bin:${PATH}"

# Plugin'leri önceden kur
RUN jenkins-plugin-cli --plugins \
    configuration-as-code \
    pipeline-stage-view \
    workflow-aggregator \
    gradle \
    htmlpublisher

USER jenkins
