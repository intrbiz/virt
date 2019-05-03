FROM opensuse/leap:15
MAINTAINER Chris Ellis <chris@intrbiz.com>

# Install the base JDK
RUN zypper -q -n in java-11-openjdk-devel
RUN zypper -q -in in tar

# Setup some common directories
RUN mkdir -p /etc/virt
RUN mkdir -p /opt/virt/hostd

# Volume for configuration
VOLUME /etc/virt

# The current version
ARG version=0.0.1-SNAPSHOT

# Add the application
WORKDIR /opt/virt/host
ADD virt-hostd-${version}.app /opt/virt/hostd/virt-hostd.app

# Extract the application
RUN java -Dbootstrap.extract.only=true -jar virt-hostd.app

# Ports
EXPOSE 8888

# The run command
ENTRYPOINT ["java", "-Dbootstrap.extract=false", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/opt/virt/hostd/virt-hostd.app"]
