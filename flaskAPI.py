from flask import Flask,request,redirect,url_for,flash,jsonify
import json
import pickle as p
import pandas as pd
import numpy as np
app = Flask(__name__)


@app.route("/api/",methods=['POST'])
def predict():
    json_data = request.get_json()
    data = pd.DataFrame(json_data)
    result = {'data' : [{'prediction' : np.array2string(model.predict(data))}]}
    # result = json.load(result)
    #prediction = np.array2string(model.predict(data))
    return jsonify(result)

if __name__ == '__main__':
    modelfile = 'final_prediction.pickle'
    model = p.load(open(modelfile,'rb'))
    app.run(debug=True,host='192.168.1.8')