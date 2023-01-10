# MLaaS - Azure Computer Vision Image Analysis

## Introduction

This sample project demonstrates calling the [Azure Cognitive Services Computer Vision service](https://azure.microsoft.com/en-us/services/cognitive-services/computer-vision/) from TIBCO ModelOps.  This GitHub repository contains the artifacts of a TIBCO ModelOps Project that can be imported into TIBCO ModelOps to allow you to analyze image data.  The input takes a name and an image file name for image analysis. 

NOTE: Use of Azure Cognitive Services may incur costs in your Azure account.

The basic steps for using this sample are:

1. Clone this GitHub repository locally and Import the project into TIBCO ModelOps.
1. Obtain the needed information from Azure - an Azure Key and Endpoint for Cognitive Services.
1. Modify the azure_vision_model with the key and endpoint.
1. Update the scoring flow and pipeline to use the modified script.
1. Approve and deploy the azure_vision_pipeline in your ModelOps environment to deploy a REST service.
1. Run the example Python script on your machine to test the deployed REST pipeline.

## Clone this Project and Import into TIBCO ModelOps

You will want to clone this GitHub project locally.  Once you have the files locally, you can either:
* Build a project zip file using maven with the pom.xml and azure-vision.xml files provided.

OR
* Create a project in ModelOps and upload the artifacts from the azure-vision directory.

### Creating zip file

The zip file can be built by running maven in the repository directory:
```
mvn install
```
This will create a zip file in the ```target``` folder called azure-vision-1.0.0.zip.  This zip file can be imported into TIBCO ModelOps using the **Import a Project** option on the Projects menu.  See [Managing Projects and Artifacts](https://docs.tibco.com/emp/modelops/1.2.0/doc/html/user/managing-projects-and-artificats.html).

Once you have selected the archive and created the project, you should see the project view with the listed project artifacts in TIBCO ModelOps.

### Upload project artifacts

You can create a project in TIBCO ModelOps and upload all the artifacts from the local azure-vision directory into that created project.  In TIBCO ModelOps, **Create a new project** and then add the artifacts to the newly created project.

You will next need to select the azure_vision_model and set the **Model Properties**:
1. Set the Input Schema to the input_image.avsc file.
1. Set the Output Schema to the output_azure_vision.avsc file.
1. Set the Model Dependency to requirements.txt.
1. Save the model.

## Obtain Information from Azure for Cognitive Services

After getting the project created in TIBCO ModelOps, now you need to get the information to call the Azure Computer Vision analysis service. You need to obtain a valid Azure Key and Endpoint URL for Cognitive Services from Azure. 

The exact steps for you may vary based on what privileges you have in Azure.  To obtain the Key and Endpoint URL from Azure, Microsoft provides the following quick start guide [Quickstart: Create a Cognitive Services resource using the Azure portal](https://docs.microsoft.com/en-us/azure/cognitive-services/cognitive-services-apis-create-account).  Copy the keys and endpoint that are displayed in the "Get the keys for your resource" steps.

## Modify the Python script with the key and endpoint

Back in TIBCO ModelOps, select the azure_vision_model for editing (click or select it).  Modify the azureCognitiveKey and azureCognitiveEndpoint variables with the values obtained in the step above:

```python
azureCognitiveKey = "ENTER_YOUR_KEY_HERE_FROM_AZURE_COGNITIVE_SERVICES"
azureCognitiveEndpoint = "ENTER_YOUR_COGNITIVE_SERVICES_URL_HERE"
```

Scroll down to the lines in the python model for the key and endpoint URL to edit them as seen in this screen shot:

![Edit azure_vision_model](edit-model.png)

After entering your key and endpoint, click the **Save** button to save the model.  

## Update Scoring Flow and Pipeline

Before deploying the pipeline, you need to modify the scoring flow to use the latest revised script from the Sandbox environment which is where the modified script exists.  After that, you will then modify the pipeline to use the updated flow.  Return to the project view and select the azure_vision_flow object.  In the flow editor, select the **Score Data** step and select the **Sandbox** revision in the revision selector next to the model selection:

![Update Scoring Flow](scoring-flow-update.png)

Click **Save** to save the updated azure_vision_flow.  Back in the project view, select azure_vision_pipeline.  In the revision dropdown for the scoring flow, select **Sandbox** revision:

![Update Scoring Pipeline](scoring-pipeline-update.png)

Click **Save** to save the updated azure_vision_pipeline.  Now you are ready to deploy the pipeline and test.

## Approve and Deploy the Azure Image Analysis Pipeline

After making the modifications in the last step, the next step is to **Approve** and **Deploy** the updated pipeline.  If you are still in the edit view of the azure_vision_pipeline, you can select **Approve** and then **Deploy**.  You can also select **Scoring Pipelines** in the Navigation Bar.  

1. Find the azure_vision_pipeline and **Approve** it using the pipeline menu. See [Promoting a Scoring Pipeline](https://docs.tibco.com/emp/modelops/1.2.0/doc/html/user/working-with-scoring-flows-and-pipelines.html#promoting-pipeline)
1. After selecting **Approve**, the pop-up for approving the pipeline for an environment appears.  
1. Select the **Development** environment or another appropriate environment.  
1. Close the approval pop-up dialog.
1. Select **Deploy** and the deployment dialog appears.  See [Pipeline Deployment](https://docs.tibco.com/emp/modelops/1.2.0/doc/html/user/working-with-scoring-flows-and-pipelines.html#pipeline-deployment)
1. In the Deploy dialog, click **Deploy** and the pipeline will be deployed to the environment selected in the approval process.  

After a few seconds, the user interface will switch to the Deployments view where you can see the pipeline is just deployed and starting up. 

## Test the deployed pipeline

An example Python script, tmo_rest_imagelabel.py, is included in this repository and can be used to test out the image labeling analysis by connecting to the REST Request Response pipeline that is deployed.  The script takes parameters which you can see by running the script with the "-h" parameter.
```
python tmo_rest_imagelabel.py -h

usage: tmo_rest_imagelabel.py [-h] [--url URL] [--imagefile IMAGEFILE] [--username [USERNAME]] [--password [PASSWORD]]

This script will authenticate and send the provided image file to the given URL for a TIBCO ModelOps REST Pipeline endpoint.

optional arguments:
  -h, --help            show this help message and exit
  --url URL             (required) Complete URL to REST pipeline. Example: https://azurevisionflow.modelops_domain/azurevisionflow
  --imagefile IMAGEFILE
                        Image file to send to image analysis.
  --username [USERNAME]
                        Username if needed.
  --password [PASSWORD]
                        Password if needed.
```

The URL for the REST Request-Response endpoint will be https://\<subdomains\>.\<modelops-kubernetes-hostname\>/\<endpoint-path-prefix\>.  See [Data Channels-Public Addresses](https://docs.tibco.com/emp/modelops/1.2.0/doc/html/user/data-channels.html#rest-addresses).  If nothing is changed in the pipeline, then the domain_prefix and path_prefix are "azurevisionflow," such that the URL becomes:
```
https://azurevisionflow.modelops_hostname/azurevisionflow
 ```

The following shows an example of using the python script with example parameters.  You may need to include a username and password for your TIBCO ModelOps environment.

```
python tmo_rest_imagelabel.py --url https://azurevisionflow.modelops.domain/azurevisionflow --imagefile 'mountainpic.jpg'

Login URL: https://azurevisionflow.modelops.domain/azurevisionflow/login
Successfully received API Token
{ "comments": "This was a lovely little place walking distance from downtown. Lisa was very responsive. My best Airbnb experience yet!", "id": "683278" }
<Response [200]>
('[{"TagScores":[{"Confidence":0.9993207454681396,"Tag":"outdoor"},{"Confidence":0.9705322980880737,"Tag":"person"},{"Confidence":0.9680513143539429,"Tag":"nature"},{"Confidence":0.9619723558425903,"Tag":"clothing"},{"Confidence":0.9538897275924683,"Tag":"sky"},{"Confidence":0.94560706615448,"Tag":"mountain"},{"Confidence":0.9227025508880615,"Tag":"hiking"},{"Confidence":0.9218982458114624,"Tag":"footwear"},{"Confidence":0.9182416200637817,"Tag":"bedrock"},{"Confidence":0.9060934782028198,"Tag":"shorts"},{"Confidence":0.9031842947006226,"Tag":"geology"},{"Confidence":0.8933262825012207,"Tag":"outcrop"},{"Confidence":0.8431090712547302,"Tag":"formation"},{"Confidence":0.8395050764083862,"Tag":"people"},{"Confidence":0.8291632533073425,"Tag":"cliff"},{"Confidence":0.8002957105636597,"Tag":"ground"},{"Confidence":0.7837000489234924,"Tag":"standing"},{"Confidence":0.547474205493927,"Tag":"rock"},{"Confidence":0.5299331545829773,"Tag":"stone"},{"Confidence":0.469105064868927,"Tag":"vacation"},{"Confidence":0.43242591619491577,"Tag":"hike"}],"CaptionScores":[{"Confidence":0.47984999418258667,"Caption":"a '
 'group of people walking on a path between rocky '
 'cliffs"}],"Name":"mountainpic.jpg"}]')
```

You have successfully deployed the Azure Computer Vision image labeling example to TIBCO ModelOps.  You can include it in other more complex scoring flows.

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
