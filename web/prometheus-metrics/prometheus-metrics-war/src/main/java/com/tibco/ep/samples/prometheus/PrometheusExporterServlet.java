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

package com.tibco.ep.samples.prometheus;

import com.codahale.metrics.MetricRegistry;
import com.kabira.platform.Transaction;
import com.tibco.ep.dtm.metrics.api.IMetric;
import com.tibco.ep.dtm.metrics.dtm.MetricConstants;
import com.tibco.ep.dtm.metrics.reporter.Metrics;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.dropwizard.samplebuilder.CustomMappingSampleBuilder;
import io.prometheus.client.dropwizard.samplebuilder.MapperConfig;
import io.prometheus.client.dropwizard.samplebuilder.SampleBuilder;
import io.prometheus.client.servlet.jakarta.exporter.MetricsServlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrometheusExporterServlet extends MetricsServlet {

    private static final long serialVersionUID = 1L;

    public PrometheusExporterServlet() {
        initialize();
    }

    public PrometheusExporterServlet(CollectorRegistry registry) {
        super(registry);
        initialize();
    }

    private void initialize() {

        final List<MapperConfig> configs = new ArrayList<>();
        new Transaction("parse properties to prometheus labels") {
            @Override
            protected void run() {
                for (final String metricName : Metrics.getInstance().getAllMetrics().keySet()) {
                    final IMetric metric = Metrics.getInstance().getAllMetrics().get(metricName);
                    // if metric has properties, we parse it to a MapperConfig
                    if (!metric.getProperties().isEmpty()) {
                        final MapperConfig config = new MapperConfig();
                        final Map<String, String> labels = new HashMap<>();
                        final StringBuilder nameTemplate = new StringBuilder(metric.getName());
                        for (int i = 0; i < metric.getPropertyTypes().size(); i++) {
                            nameTemplate.append(MetricConstants.NAME_SEPARATOR).append("*");
                            labels.put(metric.getPropertyTypes().get(i).getName(), "${" + i + "}");
                        }
                        config.setName(metricName);
                        config.setMatch(nameTemplate.toString());
                        config.setLabels(labels);
                        configs.add(config);
                    }
                }
            }
        }.execute();
        MetricRegistry metricRegistry = Metrics.getInstance().getMetricRegistry();

        // if no metric has property, we don't pass it to DropwizardExports,
        if (configs.isEmpty()) {
            CollectorRegistry.defaultRegistry.register(new DropwizardExports(metricRegistry).register());

            // if any metric has property, we create a CustomMappingSampleBuilder and pass it to DropwizardExports
        } else {
            final SampleBuilder sampleBuilder = new CustomMappingSampleBuilder(configs);
            CollectorRegistry.defaultRegistry.register(new DropwizardExports(metricRegistry, sampleBuilder).register());
        }
    }
}
