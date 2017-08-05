/*
 * Copyright 2017 Coveros, Inc.
 * 
 * This file is part of Selenified.
 * 
 * Selenified is licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy 
 * of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on 
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY 
 * KIND, either express or implied. See the License for the 
 * specific language governing permissions and limitations 
 * under the License.
 */

package com.coveros.selenified.output;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.coveros.selenified.selenium.Action;
import com.coveros.selenified.selenium.Element;
import com.coveros.selenified.selenium.Excludes;
import com.coveros.selenified.selenium.Selenium.Browser;
import com.coveros.selenified.selenium.Selenium.Locator;
import com.coveros.selenified.selenium.State;
import com.coveros.selenified.selenium.Contains;
import com.coveros.selenified.tools.General;

/**
 * A custom reporting class, which provides logging and screenshots for all
 * verifications. Meant to provide additional traceability on top of TestNG
 * Asserts
 *
 * @author Max Saperstone
 * @version 2.0.1
 * @lastupdate 7/20/2017
 */
public class Assert {

    private Action action;
    private OutputFile outputFile;

    private LocatorAssert locatorAssert;

    // the is class to determine the state of an element
    private State state;
    // the is class to determine if an element contains something
    private Contains contains;
    // the is class to determine if an element doesn't contain something
    private Excludes excludes;

    // constants
    private static final String ONPAGE = "</b> on the page";
    private static final String NOALERT = "No alert is present on the page";
    private static final String ALERTTEXT = "An alert with text <b>";
    private static final String NOCONFIRMATION = "No confirmation is present on the page";
    private static final String CONFIRMATIONTEXT = "A confirmation with text <b>";
    private static final String NOPROMPT = "No prompt is present on the page";
    private static final String PROMPTTEXT = "A prompt with text <b>";

    private static final String STORED = "</b> is stored for the page";
    private static final String VALUE = "</b> and a value of <b>";
    private static final String COOKIE = "A cookie with the name <b>";
    private static final String NOCOOKIE = "No cookie with the name <b>";

    private static final String TEXT = "The text <b>";
    private static final String PRESENT = "</b> is present on the page";
    private static final String VISIBLE = "</b> is visible on the page";

    //////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////

    /**
     * the detailed test results constructor that will setup the our test output
     * file for all documentation and information for browser based testing
     *
     * @param testsName
     *            the name of the file we will write out to
     * @param browser
     *            the browser we are running the tests with
     * @param outputDir
     *            the output directory to store the results
     */
    public Assert(String outputDir, String testsName, Browser browser) {
        outputFile = new OutputFile(outputDir, testsName, browser);
        state = new State(action, outputFile);
        contains = new Contains(action, outputFile);
        excludes = new Excludes(action, outputFile);
    }

    public Assert(String outputDir, String testsName, String serviceURL) {
        outputFile = new OutputFile(outputDir, testsName, serviceURL);
        state = new State(action, outputFile);
        contains = new Contains(action, outputFile);
        excludes = new Excludes(action, outputFile);
    }

    public void setAction(Action action) {
        this.action = action;
        locatorAssert = new LocatorAssert(action, outputFile);
        state.setAction(action);
        contains.setAction(action);
        excludes.setAction(action);
    }

    public void setOutputFile(OutputFile thisOutputFile) {
        outputFile = thisOutputFile;
        state.setOutputFile(outputFile);
        contains.setOutputFile(outputFile);
        excludes.setOutputFile(outputFile);

    }

    public OutputFile getOutputFile() {
        return outputFile;
    }

    ///////////////////////////////////////////////////////
    // instantiating our additional assert classes for further use
    ///////////////////////////////////////////////////////

    public State state() {
        return state;
    }

    public Contains contains() {
        return contains;
    }

    public Excludes excludes() {
        return excludes;
    }

    ///////////////////////////////////////////////////////
    // some basic asserts
    ///////////////////////////////////////////////////////

    /**
     * checks to see if an alert is correct on the page
     *
     * @param expectedAlert
     *            the expected text of the alert
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int checkAlert(String expectedAlert) {
        // outputFile.record the action
        outputFile.recordExpected("Expected to find alert with the text <b>" + expectedAlert + ONPAGE);
        // check for the object to the visible
        String alert = "";
        boolean isAlertPresent = action.is().alertPresent();
        if (isAlertPresent) {
            alert = action.get().alert();
        }
        if (!isAlertPresent) {
            outputFile.recordActual(NOALERT, Success.FAIL);
            outputFile.addError();
            return 1;
        }
        Pattern patt = Pattern.compile(expectedAlert);
        Matcher m = patt.matcher(alert);
        boolean isCorrect;
        if (expectedAlert.contains("\\")) {
            isCorrect = m.matches();
        } else {
            isCorrect = alert.equals(expectedAlert);
        }
        if (!isCorrect) {
            outputFile.recordActual(ALERTTEXT + alert + PRESENT, Success.FAIL);
            outputFile.addError();
            return 1;
        }
        outputFile.recordActual(ALERTTEXT + alert + PRESENT, Success.PASS);
        return 0;
    }

    /**
     * checks to see if an alert is present on the page
     *
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int checkAlertPresent() {
        // outputFile.record the action
        outputFile.recordExpected("Expected to find an alert on the page");
        // check for the object to the visible
        String alert = "";
        boolean isAlertPresent = action.is().alertPresent();
        if (isAlertPresent) {
            alert = action.get().alert();
        }
        if (!isAlertPresent) {
            outputFile.recordActual(NOALERT, Success.FAIL);
            outputFile.addError();
            return 1;
        }
        outputFile.recordActual(ALERTTEXT + alert + PRESENT, Success.PASS);
        return 0;
    }

    // /////////////////////////////////////////////////////////////////////////
    // a bunch of methods to positively check for objects using selenium calls
    // ///////////////////////////////////////////////////////////////////////

    /**
     * checks to see if an alert is not present on the page
     *
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int checkAlertNotPresent() {
        // outputFile.record the action
        outputFile.recordExpected("Expected not to find an alert on the page");
        // check for the object to the visible
        boolean isAlertPresent = action.is().alertPresent();
        if (isAlertPresent) {
            outputFile.recordActual("An alert is present on the page", Success.FAIL);
            outputFile.addError();
            return 1;
        }
        outputFile.recordActual(NOALERT, Success.PASS);
        return 0;
    }

    // /////////////////////////////////////////////////////////////////////////
    // a bunch of methods to negatively check for objects using selenium calls
    // ///////////////////////////////////////////////////////////////////////

    /**
     * checks to see if a confirmation is correct on the page
     *
     * @param expectedConfirmation
     *            the expected text of the confirmation
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int checkConfirmation(String expectedConfirmation) {
        // outputFile.record the action
        outputFile.recordExpected("Expected to find confirmation with the text <b>" + expectedConfirmation + ONPAGE);
        // check for the object to the visible
        String confirmation = "";
        boolean isConfirmationPresent = action.is().confirmationPresent();
        if (isConfirmationPresent) {
            confirmation = action.get().confirmation();
        }
        if (!isConfirmationPresent) {
            outputFile.recordActual(NOCONFIRMATION, Success.FAIL);
            outputFile.addError();
            outputFile.addError();
            return 1;
        }
        if (!expectedConfirmation.equals(confirmation)) {
            outputFile.recordActual(CONFIRMATIONTEXT + confirmation + PRESENT, Success.FAIL);
            outputFile.addError();
            return 1;
        }
        outputFile.recordActual(CONFIRMATIONTEXT + confirmation + PRESENT, Success.PASS);
        return 0;
    }

    /**
     * checks to see if a confirmation is present on the page
     *
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int checkConfirmationPresent() {
        // outputFile.record the action
        outputFile.recordExpected("Expected to find a confirmation on the page");
        // check for the object to the visible
        String confirmation = "";
        boolean isConfirmationPresent = action.is().confirmationPresent();
        if (isConfirmationPresent) {
            confirmation = action.get().confirmation();
        }
        if (!isConfirmationPresent) {
            outputFile.recordActual(NOCONFIRMATION, Success.FAIL);
            outputFile.addError();
            return 1;
        }
        outputFile.recordActual(CONFIRMATIONTEXT + confirmation + PRESENT, Success.PASS);
        return 0;
    }

    /**
     * checks to see if a confirmation is not present on the page
     *
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int checkConfirmationNotPresent() {
        // outputFile.record the action
        outputFile.recordExpected("Expected to find a confirmation on the page");
        // check for the object to the visible
        boolean isConfirmationPresent = action.is().confirmationPresent();
        if (isConfirmationPresent) {
            outputFile.recordActual("A confirmation is present on the page", Success.FAIL);
            outputFile.addError();
            return 1;
        }
        outputFile.recordActual(NOCONFIRMATION, Success.PASS);
        return 0;
    }

    /**
     * checks to see if a prompt is correct on the page
     *
     * @param expectedPrompt
     *            the expected text of the confirmation
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int checkPrompt(String expectedPrompt) {
        // outputFile.record the action
        outputFile.recordExpected("Expected to find prompt with the text <b>" + expectedPrompt + ONPAGE);
        // check for the object to the visible
        String prompt = "";
        boolean isPromptPresent = action.is().promptPresent();
        if (isPromptPresent) {
            prompt = action.get().prompt();
        }
        if (!isPromptPresent) {
            outputFile.recordActual(NOPROMPT, Success.FAIL);
            outputFile.addError();
            return 1;
        }
        if (!expectedPrompt.equals(prompt)) {
            outputFile.recordActual(PROMPTTEXT + prompt + PRESENT, Success.FAIL);
            outputFile.addError();
            return 1;
        }
        outputFile.recordActual(PROMPTTEXT + prompt + PRESENT, Success.PASS);
        return 0;
    }

    /**
     * checks to see if a prompt is present on the page
     *
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int checkPromptPresent() {
        // outputFile.record the action
        outputFile.recordExpected("Expected to find prompt on the page");
        // check for the object to the visible
        String prompt = "";
        boolean isPromptPresent = action.is().promptPresent();
        if (isPromptPresent) {
            prompt = action.get().prompt();
        }
        if (!isPromptPresent) {
            outputFile.recordActual(NOPROMPT, Success.FAIL);
            outputFile.addError();
            return 1;
        }
        outputFile.recordActual(PROMPTTEXT + prompt + PRESENT, Success.PASS);
        return 0;
    }

    /**
     * checks to see if a prompt is not present on the page
     *
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int checkPromptNotPresent() {
        // outputFile.record the action
        outputFile.recordExpected("Expected not to find prompt on the page");
        // check for the object to the visible
        boolean isPromptPresent = action.is().promptPresent();
        if (isPromptPresent) {
            outputFile.recordActual("A prompt is present on the page", Success.FAIL);
            outputFile.addError();
            return 1;
        }
        outputFile.recordActual(NOPROMPT, Success.PASS);
        return 0;
    }

    /**
     * checks to see if a cookie is correct for the page
     *
     * @param cookieName
     *            the name of the cookie
     * @param expectedCookieValue
     *            the expected value of the cookie
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int checkCookie(String cookieName, String expectedCookieValue) {
        // outputFile.record the action
        outputFile.recordExpected(
                "Expected to find cookie with the name <b>" + cookieName + VALUE + expectedCookieValue + STORED);
        // check for the object to the visible
        String cookieValue = "";
        boolean isCookiePresent = action.is().cookiePresent(cookieName);
        if (isCookiePresent) {
            cookieValue = action.get().cookieValue(cookieName);
        }
        if (!isCookiePresent) {
            outputFile.recordActual(NOCOOKIE + cookieName + STORED, Success.FAIL);
            outputFile.addError();
            return 1;
        }
        if (!cookieValue.equals(expectedCookieValue)) {
            outputFile.recordActual(COOKIE + cookieName + "</b> is stored for the page, but the value "
                    + "of the cookie is " + cookieValue, Success.FAIL);
            outputFile.addError();
            return 1;
        }
        outputFile.recordActual(COOKIE + cookieName + VALUE + cookieValue + STORED, Success.PASS);
        return 0;
    }

    /**
     * checks to see if a cookie is present on the page
     *
     * @param expectedCookieName
     *            the name of the cookie
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int checkCookiePresent(String expectedCookieName) {
        // outputFile.record the action
        outputFile.recordExpected("Expected to find cookie with the name <b>" + expectedCookieName + STORED);
        // check for the object to the visible
        String cookieValue = "";
        boolean isCookiePresent = action.is().cookiePresent(expectedCookieName);
        if (isCookiePresent) {
            cookieValue = action.get().cookieValue(expectedCookieName);
        }
        if (!isCookiePresent) {
            outputFile.recordActual(NOCOOKIE + expectedCookieName + STORED, Success.FAIL);
            outputFile.addError();
            return 1;
        }
        outputFile.recordActual(COOKIE + expectedCookieName + VALUE + cookieValue + STORED, Success.PASS);
        return 0;
    }

    /**
     * checks to see if a cookie is not present on the page
     *
     * @param unexpectedCookieName
     *            the name of the cookie
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int checkCookieNotPresent(String unexpectedCookieName) {
        // outputFile.record the action
        outputFile.recordExpected("Expected to find no cookie with the name <b>" + unexpectedCookieName + STORED);
        // check for the object to the visible
        boolean isCookiePresent = action.is().cookiePresent(unexpectedCookieName);
        if (isCookiePresent) {
            outputFile.recordActual(COOKIE + unexpectedCookieName + STORED, Success.FAIL);
            outputFile.addError();
            return 1;
        }
        outputFile.recordActual(NOCOOKIE + unexpectedCookieName + STORED, Success.PASS);
        return 0;
    }

    /**
     * checks to see if text is visible on the page
     *
     * @param expectedTexts
     *            the expected text to be visible
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int checkTextVisible(String... expectedTexts) {
        // outputFile.record the action
        int errors = 0;
        for (String expectedText : expectedTexts) {
            outputFile.recordExpected("Expected to find text <b>" + expectedText + "</b> visible on the page");
            // check for the object to the visible
            boolean isPresent = action.is().textPresent(expectedText);
            if (!isPresent) {
                outputFile.recordActual(TEXT + expectedText + "</b> is not visible on the page", Success.FAIL);
                outputFile.addError();
                errors++;
            } else {
                outputFile.recordActual(TEXT + expectedText + VISIBLE, Success.PASS);
            }
        }
        return errors;
    }

    /**
     * checks to see if text is not visible on the page
     *
     * @param expectedTexts
     *            the expected text to be invisible
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int checkTextNotVisible(String... expectedTexts) {
        // outputFile.record the action
        int errors = 0;
        for (String expectedText : expectedTexts) {
            outputFile.recordExpected("Expected not to find text <b>" + expectedText + "</b> visible on the page");
            // check for the object to the visible
            boolean isPresent = action.is().textPresent(expectedText);
            if (isPresent) {
                outputFile.recordActual(TEXT + expectedText + VISIBLE, Success.FAIL);
                outputFile.addError();
                errors++;
            } else {
                outputFile.recordActual(TEXT + expectedText + "</b> is not visible on the page", Success.PASS);
            }
        }
        return errors;
    }

    /**
     * checks to see if text is visible on the page
     *
     * @param expectedTexts
     *            the expected text to be visible
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int checkTextVisibleOR(String... expectedTexts) {
        // outputFile.record the action
        int errors = 0;
        boolean isPresent = false;
        String foundText = "";
        StringBuilder allTexts = new StringBuilder();
        for (String expectedText : expectedTexts) {
            allTexts.append("<b>" + expectedText + "</b> or ");
        }
        allTexts.setLength(allTexts.length() - 4);
        outputFile.recordExpected("Expected to find text " + allTexts + " visible on the page");
        // check for the object to the visible
        for (String expectedText : expectedTexts) {
            isPresent = action.is().textPresent(expectedText);
            if (isPresent) {
                foundText = expectedText;
                break;
            }
        }
        if (!isPresent) {
            outputFile.recordActual(
                    "None of the texts " + allTexts.toString().replace(" or ", ", ") + " are visible on the page",
                    Success.FAIL);
            outputFile.addError();
            errors++;
            return errors;
        }
        outputFile.recordActual(TEXT + foundText + VISIBLE, Success.PASS);
        return errors;
    }

    /**
     * compares the actual title a page is on to the expected title
     *
     * @param expectedTitle
     *            the friendly name of the page
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareTitle(String expectedTitle) {
        // outputFile.record the action
        outputFile.recordExpected("Expected to be on page with the title of <i>" + expectedTitle + "</i>");
        String actualTitle = action.get().title();
        if (!actualTitle.equalsIgnoreCase(expectedTitle)) {
            outputFile.recordActual("The page title reads <b>" + actualTitle + "</b>", Success.FAIL);
            outputFile.addError();
            return 1;
        }
        outputFile.recordActual("The page title reads <b>" + actualTitle + "</b>", Success.PASS);
        return 0;
    }

    /**
     * compares the actual URL a page is on to the expected URL
     *
     * @param expectedURL
     *            the URL of the page
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareURL(String expectedURL) {
        // outputFile.record the action
        outputFile.recordExpected("Expected to be on page with the URL of <i>" + expectedURL + "</i>");
        String actualURL = action.get().location();
        if (!actualURL.equalsIgnoreCase(expectedURL)) {
            outputFile.recordActual("The page URL  reads <b>" + actualURL + "</b>", Success.FAIL);
            outputFile.addError();
            return 1;
        }
        outputFile.recordActual("The page URL reads <b>" + actualURL + "</b>", Success.PASS);
        return 0;
    }

    /**
     * checks to see if an element has a particular class
     *
     * @param element
     *            - the element to be waited for
     * @param expectedClass
     *            - the full expected class value
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int checkElementHasClass(Element element, String expectedClass) {
        return checkElementHasClass(element.getType(), element.getLocator(), 0, expectedClass);
    }

    /**
     * checks to see if an element has a particular class
     *
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param expectedClass
     *            - the full expected class value
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int checkElementHasClass(Locator type, String locator, String expectedClass) {
        return checkElementHasClass(type, locator, 0, expectedClass);
    }

    /**
     * checks to see if an element has a particular class
     *
     * @param element
     *            - the element to be waited for
     * @param elementMatch
     *            - if there are multiple matches of the selector, this is which
     *            match (starting at 0) to interact with
     * @param expectedClass
     *            - the full expected class value
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int checkElementHasClass(Element element, int elementMatch, String expectedClass) {
        return checkElementHasClass(element.getType(), element.getLocator(), elementMatch, expectedClass);
    }

    /**
     * checks to see if an element has a particular class
     *
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param elementMatch
     *            - if there are multiple matches of the selector, this is which
     *            match (starting at 0) to interact with
     * @param expectedClass
     *            - the full expected class value
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int checkElementHasClass(Locator type, String locator, int elementMatch, String expectedClass) {
        int errors = locatorAssert.checkElementHasClass(type, locator, elementMatch, expectedClass);
        outputFile.addErrors(errors);
        return errors;
    }

    /**
     * compares the expected element value with the actual value from an element
     *
     * @param element
     *            - the element to be waited for
     * @param expectedValue
     *            the expected value of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareTextValue(Element element, String expectedValue) {
        return compareTextValue(element.getType(), element.getLocator(), 0, expectedValue);
    }

    /**
     * compares the expected element value with the actual value from an element
     *
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param expectedValue
     *            the expected value of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareTextValue(Locator type, String locator, String expectedValue) {
        return compareTextValue(type, locator, 0, expectedValue);
    }

    /**
     * compares the expected element value with the actual value from an element
     *
     * @param element
     *            - the element to be waited for
     * @param elementMatch
     *            - if there are multiple matches of the selector, this is which
     *            match (starting at 0) to interact with
     * @param expectedValue
     *            the expected value of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareTextValue(Element element, int elementMatch, String expectedValue) {
        return compareTextValue(element.getType(), element.getLocator(), elementMatch, expectedValue);
    }

    /**
     * compares the expected element value with the actual value from an element
     *
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param elementMatch
     *            - if there are multiple matches of the selector, this is which
     *            match (starting at 0) to interact with
     * @param expectedValue
     *            the expected value of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareTextValue(Locator type, String locator, int elementMatch, String expectedValue) {
        int errors = locatorAssert.compareTextValue(type, locator, elementMatch, expectedValue);
        outputFile.addErrors(errors);
        return errors;
    }

    /**
     * 
     * @param element
     *            - the element to be matched
     * @param expectedMatchedElements
     *            - the expected number of elements on the page that match the
     *            module
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareElementMatches(Element element, int expectedMatchedElements) {
        // TODO
        return 1;
    }

    /**
     * compares the expected element input value with the actual value from an
     * element
     *
     * @param element
     *            - the element to be waited for
     * @param expectedValue
     *            the expected input value of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareInputValue(Element element, String expectedValue) {
        return compareInputValue(element.getType(), element.getLocator(), 0, expectedValue);
    }

    /**
     * compares the expected element input value with the actual value from an
     * element
     *
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param expectedValue
     *            the expected input value of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareInputValue(Locator type, String locator, String expectedValue) {
        return compareInputValue(type, locator, 0, expectedValue);
    }

    /**
     * compares the expected element input value with the actual value from an
     * element
     *
     * @param element
     *            - the element to be waited for
     * @param elementMatch
     *            - if there are multiple matches of the selector, this is which
     *            match (starting at 0) to interact with
     * @param expectedValue
     *            the expected input value of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareInputValue(Element element, int elementMatch, String expectedValue) {
        return compareInputValue(element.getType(), element.getLocator(), elementMatch, expectedValue);
    }

    /**
     * compares the expected element input value with the actual value from an
     * element
     *
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param elementMatch
     *            - if there are multiple matches of the selector, this is which
     *            match (starting at 0) to interact with
     * @param expectedValue
     *            the expected input value of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareInputValue(Locator type, String locator, int elementMatch, String expectedValue) {
        int errors = locatorAssert.compareInputValue(type, locator, elementMatch, expectedValue);
        outputFile.addErrors(errors);
        return errors;
    }

    /**
     * compares the expected element css attribute value with the actual css
     * attribute value from an element
     *
     * @param element
     *            - the element to be waited for
     * @param attribute
     *            - the css attribute to be checked
     * @param expectedValue
     *            the expected css value of the passed attribute of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareCssValue(Element element, String attribute, String expectedValue) {
        return compareCssValue(element.getType(), element.getLocator(), 0, attribute, expectedValue);
    }

    /**
     * compares the expected element css attribute value with the actual css
     * attribute value from an element
     *
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param attribute
     *            - the css attribute to be checked
     * @param expectedValue
     *            the expected css value of the passed attribute of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareCssValue(Locator type, String locator, String attribute, String expectedValue) {
        return compareCssValue(type, locator, 0, attribute, expectedValue);
    }

    /**
     * compares the expected element css attribute value with the actual css
     * attribute value from an element
     *
     * @param element
     *            - the element to be waited for
     * @param elementMatch
     *            - if there are multiple matches of the selector, this is which
     *            match (starting at 0) to interact with
     * @param attribute
     *            - the css attribute to be checked
     * @param expectedValue
     *            the expected css value of the passed attribute of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareCssValue(Element element, int elementMatch, String attribute, String expectedValue) {
        return compareCssValue(element.getType(), element.getLocator(), elementMatch, attribute, expectedValue);
    }

    /**
     * compares the expected element css attribute value with the actual css
     * attribute value from an element
     *
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param elementMatch
     *            - if there are multiple matches of the selector, this is which
     *            match (starting at 0) to interact with
     * @param attribute
     *            - the css attribute to be checked
     * @param expectedValue
     *            the expected css value of the passed attribute of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareCssValue(Locator type, String locator, int elementMatch, String attribute, String expectedValue) {
        int errors = locatorAssert.compareCssValue(type, locator, elementMatch, attribute, expectedValue);
        outputFile.addErrors(errors);
        return errors;
    }

    /**
     * compares the expected element select value with the actual value from an
     * element
     *
     * @param element
     *            - the element to be waited for
     * @param expectedValue
     *            the expected input value of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareSelectedValue(Element element, String expectedValue) {
        return compareSelectedValue(element.getType(), element.getLocator(), 0, expectedValue);
    }

    /**
     * compares the expected element select value with the actual value from an
     * element
     *
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param expectedValue
     *            the expected input value of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareSelectedValue(Locator type, String locator, String expectedValue) {
        return compareSelectedValue(type, locator, 0, expectedValue);
    }

    /**
     * compares the expected element select value with the actual value from an
     * element
     *
     * @param element
     *            - the element to be waited for
     * @param elementMatch
     *            - if there are multiple matches of the selector, this is which
     *            match (starting at 0) to interact with
     * @param expectedValue
     *            the expected input value of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareSelectedValue(Element element, int elementMatch, String expectedValue) {
        return compareSelectedValue(element.getType(), element.getLocator(), elementMatch, expectedValue);
    }

    /**
     * compares the expected element select value with the actual value from an
     * element
     *
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param elementMatch
     *            - if there are multiple matches of the selector, this is which
     *            match (starting at 0) to interact with
     * @param expectedValue
     *            the expected input value of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareSelectedValue(Locator type, String locator, int elementMatch, String expectedValue) {
        int errors = locatorAssert.compareSelectedValue(type, locator, elementMatch, expectedValue);
        outputFile.addErrors(errors);
        return errors;
    }

    /**
     * compares the expected element select test with the actual value from an
     * element
     *
     * @param element
     *            - the element to be waited for
     * @param expectedText
     *            the expected input text of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareSelectedOption(Element element, String expectedText) {
        return compareSelectedOption(element.getType(), element.getLocator(), 0, expectedText);
    }

    /**
     * compares the expected element select test with the actual value from an
     * element
     *
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param expectedText
     *            the expected input text of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareSelectedOption(Locator type, String locator, String expectedText) {
        return compareSelectedOption(type, locator, 0, expectedText);
    }

    /**
     * compares the expected element select test with the actual value from an
     * element
     *
     * @param element
     *            - the element to be waited for
     * @param elementMatch
     *            - if there are multiple matches of the selector, this is which
     *            match (starting at 0) to interact with
     * @param expectedText
     *            the expected input text of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareSelectedOption(Element element, int elementMatch, String expectedText) {
        return compareSelectedOption(element.getType(), element.getLocator(), elementMatch, expectedText);
    }

    /**
     * compares the expected element select test with the actual value from an
     * element
     *
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param elementMatch
     *            - if there are multiple matches of the selector, this is which
     *            match (starting at 0) to interact with
     * @param expectedText
     *            the expected input text of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareSelectedOption(Locator type, String locator, int elementMatch, String expectedText) {
        int errors = locatorAssert.compareSelectedOption(type, locator, elementMatch, expectedText);
        outputFile.addErrors(errors);
        return errors;
    }

    /**
     * compares the expected element select value with the actual value from an
     * element
     *
     * @param element
     *            - the element to be waited for
     * @param expectedValue
     *            the expected input value of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareSelectedValueNotEqual(Element element, String expectedValue) {
        return compareSelectedValueNotEqual(element.getType(), element.getLocator(), 0, expectedValue);
    }

    /**
     * compares the expected element select value with the actual value from an
     * element
     *
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param expectedValue
     *            the expected input value of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareSelectedValueNotEqual(Locator type, String locator, String expectedValue) {
        return compareSelectedValueNotEqual(type, locator, 0, expectedValue);
    }

    /**
     * compares the expected element select value with the actual value from an
     * element
     *
     * @param element
     *            - the element to be waited for
     * @param elementMatch
     *            - if there are multiple matches of the selector, this is which
     *            match (starting at 0) to interact with
     * @param expectedValue
     *            the expected input value of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareSelectedValueNotEqual(Element element, int elementMatch, String expectedValue) {
        return compareSelectedValueNotEqual(element.getType(), element.getLocator(), elementMatch, expectedValue);
    }

    /**
     * compares the expected element select value with the actual value from an
     * element
     *
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param elementMatch
     *            - if there are multiple matches of the selector, this is which
     *            match (starting at 0) to interact with
     * @param expectedValue
     *            the expected input value of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareSelectedValueNotEqual(Locator type, String locator, int elementMatch, String expectedValue) {
        int errors = locatorAssert.compareSelectedValueNotEqual(type, locator, elementMatch, expectedValue);
        outputFile.addErrors(errors);
        return errors;
    }

    /**
     * compares the expected attributes from a select value with the actual
     * attributes from the element
     *
     * @param element
     *            - the element to be waited for
     * @param expectedValues
     *            the expected input value of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareSelectValues(Element element, String... expectedValues) {
        return compareSelectValues(element.getType(), element.getLocator(), 0, expectedValues);
    }

    /**
     * compares the expected attributes from a select value with the actual
     * attributes from the element
     *
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param expectedValues
     *            the expected input value of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareSelectValues(Locator type, String locator, String... expectedValues) {
        return compareSelectValues(type, locator, 0, expectedValues);
    }

    /**
     * compares the expected attributes from a select value with the actual
     * attributes from the element
     *
     * @param element
     *            - the element to be waited for
     * @param elementMatch
     *            - if there are multiple matches of the selector, this is which
     *            match (starting at 0) to interact with
     * @param expectedValues
     *            the expected input value of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareSelectValues(Element element, int elementMatch, String... expectedValues) {
        return compareSelectValues(element.getType(), element.getLocator(), elementMatch, expectedValues);
    }

    /**
     * compares the expected attributes from a select value with the actual
     * attributes from the element
     *
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param elementMatch
     *            - if there are multiple matches of the selector, this is which
     *            match (starting at 0) to interact with
     * @param expectedValues
     *            the expected input value of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareSelectValues(Locator type, String locator, int elementMatch, String... expectedValues) {
        int errors = locatorAssert.compareSelectValues(type, locator, elementMatch, expectedValues);
        outputFile.addErrors(errors);
        return errors;
    }

    /**
     * compares the text of expected table cell with the actual table cell text
     * of a table with from a table element
     *
     * @param element
     *            - the element to be waited for
     * @param row
     *            - the number of the row in the table - note, row numbering
     *            starts at 1, NOT 0
     * @param col
     *            - the number of the column in the table - note, column
     *            numbering starts at 1, NOT 0
     * @param text
     *            - what text do we expect to be in the table cell
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareTableCellText(Element element, int row, int col, String text) {
        return compareTableCellText(element.getType(), element.getLocator(), 0, row, col, text);
    }

    /**
     * compares the text of expected table cell with the actual table cell text
     * of a table with from a table element
     *
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param row
     *            - the number of the row in the table - note, row numbering
     *            starts at 1, NOT 0
     * @param col
     *            - the number of the column in the table - note, column
     *            numbering starts at 1, NOT 0
     * @param text
     *            - what text do we expect to be in the table cell
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareTableCellText(Locator type, String locator, int row, int col, String text) {
        return compareTableCellText(type, locator, 0, row, col, text);
    }

    /**
     * compares the text of expected table cell with the actual table cell text
     * of a table with from a table element
     *
     * @param element
     *            - the element to be waited for
     * @param elementMatch
     *            - if there are multiple matches of the selector, this is which
     *            match (starting at 0) to interact with
     * @param row
     *            - the number of the row in the table - note, row numbering
     *            starts at 1, NOT 0
     * @param col
     *            - the number of the column in the table - note, column
     *            numbering starts at 1, NOT 0
     * @param text
     *            - what text do we expect to be in the table cell
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareTableCellText(Element element, int elementMatch, int row, int col, String text) {
        return compareTableCellText(element.getType(), element.getLocator(), elementMatch, row, col, text);
    }

    /**
     * compares the text of expected table cell with the actual table cell text
     * of a table with from a table element
     *
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param elementMatch
     *            - if there are multiple matches of the selector, this is which
     *            match (starting at 0) to interact with
     * @param row
     *            - the number of the row in the table - note, row numbering
     *            starts at 1, NOT 0
     * @param col
     *            - the number of the column in the table - note, column
     *            numbering starts at 1, NOT 0
     * @param text
     *            - what text do we expect to be in the table cell
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int compareTableCellText(Locator type, String locator, int elementMatch, int row, int col, String text) {
        int errors = locatorAssert.compareTableCellText(type, locator, elementMatch, row, col, text);
        outputFile.addErrors(errors);
        return errors;
    }

    ///////////////////////////////////////////////////////////////////
    // this enum will be for a pass/fail
    ///////////////////////////////////////////////////////////////////

    /**
     * checks to see if an element select value exists
     *
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param selectValue
     *            the expected input value of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int checkSelectValuePresent(Locator type, String locator, String selectValue) {
        return checkSelectValuePresent(new Element(type, locator), selectValue);
    }

    /**
     * checks to see if an element select value exists
     *
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param elementMatch
     *            - if there are multiple matches of the selector, this is which
     *            match (starting at 0) to interact with
     * @param selectValue
     *            the expected input value of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int checkSelectValuePresent(Locator type, String locator, int elementMatch, String selectValue) {
        return checkSelectValuePresent(new Element(type, locator, elementMatch), selectValue);
    }

    /**
     * checks to see if an element select value exists
     *
     * @param element
     *            - the element to be assessed
     * @param selectValue
     *            the expected input value of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int checkSelectValuePresent(Element element, String selectValue) {
        String EXPECTED = "Expected to find element with ";
        String ELEMENT = "The element with ";
        String HASVALUE = "</i> contains the value of <b>";
        String ONLYVALUE = ", only the values <b>";

        // outputFile.record the action
        outputFile.recordExpected(
                EXPECTED + element.prettyOutput() + "</i> having a select value of <b>" + selectValue + "</b>");
        // check for the object to the present on the page
        String[] elementValues;
        // if (!isPresentInputEnabled(element)) {
        // return 1;
        // } else {
        elementValues = action.get().selectedValues(element);
        // }
        if (General.doesArrayContain(elementValues, selectValue)) {
            outputFile.recordActual(ELEMENT + element.prettyOutput() + HASVALUE + selectValue + "</b>", Success.PASS);
            return 0;
        }
        outputFile.recordActual(ELEMENT + element.prettyOutput() + "</i> does not contain the value of <b>"
                + selectValue + "</b>" + ONLYVALUE + Arrays.toString(elementValues) + "</b>", Success.FAIL);
        return 1;
    }

    /**
     * An enumeration used to determine if the tests pass or fail
     * 
     * @author Max Saperstone
     *
     */
    public enum Success {
        PASS, FAIL;

        protected int errors;

        /**
         * Are errors associated with the enumeration
         */
        static {
            PASS.errors = 0;
            FAIL.errors = 1;
        }

        /**
         * Retrieve the errors associated with the enumeration
         * 
         * @return Integer: the errors associated with the enumeration
         */
        public int getErrors() {
            return this.errors;
        }
    }

    /**
     * An enumeration used to give status for each test step
     * 
     * @author Max Saperstone
     *
     */
    public enum Result {
        WARNING, SUCCESS, FAILURE, SKIPPED
    }
}