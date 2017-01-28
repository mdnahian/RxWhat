import json
import re

f = open('sample.json', 'r')
raw = f.read()

data = json.loads(raw)
regions = data['regions']

for region in regions:
	for line in region['lines']:
		for word in line['words']:
			text = ''.join(i for i in word['text'] if not i.isdigit())
			text = re.sub('\W+', ' ', text)
			if len(text) > 8:
				print text