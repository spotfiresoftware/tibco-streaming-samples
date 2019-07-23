/*******************************************************************************
 * Copyright (C) 2018-2019, TIBCO Software Inc.
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
package com.tibco.ep.samples.nativelibrary.nareventflow;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.streambase.sb.StreamBaseException;
import com.streambase.sb.unittest.Expecter;
import com.streambase.sb.unittest.JSONSingleQuotesTupleMaker;
import com.streambase.sb.unittest.SBServerManager;
import com.streambase.sb.unittest.ServerManagerFactory;
import com.tibco.ep.samples.nativelibrary.narcpplib.CallCpp;
import com.tibco.ep.testing.framework.Configuration;
import com.tibco.ep.testing.framework.ConfigurationException;
import com.tibco.ep.testing.framework.TransactionalDeadlockDetectedException;
import com.tibco.ep.testing.framework.TransactionalMemoryLeakException;
import com.tibco.ep.testing.framework.UnitTest;

/**
 * Example test case
 */
public class TestCase extends UnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestCase.class);

    private static SBServerManager server;

    /**
     * Set up the server
     *
     * @throws StreamBaseException on start server error
     * @throws ConfigurationException on configuration failure
     * @throws InterruptedException on start server error
     */
    @BeforeClass
    public static void setupServer() throws StreamBaseException, ConfigurationException, InterruptedException {
        // Example configuration load
        Configuration.forFile("engine.conf").load().activate();

        // create a StreamBase server and load modules once for all tests in this class
        server = ServerManagerFactory.getEmbeddedServer();
        server.startServer();
        server.loadApp("com.tibco.ep.samples.nativelibrary.nareventflow.NarEventFlow");
    }

    /**
     * Stop the server
     *
     * @throws StreamBaseException on shutdown failure
     * @throws InterruptedException on shutdown failure
     */
    @AfterClass
    public static void stopServer() throws InterruptedException, StreamBaseException {
        try {
            assertNotNull(server);
            server.shutdownServer();
            server = null;
        } finally {
            Configuration.deactiveAndRemoveAll();
        }
    }

    /**
     * Start the containers
     *
     * @throws StreamBaseException on start container error
     */
    @Before
    public void startContainers() throws StreamBaseException {
        // before each test, startup fresh container instances
        server.startContainers();
        clearCppVar();

        // Setup test framework before running tests
        initialize();
    }

    /** the static C++ variable doesn't get reset when the SB container is reset so we need to do it explicitly */
    private void clearCppVar() {
        CallCpp.setCppInt(0);
    }
    
    /**
     * Test set
     * 
     * @throws StreamBaseException Test failed
     */
    @Test
    public void testSet() throws StreamBaseException {
    	LOGGER.info("Running testSet");
    	
        server.getEnqueuer("set").enqueue(JSONSingleQuotesTupleMaker.MAKER,
                "{'i':1}", "{'i':2}");
        new Expecter(server.getDequeuer("setOut")).expect(
                JSONSingleQuotesTupleMaker.MAKER, 
                "{'i':1,'oldVal':0}",
                "{'i':2,'oldVal':1}");
    }

    /**
     * Test increment
     * 
     * @throws StreamBaseException Test failed
     */
    @Test
    public void testInc() throws StreamBaseException  {
    	LOGGER.info("Running testInc");

        server.getEnqueuer("increment").enqueue(JSONSingleQuotesTupleMaker.MAKER,
                "{'i':1}", "{'i':1}");
        new Expecter(server.getDequeuer("incOut")).expect(
                JSONSingleQuotesTupleMaker.MAKER, 
                "{'i':1}",
                "{'i':2}");
        server.getEnqueuer("increment").enqueue(JSONSingleQuotesTupleMaker.MAKER,
                "{'i':2}", "{'i':2}");
        new Expecter(server.getDequeuer("incOut")).expect(
                JSONSingleQuotesTupleMaker.MAKER, 
                "{'i':4}",
                "{'i':6}");
    }

    /**
     * Test set and increment
     * 
     * @throws StreamBaseException Test failed
     */
    @Test
    public void testSetAndIncrement() throws StreamBaseException {
    	LOGGER.info("Running testSetAndIncrement");
    	
        server.getEnqueuer("set").enqueue(JSONSingleQuotesTupleMaker.MAKER,
                "{'i':1}", "{'i':2}");
        new Expecter(server.getDequeuer("setOut")).expect(
                JSONSingleQuotesTupleMaker.MAKER, "{'i':1,'oldVal':0}",
                "{'i':2,'oldVal':1}");
        server.getEnqueuer("increment").enqueue(
                JSONSingleQuotesTupleMaker.MAKER, "{'i':1}", "{'i':1}",
                "{'i':1}", "{'i':2}", "{'i':2}");
        new Expecter(server.getDequeuer("incOut")).expect(
                JSONSingleQuotesTupleMaker.MAKER, "{'i':3}", "{'i':4}",
                "{'i':5}", "{'i':7}", "{'i':9}");
    }

    /**
     * Stop containers
     *
     * @throws StreamBaseException on stop container error
     * @throws TransactionalMemoryLeakException Leak detected
     * @throws TransactionalDeadlockDetectedException Deadlock detected
     */
    @After
    public void stopContainers() throws StreamBaseException, TransactionalMemoryLeakException, TransactionalDeadlockDetectedException {
        // Complete test framework and check for any errors
        complete();

        // after each test, dispose of the container instances
        server.stopContainers();
    }
}
