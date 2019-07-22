#  Copyright (C) 2018-2019, TIBCO Software Inc.
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
FROM centos:7 AS build

###Note: LABEL statement build by Ansible playbook
LABEL build-image=233136

#
# Set environment
#
ENV PRODUCT_HOME     /opt/tibco/streambase

RUN yum -y install unzip

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

FROM centos:7

LABEL description="StreamBase Base"

###Note: LABEL statement build by Ansible playbook
LABEL build-image=233136

#
# Set environment
#
ENV PRODUCT_HOME     /opt/tibco/streambase
ENV RUNTIME_HOME     /var/opt/tibco/streambase
ENV JAVA_HOME        /etc/alternatives/jre
ENV PATH             /bin:/usr/sbin:${PRODUCT_HOME}/distrib/tibco/bin
ENV USER_NAME        tibco

#
# Add required additional packages
#
# systat    required for runtime statistcs
# java      required for applications
# zip       required for snapshots
# unzip     required to unpack runtime
#
RUN yum --assumeyes install \
    sysstat \
    java-1.8.0-openjdk-headless \
    zip \
    unzip \
    && yum clean all \
    && rm -rf /var/cache/yum \
    && rm -rf /var/tmp/yum-*     
#
# Create a user to run nodes
#
RUN /usr/sbin/useradd \
    --comment "TIBCO StreamBase Guest User" \
    --create-home \
    --user-group \
    --password ${USER_NAME} \
    --shell /bin/bash \
    ${USER_NAME}

#
# Allow user access to nodes and applications
#
RUN mkdir -p ${RUNTIME_HOME} \
    && mkdir -p ${RUNTIME_HOME}\application \
    &&  mkdir -p ${RUNTIME_HOME}\configuration

RUN chown ${USER_NAME}:${USER_NAME} ${RUNTIME_HOME}

#
#### copy ${PRODUCT_HOME) dir from build image
#
COPY --from=build ${PRODUCT_HOME} ${PRODUCT_HOME}

#
# Change to guest user
#
USER ${USER_NAME}


