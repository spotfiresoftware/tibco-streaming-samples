# Copyright (c) 2022-2023 Cloud Software Group, Inc.
# 
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
# 
# 1. Redistributions of source code must retain the above copyright notice,
#    this list of conditions and the following disclaimer.
# 
# 2. Redistributions in binary form must reproduce the above copyright notice,
#    this list of conditions and the following disclaimer in the documentation
#    and/or other materials provided with the distribution.
# 
# 3. Neither the name of the copyright holder nor the names of its contributors
#    may be used to endorse or promote products derived from this software
#    without specific prior written permission.
# 
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.

## Usage: python tmo_rest_imagelabel.py --url <url> --imagefile <local image file> [--username <username>] [--password <password>]
# python tmo_rest_imagelabel.py --url https://azurevisionflow.modelops_domain/azurevisionflow --imageFile myfile.png

import base64
import requests
import json
import pprint
import argparse

parser = argparse.ArgumentParser(
    description='''This script will authenticate and send the provided image file to the given URL for a TIBCO ModelOps REST Pipeline endpoint.''')
parser.add_argument('--url', help='(required) Complete URL to REST pipeline. Example: https://azurevisionflow.modelops_domain/azurevisionflow')
parser.add_argument('--imagefile', help='Image file to send to image analysis.')
parser.add_argument('--username', nargs='?', help='Username if needed.', default='')
parser.add_argument('--password', nargs='?', help='Password if needed.', default='')

args = parser.parse_args()

## The REST Pipeline Endpoint will be <domain prefix> + <domain of modelops>/<path prefix>
url = args.url
username = args.username
password = args.password

_X_TIBCO_HCA_API_TOKEN = "X-TIBCO-HCA-Data-Channel-Server-API-Token"

def _base64string(source):
    return base64.b64encode(source.encode()).decode()

# login 
# ---------------------
login_url = '/'.join([url, "login"])
token = _base64string(f'{username}:{password}')
basic_auth_header = {'Authorization': f'Basic {token}'}
print("Login URL:", login_url)

response = requests.post(
            login_url,
            headers=basic_auth_header,
            verify=False)

api_token = response.headers[_X_TIBCO_HCA_API_TOKEN]
print("Successfully received API Token")

tibco_hca_api_token_header = {
    _X_TIBCO_HCA_API_TOKEN: 
    f'{api_token}'
}

data_url = '/'.join([url, "data"])
headers = {
    'Accept': 'application/json', 
    'Connection': 'keep-alive',
    'Cache-Control': 'no-cache',
    'Content-Type': 'application/json',
    **tibco_hca_api_token_header}

# read in image file
with open(args.imagefile,'rb') as imgFile:
    imgBase64Str = base64.b64encode(imgFile.read()).decode('utf-8')

# Send one record to test service
# Input to image labeling is {"Name":"image name", "Image" : "base64 encoded bytes for image"}
resp = requests.post(data_url, 
            headers=headers, 
            json = {"Name" : args.imagefile, "Image" : imgBase64Str},
            verify=False)

pprint.pprint(resp)
pprint.pprint(resp.text)
