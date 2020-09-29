# -*- coding: utf-8 -*-
"""
Created on Wed Sep  9 22:11:52 2020

@author: achra
"""

import pandas as pd
import pickle

dataset = pd.read_csv('toyota.csv')

#print(dataset.columns)
#print(dataset.head())
#print(dataset.shape)
#print(dataset.describe())
#print(dataset['price'].max())
#print('the minimimum price in toyotas collection is {}'.format(dataset['price'].min()))
#print(dataset.isnull().any())
print(dataset['transmission'].unique())
dataset = dataset[dataset.fuelType != 'Other']
print(dataset['fuelType'].unique())


dataset['transmission'].replace('Manual',0,inplace=True)
dataset['transmission'].replace('Automatic',1,inplace=True)
dataset['transmission'].replace('Semi-Auto',2,inplace=True)
print(dataset.head())
dataset = dataset[dataset.transmission != 'Other']
dataset['transmission']=dataset['transmission'].astype(int)
print(dataset.dtypes)
dataset = dataset[dataset.fuelType != 'Other']
dataset['fuelType'].replace('Petrol',0,inplace=True)
dataset['fuelType'].replace('Diesel',1,inplace=True)
dataset['fuelType'].replace('Hybrid',2,inplace=True)
dataset['fuelType']=dataset['fuelType'].astype(int)
print(dataset.dtypes)

corr = dataset.corr()
print(corr)

#import seaborn as sns
#sns.heatmap(corr)

feature_cols=['year','transmission','mileage','tax','mpg','engineSize']
x=dataset[feature_cols]
y=dataset.price

from sklearn.model_selection import train_test_split
x_train,x_test,y_train,y_test=train_test_split(x,y,random_state=42,test_size=0.2)

from sklearn import svm
sv=svm.SVR()
sv.fit(x_train,y_train)
pred=sv.predict(x_test)
print(pred)

s=sv.score(x,y)
print(s)
from sklearn.tree import DecisionTreeRegressor
tree=DecisionTreeRegressor()
tree.fit(x_train,y_train)
DecisionTreeRegressor()
p=tree.predict(x_test)
#print(p)
df = pd.DataFrame({'year':[2013],'transmission':[0],'mileage':[10000],'tax':[265],'mpg':[36.2],'engineSize':[2.0]})

s=tree.score(x,y)
print('accuracy score',s)

pred = tree.predict(df)
print('le prix estimé est : {}'.format(pred))

print(dataset[dataset.price == 10399].mileage)
filename = 'final_pred'
pickle.dump(tree, open('final_prediction.pickle', 'wb'))

model = pickle.load(open('final_prediction.pickle', 'rb'))
prediction = model.predict(df)
print('prediction is {} '.format(prediction))