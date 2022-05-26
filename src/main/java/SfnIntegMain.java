import com.sfn.clients.SfnExecutionRunner;
import com.sfn.data.ImmutableTestExecutionRequest;
import com.sfn.data.TestExecutionRequest;
import com.sfn.poll.dagger2.DaggerMainComponent;

import java.util.ArrayList;
import java.util.List;


public class SfnIntegMain {

    public static void main(String[] args) {
        SfnExecutionRunner runner = DaggerMainComponent.create().createSfnPoller();
        runner.runExecutions(createRequests());

    }

    private static List<TestExecutionRequest> createRequests() {
        List<TestExecutionRequest> requests = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            requests.add(ImmutableTestExecutionRequest.builder()
                    .payload(samplePayload())
                    .matcher(null)
                    .testFunction(null)
                    .build());
        }
        return requests;
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
