FROM ubuntu:18.04

ENV DEBIAN_FRONTEND noninteractive

#RUN sed -i 's|http://us.|http://ir.|g' /etc/apt/sources.list
#RUN sed -i 's|http://archive|http://ir.archive|g' /etc/apt/sources.list
RUN apt-get update
RUN apt-get install -y curl vim nano iputils-ping binutils
RUN apt-get install -y libpq-dev libproj-dev gdal-bin git
RUN apt-get install -y locales tzdata
RUN apt-get install -y rsyslog
RUN apt-get install -y cron
RUN apt-get install -y openjdk-8-jdk
RUN  apt-get install scala -y
RUN apt install apt-transport-https -y
RUN echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list
RUN  apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
RUN apt-get update
RUN apt-get install sbt -y

ADD . /opt/
WORKDIR /opt/

RUN sbt compile
RUN chmod +x server.sh && chmod +x client.sh


