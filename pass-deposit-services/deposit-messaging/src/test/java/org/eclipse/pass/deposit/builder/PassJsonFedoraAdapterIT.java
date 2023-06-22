/*
 * Copyright 2017 Johns Hopkins University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.eclipse.pass.deposit.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static submissions.SubmissionResourceUtil.lookupStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.eclipse.pass.deposit.messaging.config.spring.DepositConfig;
import org.eclipse.pass.deposit.messaging.config.spring.DrainQueueConfig;
import org.eclipse.pass.support.client.PassClient;
import org.eclipse.pass.support.client.model.PassEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DepositConfig.class)
@Import(DrainQueueConfig.class)
@DirtiesContext
public class PassJsonFedoraAdapterIT {

    private URI SAMPLE_DATA_FILE = URI.create("fake:submission1");
    private PassJsonFedoraAdapter adapter;
    private List<PassEntity> entities = new LinkedList<>();

    @Autowired private PassClient passClient;

    @Before
    public void setup() {
        adapter = new PassJsonFedoraAdapter(passClient);
    }

    @Test
    public void roundTrip() {
        try {
            // Upload the sample data to the Fedora repo.
            String submissionId;
            try (InputStream is = lookupStream(SAMPLE_DATA_FILE)) {
                submissionId = adapter.jsonToFcrepo(is, entities).getId();
            }

            // Download the data from the server to a temporary JSON file
            File tempFile = File.createTempFile("fcrepo", ".json");
            tempFile.deleteOnExit();
            String tempFilePath = tempFile.getCanonicalPath();
            // TODO Deposit service port pending
//            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
//                adapter.fcrepoToJson(submissionId, fos);
//            }

            // Read the two files into JSON models
            String origString;
            try (InputStream in = lookupStream(SAMPLE_DATA_FILE)) {
                origString = IOUtils.toString(in, Charset.defaultCharset());
            }
            JsonArray origJson = new JsonParser().parse(origString).getAsJsonArray();

            String resultString;
            try (InputStream in = new FileInputStream(tempFilePath)) {
                resultString = IOUtils.toString(in, Charset.defaultCharset());
            }
            JsonArray resultJson = new JsonParser().parse(resultString).getAsJsonArray();

            // Compare the two files.  Array contents may be in a different order, and URIs have changed,
            // so find objects with same @type field and compare the values of their first properties.
            assertEquals(origJson.size(), resultJson.size());
            for (JsonElement origElement : origJson) {
                boolean found = false;
                JsonObject origObj = origElement.getAsJsonObject();
                String origType = origObj.get("@type").getAsString();
                String firstPropName = origObj.keySet().iterator().next();
                for (JsonElement resultElement : resultJson) {
                    JsonObject resObj = resultElement.getAsJsonObject();
                    if (origType.equals(resObj.get("@type").getAsString()) &&
                        origObj.get(firstPropName).getAsString().equals(resObj.get(firstPropName).getAsString())) {
                        found = true;
                        break;
                    }
                }
                assertTrue("Could not find source entity in target collection.", found);
            }

        } catch (IOException e) {
            e.printStackTrace();
            fail("Could not close the sample data file");
        }
    }

    // TODO Deposit service port pending
//    @After
//    public void tearDown() {
//        // Clean up the server
//        adapter.deleteFromFcrepo(entities);
//    }

}