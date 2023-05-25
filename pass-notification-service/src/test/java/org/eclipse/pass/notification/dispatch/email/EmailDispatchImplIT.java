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
package org.eclipse.pass.notification.dispatch.email;

import static org.apache.commons.io.IOUtils.resourceToString;
import static org.eclipse.pass.notification.util.PathUtil.packageAsPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.pass.notification.NotificationApp;
import org.eclipse.pass.notification.SpringBootIntegrationConfig;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { NotificationApp.class, SpringBootIntegrationConfig.class })
public class EmailDispatchImplIT {

//    private static final Logger LOG = LoggerFactory.getLogger(EmailDispatchImplIT.class);
//
//    private static final String SENDER = "staffWithGrants@jhu.edu";
//
//    private static final String RECIPIENT = "staffWithNoGrants@jhu.edu";
//
//    private static final String CC = "facultyWithGrants@jhu.edu";
//
//    private static final String BCC = "notification-demo-bcc@jhu.edu";
//
//    private static final String GLOBAL_DEMO_CC_ADDRESS = "notification-demo-cc@jhu.edu";
//
//    private static final URI SUBMISSION_RESOURCE_URI =
//            URI.create("http://pass.example.com/fcrepo/rest/submissions/abc/123");
//
//    private static final URI EVENT_RESOURCE_URI =
//            URI.create("http://pass.example.com/fcrepo/rest/submissionEvents/xyz/890");
//
//    @Autowired
//    private EmailDispatchImpl underTest;
//
//    @Autowired
//    private PassClient passClient;
//
//    @Autowired
//    private SimpleImapClientFactory imapClientFactory;
//
//    @Autowired
//    private NotificationConfig config;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private SimpleImapClient imapClient;
//
//    @Before
//    public void setUp() throws Exception {
//        imapClientFactory.setImapUser(RECIPIENT);
//        imapClientFactory.setImapPass("moo");
//        imapClient = imapClientFactory.getObject();
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        imapClient.close();
//    }
//
//    /**
//     * Simple test insuring the basic parts of the dispatch email are where they belong.
//     */
//    @Test
//    public void simpleSuccess() throws Exception {
//        String expectedBody = "Approval Invite Body\r\n\r\nApproval Invite Footer";
//
//        Notification n = new Notification();
//        n.setType(SUBMISSION_APPROVAL_INVITE);
//        n.setSender(SENDER);
//        n.setCc(singleton(CC));
//        n.setRecipients(singleton("mailto:" + RECIPIENT));
//        n.setResourceUri(SUBMISSION_RESOURCE_URI);
//        n.setEventUri(EVENT_RESOURCE_URI);
//
//        String messageId = underTest.dispatch(n);
//        Assert.assertNotNull(messageId);
//
//        Condition.newGetMessageCondition(messageId, imapClient).await();
//        Message message = Condition.getMessage(messageId, imapClient).call();
//        Assert.assertNotNull(message);
//
//        Assert.assertEquals("Approval Invite Subject", message.getSubject());
//        Assert.assertEquals(expectedBody, getBodyAsText(message));
//        Assert.assertEquals(SENDER, message.getFrom()[0].toString());
//        Assert.assertEquals(CC, message.getRecipients(Message.RecipientType.CC)[0].toString());
//        Assert.assertEquals(RECIPIENT, message.getRecipients(Message.RecipientType.TO)[0].toString());
//    }
//
//    /**
//     * Simple test insuring that BCC users receive their notification
//     */
//    @Test
//    public void simpleBccSuccess() throws Exception {
//        String expectedBody = "Approval Invite Body\r\n\r\nApproval Invite Footer";
//
//        Notification n = new Notification();
//        n.setType(SUBMISSION_APPROVAL_INVITE);
//        n.setSender(SENDER);
//        n.setBcc(singleton(BCC));
//        n.setRecipients(singleton("mailto:" + RECIPIENT));
//        n.setResourceUri(SUBMISSION_RESOURCE_URI);
//        n.setEventUri(EVENT_RESOURCE_URI);
//
//        String messageId = underTest.dispatch(n);
//        Assert.assertNotNull(messageId);
//
//        // Original recipient should have the message
//        Condition.newGetMessageCondition(messageId, imapClient).await();
//        Message recipientMsg = Condition.getMessage(messageId, imapClient).call();
//        Assert.assertNotNull(recipientMsg);
//        Assert.assertEquals("Approval Invite Subject", recipientMsg.getSubject());
//        Assert.assertEquals(expectedBody, getBodyAsText(recipientMsg));
//        Assert.assertEquals(SENDER, recipientMsg.getFrom()[0].toString());
//        Assert.assertEquals(RECIPIENT, recipientMsg.getRecipients(Message.RecipientType.TO)[0].toString());
//        Assert.assertNull(recipientMsg.getRecipients(Message.RecipientType.CC));
//        Assert.assertNull(recipientMsg.getRecipients(Message.RecipientType.BCC));
//
//        // must use a unique imap client instance for the BCC user (factory state is reset in setup)
//        imapClientFactory.setImapUser(BCC);
//        imapClientFactory.setImapPass("moo");
//        try (SimpleImapClient bccImapClient = imapClientFactory.getObject()) {
//            Condition.newGetMessageCondition(messageId, bccImapClient).await();
//            Message message = Condition.getMessage(messageId, bccImapClient).call();
//            Assert.assertNotNull(message);
//
//            Assert.assertEquals("Approval Invite Subject", message.getSubject());
//            Assert.assertEquals(expectedBody, getBodyAsText(message));
//            Assert.assertEquals(SENDER, message.getFrom()[0].toString());
//            Assert.assertEquals(RECIPIENT, message.getRecipients(Message.RecipientType.TO)[0].toString());
//        }
//    }
//
//    /**
//     * Dispatching a notification with a PASS User URI as a recipient should result in the proper resolution of the
//     * {@code to} recipient.
//     */
//    @Test
//    public void dispatchResolveUserUri() throws Exception {
//        User recipientUser = new User();
//        recipientUser.setEmail(RECIPIENT);
//        URI recipientUri = passClient.createResource(recipientUser);
//
//        Notification n = new Notification();
//        n.setType(SUBMISSION_APPROVAL_INVITE);
//        n.setSender(SENDER);
//        n.setRecipients(singleton(recipientUri.toString()));
//        n.setResourceUri(SUBMISSION_RESOURCE_URI);
//        n.setEventUri(EVENT_RESOURCE_URI);
//
//        String messageId = underTest.dispatch(n);
//
//        Condition.newGetMessageCondition(messageId, imapClient).await();
//        Message message = Condition.getMessage(messageId, imapClient).call();
//
//        Assert.assertEquals(RECIPIENT, message.getRecipients(Message.RecipientType.TO)[0].toString());
//    }
//
//    /**
//     * References to subject/body/footer templates should be resolved
//     */
//    @Test
//    @DirtiesContext
//    public void notificationConfigWithTemplateRefs() throws Exception {
//
//        // Override the NotificationTemplate for approval invites, subbing in Spring URIs as references
//        // to template bodies
//        NotificationTemplate template = config.getTemplates().stream()
//                .filter(templatePrototype ->
//                        templatePrototype.getNotificationType() == SUBMISSION_APPROVAL_INVITE)
//                .findAny()
//                .orElseThrow(() -> new RuntimeException("Missing expected template for SUBMISSION_APPROVAL_INVITE"));
//
//        template.setTemplates(new HashMap<NotificationTemplate.Name, String>() {
//            {
//                put(NotificationTemplate.Name.SUBJECT, "classpath:" + PathUtil.packageAsPath() + "/subject.hbr");
//                put(NotificationTemplate.Name.BODY, "classpath:" + PathUtil.packageAsPath() + "/body.hbr");
//                put(NotificationTemplate.Name.FOOTER, "classpath:" + PathUtil.packageAsPath() + "/footer.hbr");
//            }
//        });
//
//        config.setTemplates(singleton(template));
//
//        Notification n = new Notification();
//        n.setType(SUBMISSION_APPROVAL_INVITE);
//        n.setSender(SENDER);
//        n.setResourceUri(SUBMISSION_RESOURCE_URI);
//        n.setEventUri(EVENT_RESOURCE_URI);
//        n.setRecipients(singleton("mailto:" + RECIPIENT));
//
//        String messageId = underTest.dispatch(n);
//        Assert.assertNotNull(messageId);
//
//        Condition.newGetMessageCondition(messageId, imapClient).await();
//        Message message = Condition.getMessage(messageId, imapClient).call();
//        Assert.assertNotNull(message);
//
//        Assert.assertEquals("Handlebars Subject", message.getSubject());
//        Assert.assertEquals("Handlebars Body\r\n\r\nHandlebars Footer", getBodyAsText(message));
//    }
//
//    @Test
//    public void subjectTemplateParameterization() throws Exception {
//        Submission submission = new Submission();
//        submission.setMetadata(resourceToString("/" + PathUtil.packageAsPath(ComposerIT.class) +
//                                                "/submission-metadata.json", forName("UTF-8")));
//        submission.setId(SUBMISSION_RESOURCE_URI);
//
//        SubmissionEvent event = new SubmissionEvent();
//        event.setId(URI.create("http://example.org/event/1"));
//        event.setPerformerRole(SubmissionEvent.PerformerRole.PREPARER);
//        event.setPerformedBy(URI.create("http://example.org/user/1"));
//        event.setComment("How does this submission look?");
//        event.setEventType(SubmissionEvent.EventType.APPROVAL_REQUESTED_NEWUSER);
//        event.setPerformedDate(DateTime.now());
//        event.setSubmission(SUBMISSION_RESOURCE_URI);
//
//        // Override the NotificationTemplate for approval invites, including a template that
//        // requires parameterization
//        NotificationTemplate template = config.getTemplates().stream()
//                .filter(templatePrototype ->
//                        templatePrototype.getNotificationType() == SUBMISSION_APPROVAL_INVITE)
//                .findAny()
//                .orElseThrow(() -> new RuntimeException("Missing expected template for SUBMISSION_APPROVAL_INVITE"));
//
//        template.setTemplates(new HashMap<NotificationTemplate.Name, String>() {
//            {
//                put(NotificationTemplate.Name.SUBJECT, "classpath:" + PathUtil.packageAsPath() + "/subject-parameterize.hbr");
//                put(NotificationTemplate.Name.BODY, "classpath:" + PathUtil.packageAsPath() + "/body-parameterize.hbr");
//                put(NotificationTemplate.Name.FOOTER, "Footer");
//            }
//        });
//
//        config.setTemplates(singleton(template));
//
//        Link link = new Link(URI.create("http://example.org/email/dispatch/myLink"), SUBMISSION_REVIEW_INVITE);
//
//        Notification n = new Notification();
//        n.setType(SUBMISSION_APPROVAL_INVITE);
//        n.setSender(SENDER);
//        n.setRecipients(singleton("mailto:" + RECIPIENT));
//        n.setEventUri(event.getId());
//        n.setResourceUri(submission.getId());
//        n.setParameters(new HashMap<Notification.Param, String>() {
//            {
//                put(RESOURCE_METADATA, Composer.resourceMetadata(submission, objectMapper));
//                put(EVENT_METADATA, Composer.eventMetadata(event, objectMapper));
//                put(FROM, SENDER);
//                put(TO, RECIPIENT);
//                put(LINKS, asList(link).stream().collect(serialized()));
//            }
//        });
//
//        String messageId = underTest.dispatch(n);
//        Assert.assertNotNull(messageId);
//
//        Condition.newGetMessageCondition(messageId, imapClient).await();
//        Message message = Condition.getMessage(messageId, imapClient).call();
//        Assert.assertNotNull(message);
//
//        String expectedTitle = objectMapper.readTree(submission.getMetadata()).findValue("title").asText();
//        String expectedSubject = "PASS Submission titled \"" + expectedTitle + "\" awaiting your approval";
//        Assert.assertEquals(expectedSubject, message.getSubject());
//
//        String body = getBodyAsText(message);
//
//        Assert.assertTrue(body.contains("Dear " + n.getParameters().get(TO)));
//        // todo: FROM will be the global FROM, must insure the preparer User is represented in metadata.
//        Assert.assertTrue(body.contains("prepared on your behalf by " + n.getParameters().get(FROM)));
//        Assert.assertTrue(body.contains(event.getComment()));
//        Assert.assertTrue(body.contains(expectedTitle));
//        Assert.assertTrue(body.contains("Please review the submission at the following URL: " + link.getHref()));
//    }
//
//    /**
//     * mailing a non-existent email address should result in the appropriate exception
//     * (in coordination with a like-minded email relay)
//     */
//    @Test
//    public void nonExistentEmailAddress() {
//        String nonExistentRecipientAddress = "moo-thru@bar.edu";
//        Notification n = new Notification();
//        n.setType(SUBMISSION_APPROVAL_INVITE);
//        n.setSender(SENDER);
//        n.setResourceUri(SUBMISSION_RESOURCE_URI);
//        n.setEventUri(EVENT_RESOURCE_URI);
//        n.setRecipients(singleton("mailto:" + nonExistentRecipientAddress));
//
//        try {
//            underTest.dispatch(n);
//        } catch (Exception e) {
//            Assert.assertTrue(e instanceof DispatchException);
//            Throwable rootCause = e.getCause();
//            boolean sfeFound = false;
//            while (rootCause.getCause() != null) {
//                if (rootCause instanceof javax.mail.SendFailedException) {
//                    sfeFound = true;
//                    break;
//                }
//                rootCause = rootCause.getCause();
//            }
//
//            Assert.assertTrue("Missing expected javax.mail.SendFailedException in the stack trace.", sfeFound);
//            Assert.assertTrue("Expected the string 'Invalid Addresses' to be in the exception message.",
//                       rootCause.getMessage().contains("Invalid Addresses"));
//
//            return;
//        }
//
//        Assert.fail("Expected a DispatchException to be thrown.");
//    }
//
//    /**
//     * When dispatching a Notification with a non-empty whitelist, only those whitelisted recipients should be present
//     * on the email that is sent
//     */
//    @Test
//    public void testWhitelistFilter() throws Exception {
//        String unlistedRecipient = "mailto:facultyWithNoGrants@jhu.edu";
//        Notification n = new Notification();
//        n.setType(SUBMISSION_APPROVAL_INVITE);
//        n.setSender(SENDER);
//        n.setResourceUri(SUBMISSION_RESOURCE_URI);
//        n.setEventUri(EVENT_RESOURCE_URI);
//        n.setCc(singleton(CC));
//        n.setRecipients(Arrays.asList("mailto:" + RECIPIENT, unlistedRecipient));
//
//        Assert.assertTrue(recipientConfig(config).getWhitelist().contains(RECIPIENT));
//        Assert.assertFalse(recipientConfig(config).getWhitelist().contains(unlistedRecipient));
//
//        String messageId = underTest.dispatch(n);;
//        Condition.newGetMessageCondition(messageId, imapClient).await();
//        Message message = Condition.getMessage(messageId, imapClient).call();
//        Assert.assertNotNull(message);
//
//        // Only the whitelisted recipient should be present
//        Assert.assertEquals(1, message.getRecipients(Message.RecipientType.TO).length);
//        Assert.assertEquals(RECIPIENT, message.getRecipients(Message.RecipientType.TO)[0].toString());
//    }
//
//    /**
//     * When composing a Notification, the global CC addresses should not be filtered by the whitelist, while the direct
//     * recipients are.
//     */
//    @Test
//    @DirtiesContext
//    public void testGlobalCCUnaffectedByWhitelist() throws Exception {
//        RecipientConfig recipientConfig = recipientConfig(config);
//
//        // Configure the whitelist such that the submitter's address will
//        // *not* be whitelisted
//        String whitelistEmail = RECIPIENT;
//        recipientConfig.setWhitelist(singleton(whitelistEmail));
//        underTest.getComposer().setWhitelist(new SimpleWhitelist(recipientConfig));
//
//        Assert.assertTrue(recipientConfig(config).getWhitelist().contains(RECIPIENT));
//        Assert.assertFalse(recipientConfig(config).getWhitelist().contains("facultyWithNoGrants@jhu.edu"));
//
//        Notification n = new Notification();
//        n.setType(SUBMISSION_APPROVAL_INVITE);
//        n.setSender(SENDER);
//        n.setResourceUri(SUBMISSION_RESOURCE_URI);
//        n.setEventUri(EVENT_RESOURCE_URI);
//        n.setRecipients(Arrays.asList("mailto:facultyWithNoGrants@jhu.edu", "mailto:" + whitelistEmail));
//        n.setCc(singleton(GLOBAL_DEMO_CC_ADDRESS));
//
//        String messageId = underTest.dispatch(n);
//        Condition.newGetMessageCondition(messageId, imapClient).await();
//        Message message = Condition.getMessage(messageId, imapClient).call();
//        Assert.assertNotNull(message);
//
//        // The recipient list doesn't contain facultyWithNoGrants@jhu.edu because it isn't whitelisted
//        Assert.assertEquals(1, message.getRecipients(Message.RecipientType.TO).length);
//        Assert.assertEquals(whitelistEmail, message.getRecipients(Message.RecipientType.TO)[0].toString());
//
//        // The cc list does contain the expected address, because the global cc is not filtered through the whitelist
//        // at all
//        Assert.assertEquals(1, message.getRecipients(Message.RecipientType.CC).length);
//        Assert.assertEquals(GLOBAL_DEMO_CC_ADDRESS, message.getRecipients(Message.RecipientType.CC)[0].toString());
//    }
//
//    /**
//     * Insure that the proper whitelist is used for the specified mode
//     */
//    @Test
//    @DirtiesContext
//    public void testRecipientConfigForEachMode() {
//        // make a unique whitelist and recipient config for each possible mode
//        HashMap<Mode, RecipientConfig> rcs = new HashMap<>();
//        Arrays.stream(Mode.values()).forEach(m -> {
//            RecipientConfig rc = new RecipientConfig();
//            rc.setMode(m);
//            rc.setWhitelist(new ArrayList<>(1));
//            rcs.put(m, rc);
//        });
//
//        config.setRecipientConfigs(rcs.values());
//
//        Arrays.stream(Mode.values()).forEach(mode -> {
//            config.setMode(mode);
//            Assert.assertEquals(mode, recipientConfig(config).getMode());
//        });
//    }
//
//    /**
//     * When composing a Notification with an empty whitelist, every recipient should be present.
//     */
//    @Test
//    @DirtiesContext
//    public void testEmptyWhitelist() throws Exception {
//        RecipientConfig recipientConfig = recipientConfig(config);
//        recipientConfig.setWhitelist(Collections.emptyList());
//        underTest.getComposer().setWhitelist(new SimpleWhitelist(recipientConfig));
//
//        String secondRecipient = "facultyWithNoGrants@jhu.edu";
//        Notification n = new Notification();
//        n.setType(SUBMISSION_APPROVAL_INVITE);
//        n.setSender(SENDER);
//        n.setResourceUri(SUBMISSION_RESOURCE_URI);
//        n.setEventUri(EVENT_RESOURCE_URI);
//        n.setCc(Arrays.asList(CC, GLOBAL_DEMO_CC_ADDRESS));
//        n.setRecipients(Arrays.asList("mailto:" + RECIPIENT, "mailto:" + secondRecipient));
//
//        String messageId = underTest.dispatch(n);
//
//        Condition.newGetMessageCondition(messageId, imapClient).await();
//        Message message = Condition.getMessage(messageId, imapClient).call();
//        Assert.assertNotNull(message);
//
//        Collection<String> actualRecipients = Arrays.stream(message.getAllRecipients())
//                                                    .map(Object::toString)
//                                                    .collect(Collectors.toSet());
//
//        Assert.assertTrue(actualRecipients.contains(RECIPIENT));
//        Assert.assertTrue(actualRecipients.contains(secondRecipient));
//        Assert.assertTrue(actualRecipients.contains(CC));
//        Assert.assertTrue(actualRecipients.contains(GLOBAL_DEMO_CC_ADDRESS));
//        Assert.assertEquals(4, actualRecipients.size());
//
//        imapClientFactory.setImapUser(secondRecipient);
//        Message secondMessage;
//        try (SimpleImapClient facultyClient = imapClientFactory.getObject()) {
//            Condition.newGetMessageCondition(messageId, facultyClient).await();
//            secondMessage = Condition.getMessage(messageId, facultyClient).call();
//            Assert.assertNotNull(secondMessage);
//            actualRecipients = Arrays.stream(secondMessage.getAllRecipients())
//                                     .map(Object::toString)
//                                     .collect(Collectors.toSet());
//        }
//
//        Assert.assertTrue(actualRecipients.contains(RECIPIENT));
//        Assert.assertTrue(actualRecipients.contains(secondRecipient));
//        Assert.assertTrue(actualRecipients.contains(CC));
//        Assert.assertTrue(actualRecipients.contains(GLOBAL_DEMO_CC_ADDRESS));
//        Assert.assertEquals(4, actualRecipients.size());
//    }
//
//    private static RecipientConfig recipientConfig(NotificationConfig config) {
//        return config.getRecipientConfigs()
//                .stream()
//                .filter(rc -> rc.getMode() == config.getMode())
//                .findAny()
//                .orElseThrow(() -> new RuntimeException("Missing RecipientConfig for mode '" + config.getMode() + "'"));
//    }
}
