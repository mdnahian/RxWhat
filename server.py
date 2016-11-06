from flask import Flask, render_template, request, send_from_directory
from flask_socketio import SocketIO, emit
from PIL import Image
from pytesseract import image_to_string
from gtts import gTTS
from translate import translator
from threading import *
import httplib, urllib, base64
import random
import string
import io
import json
import re
import os
import requests
import ast, json
import logging

BASE_URL = 'http://138.197.0.96'

app = Flask(__name__, static_url_path='')
app.config['SECRET_KEY'] = 'Rx'
socketio = SocketIO(app)

requests.packages.urllib3.disable_warnings()


# class RxProcess(Thread):

# 	def __init__(self, image, lang):
# 		Thread.__init__(self)
# 		self.image = image
# 		self.lang = lang
# 		self.start()

# 	def run(self):
# 		# stream = BytesIO(self.image)
# 		text = image_to_string(Image.open(stream))
# 		print text
#     	fileName = ''.join(random.SystemRandom().choice(string.ascii_uppercase + string.digits) for _ in range(0, 6))+'.mp3'
#     	tts = gTTS(text=text, lang=self.lang)
#     	tts.save(fileName)
#     	emit('url', BASE_URL+'/'+fileName)



def get_drug_use(drug_name):
    try:
		get_ndc = requests.get(('https://api.fda.gov/drug/label.json?search=' + drug_name))
		get_ndc_dictionary = json.loads(get_ndc.text)
		ndc_code_list = get_ndc_dictionary["results"][0]["openfda"]["product_ndc"][-1]
		ndc_code = "".join(ndc_code_list)
		get_dbi = requests.get(('https://api.drugbankplus.com/v1/us/products/' + ndc_code), headers={'Authorization': '227d2122f2bc4b5359065739a6157271'})
		get_dbi_dictionary = json.loads(get_dbi.text)
		drugbank_id = get_dbi_dictionary["ingredients"][0]["drugbank_id"]
		get_id = requests.get(('https://api.drugbankplus.com/v1/us/drugs/' + drugbank_id), headers={'Authorization': '227d2122f2bc4b5359065739a6157271'})
		get_id_dictionary = json.loads(get_id.text)
		id = get_id_dictionary["pharmacology"]["indication_descripton"]
		return id
    except KeyError:
        return 0


def execute(image, lang='en'):
	
	# img = Image.open(io.BytesIO(image)).convert('L')
	# text = re.sub('\W+',' ', image_to_string(img))

	fileName = ''.join(random.SystemRandom().choice(string.ascii_uppercase + string.digits) for _ in range(0, 6))

	img = Image.open(io.BytesIO(image))
	img.save('static/img/'+fileName+'.jpg')

	imgg = BASE_URL+'/img/'+fileName+'.jpg'


	description, name = image_to_text('{ "url": "'+imgg+'" }')

	name = name.replace(" ", "")
	
	
	# img.save('static/img/'+fileName+'.jpg')

	# print BASE_URL+'/img/'+fileName+'.jpg'

	url = BASE_URL+'/'+fileName+'.mp3'

	print url

	if description != '':
		print description			

		try:
			translated_text = description

			if lang != 'en':
				translated_text = translator('en', lang, description)[0][0][0]

			print translated_text
			tts = gTTS(text=translated_text, lang=lang)
			tts.save('static/'+fileName+'.mp3')
			emit('url', '{ "name":"'+name+'", "mp3":"'+url+'", "image":"'+imgg+'", "description":"'+translated_text+'" }')
		except:
			pass

	else:
		print "Failed to find a match"


def image_to_text(json_url):
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
	    conn.request("POST", "/vision/v1.0/ocr?%s" % params, json_url, headers)
	    response = conn.getresponse()
	    raw = response.read()
	    data = json.loads(raw)

	    regions = data['regions']
	    for region in regions:
	    	for line in region['lines']:
				for word in line['words']:
					text = ''.join(i for i in word['text'] if not i.isdigit())
					text = re.sub('\W+', ' ', text)
					if len(text) > 8:
						txt = get_drug_use(text)
						if txt != 0:
							return (txt, text)

		return ('', '')

	    # regions = d['regions']

	    # for region in regions:
	    # 	print region
	        # lines = region['lines']
	        # for line in lines:
	        #     words = line['words']
	        #     for word in words:
	        #         if len(word) > 8:
	        #             return word

	    conn.close()
	except Exception as e:
		return ('', '')


def save_lang(lang):
	outFile = open('language.txt', 'w')
	outFile.write(lang)
	outFile.close()

def read_lang():
	inFile = open('language.txt', 'r')
	return inFile.read()



@socketio.on('delete')
def delete_mp3(url):
	print "Deleting..."
	fileName = url.split('/')[-1].split('.')[0]
	file_ext = '.'+url.split('.')[-1]
	file_path = 'static/'+fileName+file_ext
	if os.path.isfile(file_path):
		print file_path
		os.unlink(file_path)
		print "Deleted"


@socketio.on('image_sent')
def image_sent(raw):

	# data = json.loads(raw)
	# lang = data["language"]
	# print lang
	# print "Executing..."
	# RxProcess(data["image"], lang)
	# execute(data["image"], lang)
	language = read_lang()
	print language
	execute(raw, language)


@socketio.on('language')
def update_language(language):
	save_lang(language)


@app.route('/<path:path>')
def sound_file(path):
    return url_for('static', filename=path)


if __name__ == '__main__':
    socketio.run(app, host='0.0.0.0', port=int(80))

