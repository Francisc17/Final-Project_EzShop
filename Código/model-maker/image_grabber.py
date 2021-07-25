import json
import os

import requests

query = input("Search keyworks: ")

r = requests.get(
    f"https://api.unsplash.com/search/photos?query={query}&per_page=30&client_id=FNgyp1qtZrrcSa7fps_7Hze3O6HGplLYwD5udtmqkEs")

try:
    results = r.json()['results']

    for result in results:
        r = requests.get(result['urls']['regular'])
        filename = r.headers.get('X-Imgix-ID') + '.jpg'
        path = f"./food_photos/{query}"

        if not os.path.exists(path):
            os.makedirs(path)

        open(os.path.join(path, filename), 'wb+').write(r.content)
except json.JSONDecodeError:
    print("Error parsing JSON!")
