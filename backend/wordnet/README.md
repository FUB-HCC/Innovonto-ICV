# Wordnet Backend for Interactive Concept Validation (ICV)

This module is based on:
https://github.com/gancia-kiss/Bachelorarbeit/blob/master/Programm/API_Server.ipynb


This project uses
* nltk
* nltk.wordnet
* flask

to provide a backend for the interactive concept validation.

## Development
This is a python 3.6 project.

To setup the project run:

    pip install -r requirements.txt
    python flaskApiServer.py
    
    http://localhost:4000/

## Usage

Right now this Backend offers exactly one url `/api/candidates`

### `GET: /api/candidates`

GET requests against this URL will give you annotation candidates for a given text. The following parameters are available:

- `text` - required

#### `text` - required

The text is the text for wich the annotation candidates are requested.

##### Example - "Foo Bar"

```sh
curl --request GET \
  --url 'http://localhost:4000/api/candidates?text=Foo Bar'
```

## Deployment

WIP