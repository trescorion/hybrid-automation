FROM jenkins/jenkins:lts

USER root

# Plugin'leri önceden kur
RUN jenkins-plugin-cli --plugins \
    configuration-as-code \
    pipeline-stage-view \
    workflow-aggregator \
    gradle \
    htmlpublisher

USER jenkins
