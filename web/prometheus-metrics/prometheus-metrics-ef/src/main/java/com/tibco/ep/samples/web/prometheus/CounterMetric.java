package com.tibco.ep.samples.web.prometheus;

import java.util.Collection;
import java.util.HashSet;

import com.streambase.sb.*;
import com.streambase.sb.operator.*;
import com.tibco.ep.dtm.metrics.api.ICounter;
import com.tibco.ep.dtm.metrics.api.Property;
import com.tibco.ep.dtm.metrics.api.PropertyType;
import com.tibco.ep.dtm.metrics.api.Registry;

/**
 * This class is used as a CounterMetric Java Operator in a StreamBase application.
 * It registers a Counter when application starts, the counter records the count of string
 * in different length.
 */
public class CounterMetric extends Operator implements Parameterizable {

    public static final long serialVersionUID = 1620840540942L;
    // Local variables
    private final int inputPorts = 1;
    private final int outputPorts = 1;
    private int nextOutputPort = 0;
    private Schema[] outputSchemas; // caches the Schemas given during init() for use at processTuple()

    private static final String STRING_LENGTH_COUNTER_NAME = "string.count";
    private static final String LENGTH_PROPERTY_NAME = "length";

    private final PropertyType<Integer> integerPropertyType = PropertyType.builder(LENGTH_PROPERTY_NAME, Integer.class).build();
    private ICounter stringLengthCounter;

    /**
     * Constructor
     */
    public CounterMetric() {
        super();
        setPortHints(inputPorts, outputPorts);
        setDisplayName(this.getClass().getSimpleName());
        setShortDisplayName(this.getClass().getSimpleName());

    }

    public void typecheck() throws TypecheckException {
        requireInputPortCount(inputPorts);

        for (int i = 0; i < outputPorts; ++i) {
            setOutputSchema(i, getInputSchema(0));
        }
    }

    /**
     * This method will be called by the StreamBase server for each Tuple given to
     * the Operator to process. This is the only time an operator should enqueue
     * output Tuples.
     *
     * @param inputPort the input port that the tuple is from (ports are zero based)
     * @param tuple     the tuple from the given input port
     * @throws StreamBaseException Terminates the application.
     */
    public void processTuple(int inputPort, Tuple tuple) throws StreamBaseException {
        if (getLogger().isInfoEnabled()) {
            getLogger().debug("operator processing a tuple at input port" + inputPort);
        }
        if (inputPort > 0) {
            getLogger().debug("operator skipping tuple at input port" + inputPort);
            return;
        }

        // if the counter is null, then register one
        if (stringLengthCounter == null) {
            stringLengthCounter =
                    Registry.getInstance().register(STRING_LENGTH_COUNTER_NAME, "", null, ICounter.builder().withPropertyType(integerPropertyType));
        }

        Tuple out = outputSchemas[inputPort].createTuple();

        final Collection<Property<?>> properties = new HashSet<>();
        for (int i = 0; i < out.getSchema().getFieldCount(); ++i) {
            properties.clear();
            properties.add(Property.builder(integerPropertyType).withValue(tuple.getString(i).length()).build());

            // increase the count according to the string length
            stringLengthCounter.increment(stringLengthCounter.createSample(properties));
            out.setField(i, tuple.getField(i));
        }

        sendOutput(nextOutputPort, out);
        nextOutputPort = (nextOutputPort + 1) % outputPorts;
    }

    public void init() throws StreamBaseException {
        super.init();
        outputSchemas = new Schema[outputPorts];

        for (int i = 0; i < outputPorts; ++i) {
            outputSchemas[i] = getRuntimeOutputSchema(i);
        }
    }
}
