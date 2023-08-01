from flask import Flask,request,jsonify
from flask_cors import CORS

import requests

app = Flask(__name__)
CORS(app)

def contains_slang(text):
    # Replace this with your slang detection logic
    # For this example, we'll assume any text containing 'slang' is considered slang
    if 'slang' in text.lower():
        return True
    return False

@app.route('/validate', methods=['POST'])
def welcome():
    data = request.get_json()
    questions = data.get('qa')
    print(data)
    # for key, value in questions.items():
    #     if key.endswith('_type') and value == 'text':
    #         question_num = key.split('_')[0]
    #         answer_key = f'{question_num}_answer'
    #         if answer_key in data and contains_slang(data[answer_key]):
    #             return jsonify({"message": f"Slang detected in {data[answer_key]}"}), 400

    try:
        response = requests.post('http://localhost:8081/db/form/data/save', json=data)  # Replace with the actual API endpoint
        response_data = response.json()
        return jsonify(response_data), response.status_code
    except requests.exceptions.RequestException as e:
        return jsonify({"message": f"Failed to send data to the API: {str(e)}"}), 500

if __name__ == '__main__':
    app.run(debug=True)