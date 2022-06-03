import com.sfn.clients.SfnExecutionRunner;
import com.sfn.data.ImmutableTestExecutionRequest;
import com.sfn.data.TestExecutionRequest;
import com.sfn.match.lambda.LambdaSuccessfulAtLeastOnceMatcher;
import com.sfn.match.sfn.SfnSuccessMatcher;
import com.sfn.poll.dagger2.DaggerMainComponent;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sfn.model.DescribeExecutionResponse;
import software.amazon.awssdk.services.sfn.model.GetExecutionHistoryResponse;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SfnIntegMain {

    private static final String RECEIVE_TX_LAMBDA_ARN = "arn:aws:lambda:us-east-2:397250182609:function:Production-MainStack-TransactionLambdasReceiveTran-bfjVrmhEQ5Jn";

    public static void main(String[] args) {
        log.info("Hello");
        SfnExecutionRunner runner = DaggerMainComponent.create().createSfnExecutionRunner();
        runner.runExecutions(createRequests());

    }

    private static List<TestExecutionRequest> createRequests() {
        List<TestExecutionRequest> requests = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            requests.add(requestWithLambdaSuccessMatcherAndTestFunction());
        }

        return requests;
    }

    private static void logResults(GetExecutionHistoryResponse historyResponse,
                                   DescribeExecutionResponse describeExecutionResponse) {
        log.info("Request succeeded with events {}",
                historyResponse.events());
    }

    private static TestExecutionRequest requestWithLambdaSuccessMatcherAndTestFunction() {
        return ImmutableTestExecutionRequest.builder()
                .payload(samplePayload())
                .matcher(new LambdaSuccessfulAtLeastOnceMatcher(RECEIVE_TX_LAMBDA_ARN))
                .testFunction(SfnIntegMain::logResults)
                .build();
    }

    private static TestExecutionRequest requestWithSfnSuccessMatcherAndTestFunction() {
        return ImmutableTestExecutionRequest.builder()
                .payload(samplePayload())
                .matcher(new SfnSuccessMatcher())
                .testFunction(SfnIntegMain::logResults)
                .build();
    }

    private static String samplePayload() {
        return "{\n" +
                "  \"plaidItem\": {\n" +
                "    \"user\": \"google_115577301718883329077\",\n" +
                "    \"institutionId\": \"Discover-ins_33\",\n" +
                "    \"accessToken\": \"access-development-f034a8e5-ddea-4dc1-9b8c-55363f0df0ad\",\n" +
                "    \"webhook\": false,\n" +
                "    \"dateCreated\": \"2022-03-08T05:38:53.236Z\",\n" +
                "    \"metadata\": \"{\\\"institution\\\":{\\\"name\\\":\\\"Discover\\\",\\\"institution_id\\\":\\\"ins_33\\\"},\\\"account\\\":{\\\"id\\\":null,\\\"name\\\":null,\\\"type\\\":null,\\\"subtype\\\":null,\\\"mask\\\":null},\\\"account_id\\\":null,\\\"accounts\\\":[{\\\"id\\\":\\\"EMj4omXJE7hL3wzaODMqsAJ5n58VeqfJj8brY\\\",\\\"name\\\":\\\"Discover it chrome Card\\\",\\\"mask\\\":\\\"2529\\\",\\\"type\\\":\\\"credit\\\",\\\"subtype\\\":\\\"credit card\\\"}],\\\"link_session_id\\\":\\\"80c7234c-c015-4ff0-9a21-1fe023439548\\\",\\\"public_token\\\":\\\"public-development-d87cf97b-e690-4d14-af60-566ad09202b2\\\"}\",\n" +
                "    \"accounts\": [\n" +
                "      \"Discover it chrome Card-Kq3V6zN057cRvKm1o3RQszYqa1DJK8tyojLkA\"\n" +
                "    ],\n" +
                "    \"availableProducts\": [\n" +
                "      \"transactions\"\n" +
                "    ],\n" +
                "    \"id\": \"xaDEK759OqigepKbQ18jIPamwwpPKoUM5L8mK\"\n" +
                "  },\n" +
                "  \"startDate\": \"2022-03-01T21:57:00.000\",\n" +
                "  \"endDate\": \"2022-03-12T21:57:00.000\"\n" +
                "}";
    }
}
