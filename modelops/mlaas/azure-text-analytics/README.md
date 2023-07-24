# MLaaS - Azure Cognitive Service for Language Sentiment Analysis

## Introduction

This sample project demonstrates calling the [Azure Cognitive Service for Language](https://azure.microsoft.com/en-us/products/cognitive-services/language-service/#overview) to perform [Sentiment Analysis using Text Analytics](https://learn.microsoft.com/en-us/azure/synapse-analytics/machine-learning/tutorial-cognitive-services-sentiment) from Spotfire ModelOps.  This GitHub repository contains the artifacts of a Spotfire ModelOps Project that can be imported into Spotfire ModelOps to allow you to analyze text data for sentiment.  The input takes an id and a comment for sentiment analysis. 

NOTE: Use of Azure Cognitive Services may incur costs in your Azure account.

The basic steps for using this sample are:

1. Clone this GitHub repository locally and Import the project into Spotfire ModelOps.
1. Obtain the needed information from Azure - an Azure Key and Endpoint for Cognitive Services.
1. Modify the azure_text_analytics_model file with the key and endpoint.
1. Update the scoring flow and pipeline to use the modified script.
1. Approve and deploy the azure_text_analytics_pipeline in your ModelOps environment to deploy a REST service.
1. Run the example Python script on your machine to test the deployed REST pipeline.

## Clone this Project and Import into Spotfire ModelOps

You will want to clone this GitHub project locally.  Once you have the files locally, you can either:
* Build a project zip file using maven with the pom.xml and azure-text-analytics.xml files provided.

OR
* Create a project in ModelOps and upload the artifacts from the azure-text-analytics directory.

### Creating zip file

The zip file can be built by running maven in the repository directory:
```
mvn install
```
This will create a zip file in the ```target``` folder called azure-text-analytics-1.0.0.zip.  This zip file can be imported into Spotfire ModelOps using the **Import a Project** option on the Projects menu.  See [Managing Projects and Artifacts](https://docs.tibco.com/emp/modelops/1.2.0/doc/html/user/managing-projects-and-artificats.html).

Once you have selected the archive and created the project, you should see the project view with the listed project artifacts in Spotfire ModelOps.

### Upload project artifacts

You can create a project in Spotfire ModelOps and upload all the artifacts from the local azure-text-analytics directory into that created project.  In Spotfire ModelOps, **Create a new project** and then add the artifacts to the newly created project.

You will next need to select the azure_text_analytics_model and set the **Model Properties**:
1. Set the Input Schema to the input_sentiment.avsc file.
1. Set the Output Schema to the output_azure_text_analytics.avsc file.
1. Set the Model Dependency to requirements.txt.
1. Save the model.

## Obtain Information from Azure for Cognitive Services

After getting the project created in Spotfire ModelOps, now you need to get the information to call the Azure Sentiment Cognitive analysis service. You need to obtain a valid Azure Key and Endpoint URL for Cognitive Services from Azure. 

The exact steps for you may vary based on what privileges you have in Azure.  To obtain the Key and Endpoint URL from Azure, Microsoft provides the following quick start guide [Quickstart: Create a Cognitive Services resource using the Azure portal](https://docs.microsoft.com/en-us/azure/cognitive-services/cognitive-services-apis-create-account).  Copy the keys and endpoint that are displayed in the "Get the keys for your resource" steps.

## Modify the Python script with the key and endpoint

Back in Spotfire ModelOps, select the azure_text_analytics_model for editing (click or select it).  Modify the azureCognitiveKey and azureCognitiveEndpoint variables with the values obtained in the step above:

```python
azureCognitiveKey = "ENTER_YOUR_KEY_HERE_FROM_AZURE_COGNITIVE_SERVICES"
azureCognitiveEndpoint = "ENTER_YOUR_COGNITIVE_SERVICES_URL_HERE"
```

Scroll down to the lines in the python model for the key and endpoint URL to edit them as seen in this screen shot:

![Edit azure_text_analytics_model](edit-model.png)

After entering your key and endpoint, click the **Save** button to save the model.  

## Update Scoring Flow and Pipeline

Before deploying the pipeline, you need to modify the scoring flow to use the latest revised script from the Sandbox environment which is where the modified script exists.  After that, you will then modify the pipeline to use the updated flow.  Return to the project view and select the azure_text_analytics_flow object.  In the flow editor, select the **Score Data** step and select the **Sandbox** revision in the revision selector next to the model selection:

![Update Scoring Flow](scoring-flow-update.png)

Click **Save** to save the updated azure_text_analytics_flow.  Back in the project view, select azure_text_analytics_pipeline.  In the revision dropdown for the scoring flow, select **Sandbox** revision:

![Update Scoring Pipeline](scoring-pipeline-update.png)

Click **Save** to save the updated azure_text_analytics_pipeline.  Now you are ready to deploy the pipeline and test.

## Approve and Deploy the Azure Text Analytics Pipeline

After making the modifications in the last step, the next step is to **Approve** and **Deploy** the updated pipeline.  If you are still in the edit view of the azure_text_analytics_pipeline, you can select **Approve** and then **Deploy**.  You can also select **Scoring Pipelines** in the Navigation Bar.  

1. Find the azure_text_analytics_pipeline and **Approve** it using the pipeline menu. See [Promoting a Scoring Pipeline](https://docs.tibco.com/emp/modelops/1.2.0/doc/html/user/working-with-scoring-flows-and-pipelines.html#promoting-pipeline)
1. After selecting **Approve**, the pop-up for approving the pipeline for an environment appears.  
1. Select the **Development** environment or another appropriate environment.  
1. Close the approval pop-up dialog.
1. Select **Deploy** and the deployment dialog appears.  See [Pipeline Deployment](https://docs.tibco.com/emp/modelops/1.2.0/doc/html/user/working-with-scoring-flows-and-pipelines.html#pipeline-deployment)
1. In the Deploy dialog, click **Deploy** and the pipeline will be deployed to the environment selected in the approval process.  

After a few seconds, the user interface will switch to the Deployments view where you can see the pipeline is just deployed and starting up. 

## Test the deployed pipeline

An example Python script, tmo_rest_sentiment.py, is included in this repository and can be used to test out the sentiment analysis by connecting to the REST Request Response pipeline that is deployed.  The script takes parameters which you can see by running the script with the "-h" parameter.
```
python tmo_rest_sentiment.py -h

usage: tmo_rest_sentiment.py [-h] [--url URL] [--input INPUT] [--username [USERNAME]] [--password [PASSWORD]]

This script will authenticate and send JSON input to the given URL for a Spotfire ModelOps REST Pipeline endpoint.

optional arguments:
  -h, --help            show this help message and exit
  --url URL             (required) Complete URL to REST pipeline. Example: https://aztextanalyticsflow.modelops_domain/aztextanalyticsflow
  --input INPUT         (required) JSON input to pipeline, e.g. { "comments": "This was a lovely little place", "id": "683278" }
  --username [USERNAME]
                        Username if needed.
  --password [PASSWORD]
                        Password if needed.
```

The URL for the REST Request-Response endpoint will be https://\<subdomains\>.\<modelops-kubernetes-hostname\>/\<endpoint-path-prefix\>.  See [Data Channels-Public Addresses](https://docs.tibco.com/emp/modelops/1.2.0/doc/html/user/data-channels.html#rest-addresses).  If nothing is changed in the pipeline, then the domain_prefix and path_prefix are "aztextanalyticsflow," such that the URL becomes:
```
https://aztextanalyticsflow.modelops_hostname/aztextanalyticsflow
 ```

The following shows an example of using the python script with example parameters.  You may need to include a username and password for your Spotfire ModelOps environment.

```
python tmo_rest_sentiment.py --url https://aztextanalyticsflow.modelops.domain/aztextanalyticsflow --input "{ 'comments': 'This was a lovely little place walking distance from downtown. Lisa was very responsive. My best Airbnb experience yet!', 'id': '683278' }"

Login URL: https://aztextanalyticsflow.modelops.domain/aztextanalyticsflow/login
Successfully received API Token
{ "comments": "This was a lovely little place walking distance from downtown. Lisa was very responsive. My best Airbnb experience yet!", "id": "683278" }
<Response [200]>
'[{"Neutral":0.01,"Language":"en","Negative":0.01,"Positive":0.98,"id":"683278","Sentiment":"positive"}]'
```

Now you can modify the text input and see how the sentiment changes:
```
python tmo_rest_sentiment.py --url https://aztextanalyticsflow.modelops.domain/aztextanalyticsflow --input "{ 'comments': 'This place was awful.', 'id': '683278' }"

Login URL: https://aztextanalyticsflow.modelops.domain/aztextanalyticsflow/login
Successfully received API Token
{ "comments": "This place was awful.", "id": "683278" }
<Response [200]>
'[{"Neutral":0.99,"Language":"en","Negative":0.0,"Positive":0.01,"id":"683278","Sentiment":"negative"}]'
```

You have successfully deployed the Azure Cognitive Service for Language Sentiment example to Spotfire ModelOps.  You can include it in other more complex scoring flows.

---
Copyright (c) 2022-2023 Cloud Software Group, Inc.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

* Neither the name of the copyright holder nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
