/*
 * Copyright 2018 Johns Hopkins University
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
package org.eclipse.pass.notification.config;

import static org.eclipse.pass.notification.config.Mode.DEMO;
import static org.eclipse.pass.notification.config.Mode.PRODUCTION;
import static org.eclipse.pass.notification.config.NotificationTemplateName.BODY;
import static org.eclipse.pass.notification.config.NotificationTemplateName.FOOTER;
import static org.eclipse.pass.notification.config.NotificationTemplateName.SUBJECT;
import static org.eclipse.pass.notification.model.NotificationType.SUBMISSION_APPROVAL_INVITE;
import static org.junit.Assert.assertEquals;

import org.eclipse.pass.notification.model.NotificationType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
@TestPropertySource(properties = {
    "pass.notification.configuration=classpath:test-notification.json"
})
public class NotificationConfigTest {

    static {
        System.setProperty("pass.core.url", "localhost:8080");
        System.setProperty("pass.core.url", "localhost:8080");
        System.setProperty("pass.core.url", "localhost:8080");
    }

    @Autowired
    private NotificationConfig notificationConfig;

    @Test
    public void parseJson() {
        assertEquals(DEMO, notificationConfig.getMode());
        assertEquals(2, notificationConfig.getRecipientConfigs().size());
        notificationConfig.getRecipientConfigs()
                .stream().filter(rc -> rc.getMode() == PRODUCTION).findAny()
                .orElseThrow(() -> new RuntimeException("Missing RecipientConfig for mode PRODUCTION."));
        notificationConfig.getRecipientConfigs()
                .stream().filter(rc -> rc.getMode() == DEMO).findAny()
                .orElseThrow(() -> new RuntimeException("Missing RecipientConfig for mode DEMO."));
        assertEquals(1, notificationConfig.getTemplates().size());
        notificationConfig.getTemplates()
                .stream().filter(tc -> tc.getNotificationType() == SUBMISSION_APPROVAL_INVITE).findAny()
                .orElseThrow(() -> new RuntimeException(
                    "Missing NotificationTemplate for type SUBMISSION_APPROVAL_INVITE"));

        NotificationTemplate template = new ArrayList<>(notificationConfig.getTemplates()).get(0);
        assertEquals(NotificationType.SUBMISSION_APPROVAL_INVITE, template.getNotificationType());
        assertEquals(3, template.getTemplates().size());
        assertEquals("PASS Submission Approval: ${RESOURCE_METADATA.title}",
            template.getTemplates().get(SUBJECT));
        assertEquals("classpath*:pass-body-submission-approval-invite-template.vm",
            template.getTemplates().get(BODY));
        assertEquals("classpath*:pass-footer-template.vm", template.getTemplates().get(FOOTER));
    }
}