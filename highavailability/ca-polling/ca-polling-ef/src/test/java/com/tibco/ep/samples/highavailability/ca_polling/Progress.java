/*******************************************************************************
 * Copyright (c) 2018-2023 Cloud Software Group, Inc.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package com.tibco.ep.samples.highavailability.ca_polling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tibco.ep.dtm.management.DtmResults;
import com.tibco.ep.dtm.management.DtmRow;
import com.tibco.ep.dtm.management.IDtmProgress;

/**
 * Display progress of admin command
 *
 */
public class Progress implements IDtmProgress{

    /**
     * Logger
     */
    static final Logger logger = LoggerFactory.getLogger(Progress.class);

    private String output = "";

    /**
     * Get the results of the command
     *
     * @return Output string
     */
    String getOutput() {
        return output;
    }

    @Override
    public void start() {
    }

    @Override
    public void results(DtmResults results) {
        for (DtmRow row : results.getResultSet().getRows()) {
            String out = String.join(", ", (row.getColumns()));
            logger.info("["+results.source()+"] results: "+out);
            output += out;
            output += "\n";
        }
    }

    @Override
    public void status(String source, String message) {
        logger.info("["+source+"] status: "+message);
        output += message;
    }

    @Override
    public void cancel() {
    }

    @Override
    public void complete() {
    }

}
