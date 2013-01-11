package sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/***
 *
 * Sample program to upload isa tab formatted files to Ingenuity.
 *
 */
public class Main {

    // beta or production environment

    public static final String SERVER_CLUSTER = "https://developer.ingenuity.com";
//    public static final String SERVER_CLUSTER = "https://api.ingenuity.com";

    public static final String ACCESS_URI = SERVER_CLUSTER + "/datastream/api/v1/oauth/access_token";
    public static final String BATCH_URI = SERVER_CLUSTER + "/datastream/api/v1/labcorp/datapackages/batch";

    // replace with your actual id/secret from
    // https://developer.ingenuity.com/datastream/developers/myapps.html

    static final String clientId = "xxx";
    static final String clientSecret = "xxx";

    static final Client client = Client.create();
    static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws InterruptedException, IOException {

        String accessToken = getAccessToken();

        String batchResourceUri = upload(accessToken);

        while (!isDone(batchResourceUri, accessToken)) {
            Thread.sleep(5000);
        }

        String exportUri = getExportUri(batchResourceUri, accessToken);

        export(exportUri, accessToken);
    }

    public static String getAccessToken() throws IOException {

        String json = client
                .resource(ACCESS_URI + "?grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret)
                .get(String.class);

        String token = parseJson(json, "access_token");

        System.out.println("access_token: " + token);

        return token;
    }

    private static String upload(String accessToken) throws IOException {
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.field("access_token", accessToken);

        // replace with your actual experiment files
        // use StreamDataBodyPart if prefer not to use a file

        multiPart.bodyPart(new FileDataBodyPart("testId001", new File("src/main/resources/isatab/testId001.zip")));
        multiPart.bodyPart(new FileDataBodyPart("testId002", new File("src/main/resources/isatab/testId002.zip")));

        ClientResponse clientResponse = client.resource(BATCH_URI)
                .type(MediaType.MULTIPART_FORM_DATA_TYPE)
                .post(ClientResponse.class, multiPart);

        String json = clientResponse.getEntity(String.class);

        String location = parseJson(json, "location");

        System.out.println("batchResourceUri: " + location);

        return location;
    }

    private static boolean isDone(String batchResourceUri, String accessToken) throws IOException {
        String json = client
                .resource(batchResourceUri + "?access_token=" + accessToken)
                .get(String.class);

        String status = parseJson(json, "status");

        System.out.println("status: " + status);

        return "DONE".equals(status);
    }

    private static String getExportUri(String batchResourceUri, String accessToken) throws IOException {

        String json = client
                .resource(batchResourceUri + "?access_token=" + accessToken)
                .get(String.class);

        String exportUri = parseJson(json, "exportUrl");

        System.out.println("exportUri: " + exportUri);

        return exportUri;
    }

    private static String parseJson(String json, String fieldName) throws IOException {
        Map m = mapper.readValue(json, Map.class);
        return (String) m.get(fieldName);
    }


    private static void export(String exportUri, String accessToken) throws IOException {
        InputStream input = client
                .resource(exportUri + "?access_token=" + accessToken)
                .get(InputStream.class);

        FileOutputStream output = new FileOutputStream("batch.zip");
        IOUtils.copy(input, output);

        System.out.println("exported file: batch.zip");

        IOUtils.closeQuietly(output);
        IOUtils.closeQuietly(input);
    }
}
