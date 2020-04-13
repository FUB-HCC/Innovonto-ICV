from nltk.corpus import stopwords
from nltk.corpus import wordnet
import re


# https://stackoverflow.com/questions/1883980/find-the-nth-occurrence-of-substring-in-a-string
def find_nth(haystack, needle, n):
    start = haystack.find(needle)
    while start >= 0 and n > 1:
        start = haystack.find(needle, start + len(needle))
        n -= 1
    return start


def get_synsets(w):
    return wordnet.synsets(w)


def get_definitions(syns):
    return syns.definition()


def get_label(syns):
    lemmas = syns.lemmas()
    return lemmas[0].name()


def get_class(syns):
    return syns.pos()


def get_resource(syns):
    return syns.offset()  # id for a synset in wordnet db


def get_source(syns):
    return syns.name()


# what if the text is used multiple times?
def get_offset(w, text, nth):
    offset = find_nth(text, w, nth)
    return offset


def stop_words_filtering(text):
    # "A" to "Z"
    aToZ = re.compile(r"[a-zA-Z0-9_-]+")
    word_tokens = []
    word_tokens = word_tokens + aToZ.findall(text)  # remove all punctuation within the text.

    stop_words = set(stopwords.words('english'))  # Stopwords for english texts
    filtered_sentence = [w for w in word_tokens if
                         not w in stop_words]  # remove all stopwords within the text
    return filtered_sentence


# Nested dictionary
def build_resource_candidates(word, syns, offset):
    return {
        "description": get_definitions(syns),
        "label": get_label(syns),
        "offset": offset,
        "resource": get_resource(syns),
        "source": get_source(syns),
        "text": word,
        "pos": get_class(syns)
    }


def build_resources(word, offset):
    resources = []
    synsets = get_synsets(word)
    if "-" in word:
        tokens = word.split("-")
        for w in tokens:
            synsets.extend(get_synsets(w))
    elif "_" in word:
        tokens = word.split("_")
        for w in tokens:
            synsets.extend(get_synsets(w))
    for syns in synsets:
        resources.append(build_resource_candidates(word, syns, offset))
    return resources


def build_annot_candidates(word, text, current_word_occurence_count):
    offset = get_offset(word, text, current_word_occurence_count)
    return {
        "offset": offset,
        "resource_candidates": build_resources(word, offset),
        "text": word
    }


def build_annot(text):
    annotDict = {}
    annot = []
    annotDict["annotation_candidates"] = annot
    annotDict["text"] = text
    nonStopWords = stop_words_filtering(text)
    wordCount = {}
    for w in nonStopWords:
        find = 0
        if not bool(wordCount):  # wenn dictionary leer ist
            wordCount[w] = 0
            wordCount[w] = wordCount[w] + 1
            currentWordCount = wordCount[w]
        else:
            for key in wordCount.copy():
                if (key.find(w) != -1):
                    wordCount[key] = wordCount[key] + 1
                    currentWordCount = wordCount[key]
                    find = 1
                    break
            if find != 1:
                wordCount[w] = 0
                wordCount[w] = wordCount[w] + 1
                currentWordCount = wordCount[w]
        annot.append(build_annot_candidates(w, text, currentWordCount))

    return annotDict
