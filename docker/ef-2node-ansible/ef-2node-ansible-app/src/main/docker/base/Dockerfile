#  Copyright (c) 2018-2023 Cloud Software Group, Inc.
#  
#  Redistribution and use in source and binary forms, with or without
#  modification, are permitted provided that the following conditions are met:
#  
#  1. Redistributions of source code must retain the above copyright notice,
#     this list of conditions and the following disclaimer.
#  
#  2. Redistributions in binary form must reproduce the above copyright notice,
#     this list of conditions and the following disclaimer in the documentation
#     and/or other materials provided with the distribution.
#  
#  3. Neither the name of the copyright holder nor the names of its contributors
#     may be used to endorse or promote products derived from this software
#     without specific prior written permission.
#  
#  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
#  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
#  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
#  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
#  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
#  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
#  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
#  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
#  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
#  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
#  POSSIBILITY OF SUCH DAMAGE.


#
### Build temporary image in multi-stage docker image build process
#
FROM almalinux:8 AS build

###Note: LABEL statement build by Ansible playbook
LABEL build-image=0.0.0

#
# Set environment
#
ENV PRODUCT_HOME     /opt/tibco/streambase

RUN dnf --assumeyes install unzip

#
# Install runtime
#

RUN mkdir -p ${PRODUCT_HOME}
COPY maven/start-node ${PRODUCT_HOME}
COPY maven/platform_linuxx86_64.zip /tmp
RUN unzip /tmp/platform_linuxx86_64.zip -d ${PRODUCT_HOME}
RUN chmod a+x ${PRODUCT_HOME}/start-node

#
### Build base image
#

FROM almalinux:8

LABEL description="Spotfire Streaming base image"
LABEL PRODUCT_VERSION=${docker_base_version}

###Note: LABEL statement build by Ansible playbook
LABEL build-image=0.0.0

#
# Set environment
#
ENV STREAMING_PRODUCT_HOME     /opt/tibco/streambase
ENV STREAMING_RUNTIME_HOME     /var/opt/tibco/streambase
ENV JAVA_HOME        /etc/alternatives/jre
ENV PATH             /bin:/usr/sbin:${STREAMING_PRODUCT_HOME}/distrib/tibco/bin
ENV USER_NAME        tibco

#
# Add required additional packages
#
# systat    required for runtime statistcs
# gdb       required for crash dumps
# java      required for applications
# zip       required for snapshots
# unzip     required to unpack runtime
# libnsl    required by epadmin
# less      to aid log file reading
#
RUN echo 'fastestmirror=1' >> /etc/dnf/dnf.conf && dnf --assumeyes update && dnf --setopt=install_weak_deps=False --best --assumeyes --exclude=gcc --exclude=binutils install \
    sysstat \
    gdb \
    zip \
    unzip \
    libnsl \
    less \
    java-11-openjdk-headless \
    iproute \
    && dnf clean all

#
# Create a user to run nodes
#
RUN /usr/sbin/useradd \
    --comment "Spotfire Streaming User" \
    --create-home \
    --user-group \
    --password ${USER_NAME} \
    --shell /bin/bash \
    ${USER_NAME}

#
# Allow user access to nodes and applications
#
RUN mkdir -p ${STREAMING_RUNTIME_HOME} \
    && mkdir -p ${STREAMING_RUNTIME_HOME}\application \
    &&  mkdir -p ${STREAMING_RUNTIME_HOME}\configuration

RUN chown ${USER_NAME}:${USER_NAME} ${STREAMING_RUNTIME_HOME}

#
#### copy ${STREAMING_PRODUCT_HOME) dir from build image
#
COPY --from=build ${STREAMING_PRODUCT_HOME} ${STREAMING_PRODUCT_HOME}

#
# Change to guest user
#
USER ${USER_NAME}

