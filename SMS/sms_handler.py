import boto3
import json
import logging


logger = logging.getLogger()
logger.setLevel(logging.INFO)

def send_sms(event, context):
    # Replace with the AWS SNS topic ARN
    sns_topic_arn = 'SNS_TOPIC_ARN'
    
    try:
        # Extract phone number and message from the request body
        body = json.loads(event['body'])
        phone_number = body.get('phone_number')
        message = body.get('message')

        if not phone_number or not message:
            logger.error('Invalid request. Phone number and message are required.')
            return {
                'statusCode': 400,
                'body': 'Invalid request. Phone number and message are required.'
            }

        # Initialize the SNS client
        sns_client = boto3.client('sns')

        # Send the SMS message
        response = sns_client.publish(
            TopicArn=sns_topic_arn,
            Message=message,
            PhoneNumber=phone_number
        )

        logger.infp("SMS sent successfully!")
        logger.info("Message ID:", response['MessageId'])

        return {
            'statusCode': 200,
            'body': 'SMS sent successfully!'
        }
    except Exception as e:
        logger.error("Error sending SMS:", e)
        return {
            'statusCode': 500,
            'body': 'Error sending SMS.'
        }
