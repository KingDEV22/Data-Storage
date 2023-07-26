import json
import requests
import logging


logger = logging.getLogger()
logger.setLevel(logging.INFO)

slangs=[]

def make_api_call(data, url , type):

    try:
        # Convert data to JSON format
        data_json = json.dumps(data)

        # Set headers (optional)
        headers = {
            "Content-Type": "application/json",
            "Authorization": "Bearer YOUR_API_TOKEN",  # Replace with the actual API token if needed
        }

        if type == "GET":
            response = requests.get(url, headers=headers)
        else: 
            response = requests.post(url, data=data_json, headers=headers)
        

        # Check if the request was successful
        if response.status_code == 200:
            # Process the response data (if needed)
            response_data = response.json()
            return response_data
        else:
            logger.error(response.content)
            return None
    except requests.exceptions.RequestException as e:
        # Handle connection or request errors
        logger.error(response.content)
        return None
    

def slang_filter(answer,country):
    if len(slangs)==0:
        slangs.extend(list(make_api_call(country, "slang fetching url", "GET")))
    
    if answer in slangs:
        return True
    
    return False
                
  

def lambda_handler(event, context):
    error = {}
    try:
        # Get the form data from the event
        data = json.loads(event['body'])
        name = data.get('name')
        country = data.get('country')
        questions = data.get('questions', [])
        answers = data.get('answers' , [])
        answer_types = data.get('answer', []) 
        state = data.ger('state')

        validation_rules = make_api_call(name, "url to recieve form data", "GET") # fetch the validations for the form based on the form name

        # Iterate through each question-answer pair
        for question_id, answer, a_type in enumerate(questions, answers, answer_types):
            # Validate the answer
            validation_rule = validation_rules[answer_types]
            if validation_rule == "slang":
                if not slang_filter(answer,country):
                    error[question_id] = answer
        

        if len(error) == 0: # the data is error free
            make_api_call(data, "url of the database service to store data", "POST")
        else:
            return {
            'statusCode': 400,
            'body': json.dumps(error)
        }
            

        # Return the response from the microservice
        return {
            'statusCode': 200,
            'body': "The data has been submitted you would be notifed by email about further process."
        }
    except Exception as e:
        # Handle any exceptions or errors that might occur during the validation process
        logger.error(e)
        return {
            'statusCode': 500,
            'body': json.dumps(str(e))
        }