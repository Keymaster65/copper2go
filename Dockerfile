# docker build --tag docker-in-docker-jenkins .
# call if needed for docker:
# docker exec --user root -ti jenkins bash -c '
FROM jenkins/jenkins:lts
USER root
RUN curl -sSL https://get.docker.com/ | sh
USER jenkins
