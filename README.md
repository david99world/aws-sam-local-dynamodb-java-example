# aws-sam-local-dynamodb-java-example
This is a local DynamoDB example with a Java Lambda function all running locally

## Develop Java Lambda functions locally with a DynamoDB local.

This repository is an example of running a local Java Lambda function with a local DynamoDb running in a container.  The process is as follows:



### Install required dependencies

 - Install [Maven](https://maven.apache.org/)
 - Install [Java11 OpenJDK](https://adoptopenjdk.net/)
 - Install [AWS CLI](https://aws.amazon.com/cli/)


#### Steps
1.  Create a docker network

    `docker network create lambda-local`

2.  Create a local docker container for dynamodb.

    We pass -sharedDb so it can be used by multiple processes.  We reuse the same docker network declared above.

    ```
    docker run -d -p 8000:8000 --network lambda-local --name dynamodb amazon/dynamodb-local -jar DynamoDBLocal.jar -sharedDb
    ```

3.  Create table in dynamodb on port 8000

    ```
    aws dynamodb create-table --table-name orders_table --attribute-definitions AttributeName=orderId,AttributeType=S --key-schema AttributeName=orderId,KeyType=HASH --billing-mode PAY_PER_REQUEST --endpoint-url http://localhost:8000
    ```

4.  Insert data into that table

    ```shell
    aws dynamodb put-item --table-name orders_table --item '{"orderId": {"S":"1"} }' --endpoint-url http://localhost:8000
    ```

5.  (Optional)  Check the data is in the table

    Navigate to the DynamoDB shell

    http://localhost:8000/shell/

    Insert the following command
    
    ```javascript
    var params = {
    TableName: 'orders_table',
    };
    dynamodb.scan(params, function(err, data) {
        if (err) ppJson(err); // an error occurred
        else ppJson(data); // successful response
    });
    ```
    ![dynamoDbScreenshotShell](https://raw.githubusercontent.com/david99world/aws-sam-local-dynamodb-java-example/main/images/dynamoDbScreenshot.png)


7.  Build the SAM Java function

    You need to be in the cloned directory at this point, rather than the Lambda function.

    ```shell
    sam build
    ``` 
    This will build the sam application with the following output

    ```
    Building codeuri: DynamoDbFunction runtime: java11 metadata: {} functions: ['DynamoDbFunction']
    Running JavaMavenWorkflow:CopySource
    Running JavaMavenWorkflow:MavenBuild
    Running JavaMavenWorkflow:MavenCopyDependency
    tabRunning JavaMavenWorkflow:MavenCopyArtifacts

    Build Succeeded

    Built Artifacts  : .aws-sam/build
    Built Template   : .aws-sam/build/template.yaml

    Commands you can use next
    =========================
    [*] Invoke Function: sam local invoke
    [*] Deploy: sam deploy --guided
    ```

8.  Run on local docker network

    ```shell
    sam local invoke --docker-network lambda-local
    ```
    This will invoke your lambda image locally with the target classes from the maven package you previously used.
