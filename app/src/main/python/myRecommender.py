import pandas as pd
from os.path import dirname, join
import os
filename = join(dirname(__file__),"data/books_dataset.csv")
books = pd.read_csv(filename)

books = books[['id', 'title', 'author', 'description','type', 'ownerNumber']]

books['info'] = books['type'] +' ' +books['author'] +' ' + books['title']
new_books = books.drop(columns=['author','description', 'type'])

from sklearn.feature_extraction.text import CountVectorizer
cv =CountVectorizer()
vector = cv.fit_transform(new_books['info'].values.astype('U')).toarray()

#cosine similarity pentru a afla similaritatea dintre 2 carti
from sklearn.metrics.pairwise import cosine_similarity
similarity = cosine_similarity(vector)

def recommand(titleBook, phone):
    myBooksList = []
    index = new_books[new_books['title']==titleBook].index[0]
    distance = sorted(list(enumerate(similarity[index])), reverse=True, key=lambda vector:vector[1])

    for i in distance:
        if i[1] != 0 :
            if ((str(new_books.iloc[i[0]].ownerNumber) != str(phone)) and new_books.iloc[i[0]].title != titleBook) :
                myBooksList.append(new_books.iloc[i[0]].title)

            if len(myBooksList)==6 : break
    if len(myBooksList)==0:
        myBooksList.append("nicioRecomandare")
    return list(myBooksList)
