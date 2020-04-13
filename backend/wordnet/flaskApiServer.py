import nltk
from flask import Flask, request, jsonify
from flask_cors import CORS

from wordnetApi import build_annot

app = Flask(__name__)
CORS(app)


@app.route('/')
def index():
    return jsonify({"wordnetApiStatus": "up"})


@app.route('/api/candidates')
def candidates():
    text = request.args.get('text')
    if not text:
        return "Missing required url parameter \'text\'", 400
    annotation = build_annot(text)
    return jsonify(annotation)


if __name__ == '__main__':
    nltk.download('wordnet')
    nltk.download('stopwords')
    with app.app_context():
        # app.debug = True
        from werkzeug.serving import run_simple

        run_simple('localhost', 4000, app)
