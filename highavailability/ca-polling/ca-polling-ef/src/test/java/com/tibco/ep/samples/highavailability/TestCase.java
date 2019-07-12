package com.tibco.ep.samples.highavailability;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.streambase.sb.StreamBaseException;
import com.streambase.sb.unittest.SBServerManager;
import com.streambase.sb.unittest.ServerManagerFactory;
import com.tibco.ep.testing.framework.Administration;
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
     * @throws IOException unable to create poll directory
     */
    @BeforeClass
    public static void setupServer() throws StreamBaseException, ConfigurationException, InterruptedException, IOException {
        // create a temp directory to poll
        //
        File myTempDir = Files.createTempDirectory("poll").toFile();
        myTempDir.deleteOnExit();
        Map<String, String> subs = new HashMap<>();
        subs.put("PollDirectory", myTempDir.getAbsolutePath().replace("\\","\\\\"));
        Configuration.forFile("engine.conf", subs).load().activate();

        // create a StreamBase server and load modules once for all tests in this class
        server = ServerManagerFactory.getEmbeddedServer();
        server.startServer();
        server.loadApp("com.tibco.ep.samples.highavailability.ca-polling-ef");
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

        // Setup test framework before running tests
        this.initialize();
    }

    /**
     * test case
     */
    @Test
    public void test1() {
        LOGGER.info("Test Case 1");

        Administration admin = new Administration();

        LOGGER.info(admin.execute("display", "adapter").toString());
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
        this.complete();

        // after each test, dispose of the container instances
        server.stopContainers();

    }
}
