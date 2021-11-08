//  Copyright (C) 2020, TIBCO Software Inc.
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

import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PrometheusIT {

    public static final String BUILTIN_CPU_IDLE_UTILIZATION_PERCENTAGE = "builtin_cpu_idle_utilization_percentage";

    @Test
    @Ignore
    //FIX THIS: TOZHU SB-51300
    public void test() throws IOException, InterruptedException {
        String prometheus_port = System.getProperty("prometheus.metrics.port");

        int nbWait = 20;
        while (nbWait > 0) {
            URL url = new URL("http://localhost:" + prometheus_port
                + "/api/v1/query?query=" + BUILTIN_CPU_IDLE_UTILIZATION_PERCENTAGE);
            HttpURLConnection httpClient = (HttpURLConnection) url.openConnection();

            httpClient.setRequestMethod("GET");

            int responseCode = httpClient.getResponseCode();
            System.out.println("\nSending 'GET' request to URL: " + url);
            System.out.println("Response Code : " + responseCode);

            try (BufferedReader in = new BufferedReader(
                new InputStreamReader(httpClient.getInputStream()))) {

                StringBuilder response = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    response.append(line);
                }


                String output = response.toString();
                System.out.println("Received: " + output);

                if (output.contains(BUILTIN_CPU_IDLE_UTILIZATION_PERCENTAGE)) {
                    System.out.println("Success: found metric " + BUILTIN_CPU_IDLE_UTILIZATION_PERCENTAGE);
                    return;
                }
            }

            System.out.println("Metric not created. Waiting (remaining: " + nbWait +")...");
            Thread.sleep( 5000);
            nbWait--;
        }

        throw new AssertionError(
            "Could not find metric named " + BUILTIN_CPU_IDLE_UTILIZATION_PERCENTAGE);
    }
}
