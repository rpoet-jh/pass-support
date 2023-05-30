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

package org.eclipse.pass.notification.service;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.pass.notification.model.Link;
import org.eclipse.pass.notification.config.LinkValidationRule;
import org.eclipse.pass.notification.config.NotificationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author apb@jhu.edu
 */
public class LinkValidatorTest {

    String REL = "myRel";
    String EXPECTED_BASEURI = "http://example.org";
    URI GOOD_URI = URI.create("http://example.org/whatever");
    URI BAD_URI = URI.create("http://ruminant.moo/whatever");
    Link GOOD_LINK;
    Link BAD_LINK;
    LinkValidationRule RULE;

    private NotificationConfig config;
    private Set<LinkValidationRule> rules;

    @BeforeEach
    public void setUp() {
        GOOD_LINK = new Link(GOOD_URI, REL);
        BAD_LINK = new Link(BAD_URI, REL);

        RULE = new LinkValidationRule();
        RULE.setRels(asList(REL));
        RULE.setRequiredBaseURI(EXPECTED_BASEURI);

        rules = new HashSet<>();

        config = new NotificationConfig();
        config.setLinkValidatorConfig(rules);
    }

    // Make sure a simple rule passing/failing on baseuri does the right the right thing
    @Test
    public void simpleRuleTest() {

        rules.add(RULE);

        // TODO possibly change this to spring test
        final LinkValidator toTest = new LinkValidator(config, new ObjectMapper());
        assertTrue(toTest.test(GOOD_LINK));
        assertFalse(toTest.test(BAD_LINK));
    }

    // If the config says to throw an exception on on invalid links, verify an exception is thrown.
    @Test
    public void throwExceptionIfRequestedTest() {
        RULE.setThrowExceptionOnFailure(true);
        rules.add(RULE);
        final LinkValidator toTest = new LinkValidator(config, new ObjectMapper());
        assertTrue(toTest.test(GOOD_LINK));

        Exception ex = assertThrows(Exception.class, () -> {
            toTest.test(BAD_LINK);
        });
        assertTrue(ex.getMessage().contains(BAD_LINK.toString()));
        assertTrue(ex.getMessage().contains(EXPECTED_BASEURI));
    }

    // If there are no rules defined at all, all links shall pass.
    @Test
    public void noRulesTest() {
        rules.clear();

        final LinkValidator toTest = new LinkValidator(config, new ObjectMapper());
        assertTrue(toTest.test(GOOD_LINK));
        assertTrue(toTest.test(BAD_LINK));
    }

    // Make sure validation rule matching picks up the appropriate rules, and ignores the others.
    @Test
    public void ruleMatchingTest() {

        final String DIFFERENT_REL = "otherRel";

        final LinkValidationRule DIFFERENT_RULE = new LinkValidationRule();
        DIFFERENT_RULE.setRels(asList(DIFFERENT_REL));
        DIFFERENT_RULE.setRequiredBaseURI(BAD_URI.toString());

        rules.addAll(asList(RULE, DIFFERENT_RULE));

        final LinkValidator toTest = new LinkValidator(config, new ObjectMapper());
        assertTrue(toTest.test(GOOD_LINK));
        assertFalse(toTest.test(BAD_LINK));

        GOOD_LINK.setRel(DIFFERENT_REL);
        BAD_LINK.setRel(DIFFERENT_REL);

        assertFalse(toTest.test(GOOD_LINK));
        assertTrue(toTest.test(BAD_LINK));
    }

    // Verify that a rule is applied if it matches _any_ rels in a _list_ of rels.
    @Test
    public void relMatchingTest() {
        final String OTHER_REL_MATCHING = "otherRel";
        final String OTHER_REL_NON_MATCHING = "otherRelNonMatching";

        RULE.setRels(asList(REL, OTHER_REL_MATCHING));

        rules.add(RULE);

        final LinkValidator toTest = new LinkValidator(config, new ObjectMapper());
        assertFalse(toTest.test(BAD_LINK));

        BAD_LINK.setRel(OTHER_REL_NON_MATCHING);
        assertTrue(toTest.test(BAD_LINK));

        BAD_LINK.setRel(OTHER_REL_MATCHING);
        assertFalse(toTest.test(BAD_LINK));
    }

    // Make sure a glob matches everything
    @Test
    public void globMatchingTest() {
        final String OTHER_REL_MATCHING = "otherRel";
        final String OTHER_REL_NON_MATCHING = "otherRelNonMatching";

        RULE.setRels(asList("*"));

        rules.add(RULE);

        final LinkValidator toTest = new LinkValidator(config, new ObjectMapper());
        assertFalse(toTest.test(BAD_LINK));

        BAD_LINK.setRel(OTHER_REL_NON_MATCHING);
        assertFalse(toTest.test(BAD_LINK));

        BAD_LINK.setRel(OTHER_REL_MATCHING);
        assertFalse(toTest.test(BAD_LINK));
    }

    // Null base URI in a rule simply means it won't be tested.
    @Test
    public void nullBaseUriTest() {
        RULE.setRequiredBaseURI(null);
        rules.add(RULE);

        final LinkValidator toTest = new LinkValidator(config, new ObjectMapper());
        assertTrue(toTest.test(GOOD_LINK));
        assertTrue(toTest.test(BAD_LINK));
    }

}
