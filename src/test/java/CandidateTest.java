import com.shaft.driver.SHAFT;
import com.shaft.tools.io.JSONFileManager;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import junit.framework.Assert;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.GetCookies;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class CandidateTest {

    private final JSONFileManager data = new JSONFileManager("src/test/resources/TestDataFiles/candidateData.json");


    private SHAFT.API api;

    private String cookie;
    private String firstName;
    private String lastName;
    private String email;
    private String newCandidateId;

    public static final String BASE_URL= System.getProperty("BaseUrl");

    @BeforeClass
    public void init(){
        api = new SHAFT.API(BASE_URL);
        GetCookies cookieHandler = new GetCookies();
        cookie = cookieHandler.getCookiesUsingSelenium();
        firstName = data.getTestData("firstName");
        lastName = data.getTestData("lastName");
        email = data.getTestData("email");

    }



    @Test(description = "create new candidate")
    public void createCandidate() throws JSONException {

        JSONObject body = new JSONObject();
        body.put("firstName",firstName);
        body.put("lastName",lastName);
        body.put("email",email);

        api
                .post("/web/index.php/api/v2/recruitment/candidates")
                .setTargetStatusCode(200)
                .setContentType(ContentType.JSON)
                .addHeader("Cookie", cookie)
                .setRequestBody(body)
                .perform();

        newCandidateId = api.getResponseJSONValue("data.id");
    }

    @Test(description = "validate that candidate created correctly ", dependsOnMethods = {"createCandidate"})
    public void searchCandidate(){
        String candidates = api
                .get("/web/index.php/api/v2/recruitment/candidates")
                .setTargetStatusCode(200)
                .addHeader("Cookie", cookie)
                .perform().asString();


        JsonPath candidateList = new JsonPath(candidates);
        int count = candidateList.getInt("data.size()");
        for (int i = 0; i < count; i++){
            String candidateID = candidateList.getString("data["+i+"].id");
            if(candidateID.equals(newCandidateId)){

                String actualFirstName = candidateList.getString("data["+i+"].firstName");
                String actualLastName = candidateList.getString("data["+i+"].lastName");
                String actualEmail = candidateList.getString("data["+i+"].email");

                Assert.assertEquals(firstName,actualFirstName);
                Assert.assertEquals(lastName,actualLastName);
                Assert.assertEquals(email,actualEmail);
            }
        }



    }
    @Test(description = "delete the created candidate", dependsOnMethods = {"searchCandidate"})
    public void deleteCandidate() throws JSONException {
        Map<String, Object> body = new HashMap<>();
        body.put("ids", Arrays.asList(newCandidateId));
        api
                .delete("/web/index.php/api/v2/recruitment/candidates")
                .setTargetStatusCode(200)
                .addHeader("Cookie", cookie)
                .setRequestBody(body)
                .setContentType(ContentType.JSON)
                .perform();
    }
}
