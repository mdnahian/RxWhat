import httplib, urllib, base64

def image_to_text(url):
	headers = {
	    # Request headers
	    'Content-Type': 'application/json',
	    'Ocp-Apim-Subscription-Key': 'be6d561231ec489d8593ca3ef59a67ba',
	}

	params = urllib.urlencode({
	    # Request parameters
	    'language': 'unk',
	    'detectOrientation ': 'true',
	})

	try:
	    conn = httplib.HTTPSConnection('api.projectoxford.ai')
	    conn.request("POST", "/vision/v1.0/ocr?%s" % params, '{ "url": "'+url+'" }', headers)
	    response = conn.getresponse()
	    data = response.read()
            
            print data	    
	    
	    d = json.loads(data)
	    regions = d['regions']

	    for region in regions:
	        lines = region['lines']
	        for line in lines:
	            words = line['words']
	            for word in words:
	                if len(word) > 8:
	                    return word

	    conn.close()
	except Exception as e:
	    print 'Connection Failed'

print image_to_text("https://www.researchgate.net/profile/Philip_Johnston3/publication/257460358/figure/fig2/AS:203093967937545@1425432783552/Traditional-medication-label-Example-shown-to-patients-for-purpose-of-discussion-Color.png")
