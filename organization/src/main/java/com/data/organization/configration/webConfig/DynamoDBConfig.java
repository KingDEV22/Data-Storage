// package com.data.organization.configration.webConfig;

// import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// import com.amazonaws.auth.AWSCredentialsProvider;
// import com.amazonaws.auth.AWSStaticCredentialsProvider;
// import com.amazonaws.auth.BasicAWSCredentials;
// import com.amazonaws.client.builder.AwsClientBuilder;
// import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
// import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

// @Configuration
// @EnableDynamoDBRepositories(basePackages = "com.data.database.repository")
// public class DynamoDBConfig {

//     @Value("${amazon.dynamodb.endpoint}")
//     private String amazonDynamoDBEndpoint;

//     @Value("${amazon.aws.accesskey}")
//     private String amazonAWSAccessKey;

//     @Value("${amazon.aws.secretkey}")
//     private String amazonAWSSecretKey;

//     @Value("${amazon.aws.region}")
//     private String region;

//     @Bean
//     public AmazonDynamoDB amazonDynamoDB(AWSCredentialsProvider awsCredentialsProvider) {
//         AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
//                 .withEndpointConfiguration(
//                         new AwsClientBuilder.EndpointConfiguration(amazonDynamoDBEndpoint, region))
//                 .withCredentials(awsCredentialsProvider).build();
//         return amazonDynamoDB;
//     }

//     @Bean
//     public AWSCredentialsProvider awsCredentialsProvider() {
//         return new AWSStaticCredentialsProvider(new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey));
//     }
// }
