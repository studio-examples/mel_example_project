package org.mule.examples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.AfterClass;
import org.junit.Test;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;
import org.mule.transport.NullPayload;

public class MELTest extends FunctionalTestCase
{
	private static final String MESSAGE = "username=Mule&age=1";
	private static final String MESSAGE_XML = "<user><username>Mule</username><age>1</age></user>";
	private static final String MESSAGE_JSON = "{\n\"username\": \"Mule\",\n\"age\": 1\n}\n";
	private static final String REPLY = "Hello Mule";
	private static final String REPLY_2 = "No username provided";
	private static final String DIR = "Path_of_your_choice";
	private static final String REPLY_1 = "Mule, 1, false";
	
    @Override
    protected String getConfigResources()
    {
        return "greeting.xml";
    }

    @Test
    public void melTest() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        testResponse(client, "greet1?" + MESSAGE, REPLY, StringUtils.EMPTY);        
        testResponse(client, "greet2", REPLY_2, StringUtils.EMPTY);
        testResponse(client, "greet3?" + MESSAGE, REPLY, StringUtils.EMPTY);
        assertTrue(new File(DIR).list().length == 1);
        FileUtils.deleteDirectory(new File(DIR));
        
        testFileAndClean(client, "greet4?" + MESSAGE, "\"Mule\",\"1\",\"false\"\n", StringUtils.EMPTY);
        testFileAndClean(client, "greet5", REPLY_1, MESSAGE_XML);
        testFileAndClean(client, "greet6", REPLY_1, MESSAGE_JSON);               
    }
    
    private void testFileAndClean(MuleClient client, String param, String reply, String body) throws Exception{
    	testResponse(client, param, REPLY, body);
        assertTrue(new File(DIR).list().length == 1);
        assertEquals(reply, FileUtils.readFileToString(new File(DIR).listFiles()[0]));
        FileUtils.deleteDirectory(new File(DIR));
    }
    
    private void testResponse(MuleClient client, String param, String reply, String body) throws Exception{
    	Map<String, Object> props = new HashMap<String, Object>();
    	props.put("http.method", "POST");
        MuleMessage result = client.send("http://localhost:8081/" + param, body, props);
        assertNotNull(result);
        assertFalse(result.getPayload() instanceof NullPayload);
        assertEquals(reply, result.getPayloadAsString());
    }
    
    @AfterClass
    public static void tearDown() throws IOException {
    	FileUtils.deleteDirectory(new File(DIR));
    }

}
