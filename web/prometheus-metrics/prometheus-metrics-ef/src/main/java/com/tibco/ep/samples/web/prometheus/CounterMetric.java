//  Copyright (c) 2021-2023 Cloud Software Group, Inc.
//
//  Redistribution and use in source and binary forms, with or without
//  modification, are permitted provided that the following conditions are met:
//
//  1. Redistributions of source code must retain the above copyright notice,
//     this list of conditions and the following disclaimer.
//
//  2. Redistributions in binary form must reproduce the above copyright notice,
//     this list of conditions and the following disclaimer in the documentation
//     and/or other materials provided with the distribution.
//
//  3. Neither the name of the copyright holder nor the names of its contributors
//     may be used to endorse or promote products derived from this software
//     without specific prior written permission.
//
//  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
//  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
//  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
//  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
//  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
//  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
//  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
//  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
//  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
//  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
//  POSSIBILITY OF SUCH DAMAGE.

package com.tibco.ep.samples.web.prometheus;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;

import com.streambase.sb.Schema;
import com.streambase.sb.StreamBaseException;
import com.streambase.sb.Tuple;
import com.streambase.sb.operator.Operator;
import com.streambase.sb.operator.Parameterizable;
import com.streambase.sb.operator.TypecheckException;
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
     * <p>
     * The tuple only has one string field, when a tuple passes through this operator,
     * the counter increases the count of the number of strings with the specified length.
     *
     * @param inputPort the input port that the tuple is from (ports are zero based)
     * @param tuple     the tuple from the given input port
     * @throws StreamBaseException Terminates the application.
     */
    @Override
    public void processTuple(int inputPort, Tuple tuple) throws StreamBaseException {
        if (getLogger().isInfoEnabled()) {
            getLogger().info("operator processing a tuple at input port" + inputPort);
        }
        assert inputPort < inputPorts : MessageFormat.format("inputPort {0} is larger than totally inputport count {1}", inputPort, inputPorts);

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

    @Override
    public void init() throws StreamBaseException {
        super.init();
        outputSchemas = new Schema[outputPorts];
        outputSchemas[0] = getRuntimeOutputSchema(0);
        // register the counter
        stringLengthCounter =
                Registry.getInstance().register(STRING_LENGTH_COUNTER_NAME, "", ICounter.builder().withPropertyType(integerPropertyType));
    }
}
