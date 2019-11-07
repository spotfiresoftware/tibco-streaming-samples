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
# Dockerfile for StreamBase generic base image
#
FROM centos:7

LABEL description="TIBCO Streaming base image"
LABEL PRODUCT_VERSION=${docker_base_version}

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
# gdb       required for crash dumps
# java      required for applications
# zip       required for snapshots
# unzip     required to unpack runtime on windows
# less      to aid log file reading
#
RUN yum --assumeyes install \
    sysstat \
    gdb \
    java-1.8.0-openjdk-headless \
    zip \
    unzip \
    less \
    && yum clean all

#
# Create a user to run nodes
#
RUN /usr/sbin/useradd \
    --comment "TIBCO Streaming User" \
    --create-home \
    --user-group \
    --password ${USER_NAME} \
    --shell /bin/bash \
    ${USER_NAME}

#
# Install runtime
#
RUN mkdir -p ${PRODUCT_HOME}
COPY maven ${PRODUCT_HOME}
RUN chmod a+x ${PRODUCT_HOME}/start-node
RUN ${docker.run.unpack}

#
# Allow user access to nodes and applications
#
# required for openshift permissions - see https://docs.okd.io/latest/creating_images/guidelines.html#openshift-specific-guidelines
#
RUN mkdir -p ${RUNTIME_HOME} && chown ${USER_NAME}:${USER_NAME} ${RUNTIME_HOME} && chmod -R g=u /etc/passwd && chgrp -R 0 ${RUNTIME_HOME} && chmod -R g=u ${RUNTIME_HOME}

#
# Change to guest user
#
USER ${USER_NAME}
