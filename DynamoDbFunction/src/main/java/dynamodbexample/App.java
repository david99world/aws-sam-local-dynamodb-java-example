package dynamodbexample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<Object, String> {

    private String DYNAMODB_TABLE_NAME = "orders_table";

    public String handleRequest(final Object input, final Context context) {
        DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(
            new AwsClientBuilder.EndpointConfiguration("http://dynamodb:8000/", "local")).build());

        Table table =  dynamoDB.getTable(DYNAMODB_TABLE_NAME);

        ItemCollection<ScanOutcome> items = table.scan();

        System.out.println("Scan of " + table.getTableName());
        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next().toJSONPretty());
        }

        String response = String.format("{ \"message\": \"Success\"}");

        return response;
    }
}
