import pandas as pd
from os.path import dirname, join
import os
filename = join(dirname(__file__),"data/books_dataset.csv")
books = pd.read_csv(filename)

books = books[['id', 'title', 'author', 'description','type', 'ownerNumber']]

books['tags'] = books['type'] +' ' +books['author'] + books['title']
new_books = books.drop(columns=['description', 'id'])

from sklearn.feature_extraction.text import CountVectorizer
cv =CountVectorizer(max_features=14, stop_words='english')
vector = cv.fit_transform(new_books['tags'].values.astype('U')).toarray()

#cosine similarity pentru a afla similaritatea dintre 2 carti
from sklearn.metrics.pairwise import cosine_similarity
similarity = cosine_similarity(vector)

def recommand(books, phone):
    myList = []
    nr=0
    index = new_books[new_books['title']==books].index[0]
    distance = sorted(list(enumerate(similarity[index])), reverse=True, key=lambda vector:vector[1])

    print(distance)

    for i in distance:
        if i[1] != 0 :
            if ((str(new_books.iloc[i[0]].ownerNumber) != str(phone)) and new_books.iloc[i[0]].title != books) :
                myList.append(new_books.iloc[i[0]].title)
                nr=nr+1
            if nr==6 : break
    if len(myList)==0:
        myList.append("nicioRecomandare")
    return list(myList)
