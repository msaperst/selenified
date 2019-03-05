/*
 * Copyright 2019 Coveros, Inc.
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

package com.coveros.selenified.application;

import com.coveros.selenified.OutputFile;
import com.coveros.selenified.OutputFile.Success;

/**
 * Assert will handle all verifications performed on the actual application
 * itself. These asserts are custom to the framework, and in addition to
 * providing easy object oriented capabilities, they take screenshots with each
 * verification to provide additional traceability, and assist in
 * troubleshooting and debugging failing tests.
 *
 * @author Max Saperstone
 * @version 3.0.5
 * @lastupdate 5/4/2018
 */
public interface Check {

    // constants
    String ONPAGE = "</b> on the page";
    String NOALERT = "An alert is not present on the page";
    String ALERTTEXT = "An alert with text <b>";
    String NOCONFIRMATION = "a confirmation is not present on the page";
    String CONFIRMATIONTEXT = "a confirmation with text <b>";
    String NOPROMPT = "a prompt is not present on the page";
    String PROMPTTEXT = "a prompt with text <b>";

    String STORED = "</b> is stored for the page";
    String NOTSTORED = "</b> is not stored for the page";
    String VALUE = "</b> and a value of <b>";
    String COOKIE = "a cookie with the name <b>";

    String TEXT = "The text <b>";
    String PRESENT = "</b> is present on the page";


    /**
     * Retrieves the output file that we write all details out to
     *
     * @return OutputFile
     */
    OutputFile getOutputFile();

    /**
     * Retrieves the driver that is used for all selenium actions
     *
     * @return App
     */
    App getApp();

    ///////////////////////////////////////////////////////
    // checks about the page in general
    ///////////////////////////////////////////////////////

    default void recordAction(String check, double waitFor) {
        String action = "";
        if (waitFor > 0) {
            action = "Waiting up to " + waitFor + " seconds " + check;
        }
        getOutputFile().recordAction(action, "Expected " + check);
    }

    default void recordActual(String check, double timeTook, Success success) {
        String actual = check;
        if (timeTook > 0) {
            String lowercase = actual.substring(0, 1).toLowerCase();
            actual = "After waiting for " + timeTook + " seconds, " + lowercase + actual.substring(1);
        }
        getOutputFile().recordActual(actual, success);
    }

    /**
     * Verifies that the provided URL equals the actual URL the application is
     * currently on. This information will be logged and recorded, with a
     * screenshot for traceability and added debugging support.
     *
     * @param expectedURL the URL of the page
     */
    void urlEquals(String expectedURL);

    default String checkUrlEquals(String expectedURL, double waitFor, double timeTook) {
        // record the action
        recordAction("to be on page with the URL of <b>" + expectedURL + "</b>", waitFor);
        String actualURL = getApp().get().location();
        if (!actualURL.equals(expectedURL)) {
            recordActual("The page URL reads <b>" + actualURL + "</b>", timeTook, Success.FAIL);
        } else {
            recordActual("The page URL reads <b>" + actualURL + "</b>", timeTook, Success.PASS);
        }
        return actualURL;
    }

    /**
     * Verifies the provided title equals the actual title of the current page
     * the application is on. This information will be logged and recorded, with
     * a screenshot for traceability and added debugging support.
     *
     * @param expectedTitle the friendly name of the page
     */
    void titleEquals(String expectedTitle);

    default String checkTitleEquals(String expectedTitle, double waitFor, double timeTook) {
        // record the action
        recordAction("to be on page with the title of <b>" + expectedTitle + "</b>", waitFor);
        String actualTitle = getApp().get().title();
        if (!actualTitle.equals(expectedTitle)) {
            recordActual("The page title reads <b>" + actualTitle + "</b>", timeTook, Success.FAIL);
        } else {
            recordActual("The page title reads <b>" + actualTitle + "</b>", timeTook, Success.PASS);
        }
        return actualTitle;
    }

    /**
     * Verifies the provided title matches the actual title of the current page
     * the application is on. This information will be logged and recorded, with
     * a screenshot for traceability and added debugging support.
     *
     * @param expectedTitlePattern the friendly name of the page
     */
    void titleMatches(String expectedTitlePattern);

    default String checkTitleMatches(String expectedTitlePattern, double waitFor, double timeTook) {
        // record the action
        recordAction("to be on page with the title matching pattern <b>" + expectedTitlePattern + "</b>", waitFor);
        String actualTitle = getApp().get().title();
        if (!actualTitle.matches(expectedTitlePattern)) {
            recordActual("The page title reads <b>" + actualTitle + "</b>", timeTook, Success.FAIL);
        } else {
            recordActual("The page title reads <b>" + actualTitle + "</b>", timeTook, Success.PASS);
        }
        return actualTitle;
    }

    /**
     * Verifies that provided text(s) are on the current page. This information
     * will be logged and recorded, with a screenshot for traceability and added
     * debugging support.
     *
     * @param expectedText the expected text to be present
     */
    void textPresent(String expectedText);

    default boolean checkTextPresent(String expectedText, double waitFor, double timeTook) {
        // record the action
        recordAction("to find text <b>" + expectedText + "</b> present on the page", waitFor);
        // check for the object to be present
        boolean isPresent = getApp().is().textPresent(expectedText);
        if (!isPresent) {
            recordActual(TEXT + expectedText + "</b> is not present on the page", timeTook, Success.FAIL);
            return false;
        }
        recordActual(TEXT + expectedText + PRESENT, timeTook, Success.PASS);
        return true;
    }

    /**
     * Verifies that provided text(s) are not on the current page. This
     * information will be logged and recorded, with a screenshot for
     * traceability and added debugging support.
     *
     * @param expectedText the expected text to be not present
     */
    void textNotPresent(String expectedText);

    default boolean checkTextNotPresent(String expectedText, double waitFor, double timeTook) {
        // record the action
        recordAction("not to find text <b>" + expectedText + "</b> present on the page", waitFor);
        // check for the object to be present
        boolean isPresent = getApp().is().textPresent(expectedText);
        if (isPresent) {
            recordActual(TEXT + expectedText + PRESENT, timeTook, Success.FAIL);
            return false;
        }
        recordActual(TEXT + expectedText + "</b> is not present on the page", timeTook, Success.PASS);
        return true;
    }

    ///////////////////////////////////////////////////////
    // assertions about pop-ups
    ///////////////////////////////////////////////////////

    /**
     * Verifies that an alert is present on the page. This information will be
     * logged and recorded, with a screenshot for traceability and added
     * debugging support.
     */
    void alertPresent();

    default boolean checkAlertPresent(double waitFor, double timeTook) {
        // record the action
        recordAction("to find an alert on the page", waitFor);
        // check for the object to be present
        String alert;
        boolean isAlertPresent = getApp().is().alertPresent();
        if (isAlertPresent) {
            alert = getApp().get().alert();
        } else {
            recordActual(NOALERT, timeTook, Success.FAIL);
            return false;
        }
        recordActual(ALERTTEXT + alert + PRESENT, timeTook, Success.PASS);
        return true;
    }

    /**
     * Verifies that an alert is not present on the page. This information will
     * be logged and recorded, with a screenshot for traceability and added
     * debugging support.
     */
    void alertNotPresent();

    default boolean checkAlertNotPresent(double waitFor, double timeTook) {
        // record the action
        recordAction("not to find an alert on the page", waitFor);
        // check for the object to be present
        boolean isAlertPresent = getApp().is().alertPresent();
        if (isAlertPresent) {
            recordActual("An alert is present on the page", timeTook, Success.FAIL);
            return false;
        }
        recordActual(NOALERT, timeTook, Success.PASS);
        return true;
    }

    /**
     * Verifies that an alert present on the page has content equal to the
     * expected text. This information will be logged and recorded, with a
     * screenshot for traceability and added debugging support.
     *
     * @param expectedAlertText the expected text of the alert
     */
    void alertEquals(String expectedAlertText);

    default String checkAlertEquals(String expectedAlertText, double waitFor, double timeTook) {
        // record the action
        recordAction("to find alert with the text <b>" + expectedAlertText + ONPAGE, waitFor);
        // check for the object to be present
        String alert;
        boolean isAlertPresent = getApp().is().alertPresent();
        if (isAlertPresent) {
            alert = getApp().get().alert();
        } else {
            recordActual(NOALERT, timeTook, Success.FAIL);
            return "";
        }
        if (!alert.equals(expectedAlertText)) {
            recordActual(ALERTTEXT + alert + PRESENT, timeTook, Success.FAIL);
        } else {
            recordActual(ALERTTEXT + alert + PRESENT, timeTook, Success.PASS);
        }
        return alert;
    }

    /**
     * Verifies that an alert present on the page has content matching the
     * expected pattern. This information will be logged and recorded, with a
     * screenshot for traceability and added debugging support.
     *
     * @param expectedAlertPattern the expected text of the alert
     */
    void alertMatches(String expectedAlertPattern);

    default String checkAlertMatches(String expectedAlertPattern, double waitFor, double timeTook) {
        // record the action
        recordAction("to find alert with the text matching pattern <b>" + expectedAlertPattern + ONPAGE, waitFor);
        // check for the object to be present
        String alert;
        boolean isAlertPresent = getApp().is().alertPresent();
        if (isAlertPresent) {
            alert = getApp().get().alert();
        } else {
            recordActual(NOALERT, timeTook, Success.FAIL);
            return "";
        }
        if (!alert.matches(expectedAlertPattern)) {
            recordActual(ALERTTEXT + alert + PRESENT, timeTook, Success.FAIL);
        } else {
            recordActual(ALERTTEXT + alert + PRESENT, timeTook, Success.PASS);
        }
        return alert;
    }

    /**
     * Verifies that a confirmation is present on the page. This information
     * will be logged and recorded, with a screenshot for traceability and added
     * debugging support.
     */
    void confirmationPresent();

    default boolean checkConfirmationPresent(double waitFor, double timeTook) {
        //record the action
        recordAction("to find a confirmation on the page", waitFor);
        // check for the object to be present
        String confirmation;
        boolean isConfirmationPresent = getApp().is().confirmationPresent();
        if (isConfirmationPresent) {
            confirmation = getApp().get().confirmation();
        } else {
            recordActual(NOCONFIRMATION, timeTook, Success.FAIL);
            return false;
        }
        recordActual(CONFIRMATIONTEXT + confirmation + PRESENT, timeTook, Success.PASS);
        return true;
    }

    /**
     * Verifies that a confirmation is not present on the page. This information
     * will be logged and recorded, with a screenshot for traceability and added
     * debugging support.
     */
    void confirmationNotPresent();

    default boolean checkConfirmationNotPresent(double waitFor, double timeTook) {
        //record the action
        recordAction("to find a confirmation on the page", waitFor);
        // check for the object to be present
        boolean isConfirmationPresent = getApp().is().confirmationPresent();
        if (isConfirmationPresent) {
            recordActual(" confirmation is present on the page", timeTook, Success.FAIL);
            return false;
        }
        recordActual(NOCONFIRMATION, timeTook, Success.PASS);
        return true;
    }

    /**
     * Verifies that a confirmation present on the page has content equal to the
     * expected text. This information will be logged and recorded, with a
     * screenshot for traceability and added debugging support.
     *
     * @param expectedConfirmationText the expected text of the confirmation
     */
    void confirmationEquals(String expectedConfirmationText);

    default String checkConfirmationEquals(String expectedConfirmationText, double waitFor, double timeTook) {
        //record the action
        recordAction("to find confirmation with the text <b>" + expectedConfirmationText + ONPAGE, waitFor);
        // check for the object to be present
        String confirmation;
        boolean isConfirmationPresent = getApp().is().confirmationPresent();
        if (isConfirmationPresent) {
            confirmation = getApp().get().confirmation();
        } else {
            recordActual(NOCONFIRMATION, timeTook, Success.FAIL);
            return "";
        }
        if (!expectedConfirmationText.equals(confirmation)) {
            recordActual(CONFIRMATIONTEXT + confirmation + PRESENT, timeTook, Success.FAIL);
        } else {
            recordActual(CONFIRMATIONTEXT + confirmation + PRESENT, timeTook, Success.PASS);
        }
        return confirmation;
    }

    /**
     * Verifies that a confirmation present on the page has content matching the
     * expected pattern. This information will be logged and recorded, with a
     * screenshot for traceability and added debugging support.
     *
     * @param expectedConfirmationPattern the expected text of the confirmation
     */
    void confirmationMatches(String expectedConfirmationPattern);

    default String checkConfirmationMatches(String expectedConfirmationPattern, double waitFor, double timeTook) {
        //record the action
        recordAction("to find confirmation with the text matching pattern <b>" + expectedConfirmationPattern + ONPAGE, waitFor);
        // check for the object to be present
        String confirmation;
        boolean isConfirmationPresent = getApp().is().confirmationPresent();
        if (isConfirmationPresent) {
            confirmation = getApp().get().confirmation();
        } else {
            recordActual(NOCONFIRMATION, timeTook, Success.FAIL);
            return "";
        }
        if (!confirmation.matches(expectedConfirmationPattern)) {
            recordActual(CONFIRMATIONTEXT + confirmation + PRESENT, timeTook, Success.FAIL);
        } else {
            recordActual(CONFIRMATIONTEXT + confirmation + PRESENT, timeTook, Success.PASS);
        }
        return confirmation;
    }

    /**
     * Verifies that a prompt is present on the page. This information will be
     * logged and recorded, with a screenshot for traceability and added
     * debugging support.
     */
    void promptPresent();

    default boolean checkPromptPresent(double waitFor, double timeTook) {
        //record the action
        recordAction("to find prompt on the page", waitFor);
        // check for the object to be present
        String prompt;
        boolean isPromptPresent = getApp().is().promptPresent();
        if (isPromptPresent) {
            prompt = getApp().get().prompt();
        } else {
            recordActual(NOPROMPT, timeTook, Success.FAIL);
            return false;
        }
        recordActual(PROMPTTEXT + prompt + PRESENT, timeTook, Success.PASS);
        return true;
    }

    /**
     * Verifies that a prompt is not present on the page. This information will
     * be logged and recorded, with a screenshot for traceability and added
     * debugging support.
     */
    void promptNotPresent();

    default boolean checkPromptNotPresent(double waitFor, double timeTook) {
        //record the action
        recordAction("not to find prompt on the page", waitFor);
        // check for the object to be present
        boolean isPromptPresent = getApp().is().promptPresent();
        if (isPromptPresent) {
            recordActual(" prompt is present on the page", timeTook, Success.FAIL);
        } else {
            recordActual(NOPROMPT, timeTook, Success.PASS);
        }
        return !isPromptPresent;
    }

    /**
     * Verifies that a prompt present on the page has content equal to the
     * expected text. This information will be logged and recorded, with a
     * screenshot for traceability and added debugging support.
     *
     * @param expectedPromptText the expected text of the prompt
     */
    void promptEquals(String expectedPromptText);

    default String checkPromptEquals(String expectedPromptText, double waitFor, double timeTook) {
        //record the action
        recordAction("to find prompt with the text <b>" + expectedPromptText + ONPAGE, waitFor);
        // check for the object to be present
        String prompt;
        boolean isPromptPresent = getApp().is().promptPresent();
        if (isPromptPresent) {
            prompt = getApp().get().prompt();
        } else {
            recordActual(NOPROMPT, timeTook, Success.FAIL);
            return "";
        }
        if (!expectedPromptText.equals(prompt)) {
            recordActual(PROMPTTEXT + prompt + PRESENT, timeTook, Success.FAIL);
        } else {
            recordActual(PROMPTTEXT + prompt + PRESENT, timeTook, Success.PASS);
        }
        return prompt;
    }

    /**
     * Verifies that a prompt present on the page has content matches the
     * expected pattern. This information will be logged and recorded, with a
     * screenshot for traceability and added debugging support.
     *
     * @param expectedPromptPattern the expected text of the prompt
     */
    void promptMatches(String expectedPromptPattern);

    default String checkPromptMatches(String expectedPromptPattern, double waitFor, double timeTook) {
        //record the action
        recordAction("to find prompt with the text matching pattern <b>" + expectedPromptPattern + ONPAGE, waitFor);
        // check for the object to be present
        String prompt;
        boolean isPromptPresent = getApp().is().promptPresent();
        if (isPromptPresent) {
            prompt = getApp().get().prompt();
        } else {
            recordActual(NOPROMPT, timeTook, Success.FAIL);
            return "";
        }
        if (!prompt.matches(expectedPromptPattern)) {
            recordActual(PROMPTTEXT + prompt + PRESENT, timeTook, Success.FAIL);
        } else {
            recordActual(PROMPTTEXT + prompt + PRESENT, timeTook, Success.PASS);
        }
        return prompt;
    }

    ///////////////////////////////////////////////////////
    // assertions about cookies
    ///////////////////////////////////////////////////////

    /**
     * Verifies that a cookie exists in the application with the provided
     * cookieName. This information will be logged and recorded, with a
     * screenshot for traceability and added debugging support.
     *
     * @param expectedCookieName the name of the cookie
     */
    void cookieExists(String expectedCookieName);

    default boolean checkCookieExists(String expectedCookieName, double waitFor, double timeTook) {
        //record the action
        recordAction("to find cookie with the name <b>" + expectedCookieName + STORED, waitFor);
        // check for the object to be present
        String cookieValue;
        boolean isCookiePresent = getApp().is().cookiePresent(expectedCookieName);
        if (isCookiePresent) {
            cookieValue = getApp().get().cookieValue(expectedCookieName);
            recordActual(COOKIE + expectedCookieName + VALUE + cookieValue + STORED, timeTook, Success.PASS);
        } else {
            recordActual(COOKIE + expectedCookieName + NOTSTORED, timeTook, Success.FAIL);
        }
        return isCookiePresent;
    }

    /**
     * Verifies that a cookie doesn't exist in the application with the provided
     * cookieName. This information will be logged and recorded, with a
     * screenshot for traceability and added debugging support.
     *
     * @param unexpectedCookieName the name of the cookie
     */
    void cookieNotExists(String unexpectedCookieName);

    default boolean checkCookieNotExists(String unexpectedCookieName, double waitFor, double timeTook) {
        //record the action
        recordAction("to find no cookie with the name <b>" + unexpectedCookieName + STORED, waitFor);
        // check for the object to be present
        boolean isCookiePresent = getApp().is().cookiePresent(unexpectedCookieName);
        if (isCookiePresent) {
            recordActual(COOKIE + unexpectedCookieName + STORED, timeTook, Success.FAIL);
        } else {
            recordActual(COOKIE + unexpectedCookieName + NOTSTORED, timeTook, Success.PASS);
        }
        return !isCookiePresent;
    }

    /**
     * Verifies that a cookies with the provided name has a value equal to the
     * expected value. This information will be logged and recorded, with a
     * screenshot for traceability and added debugging support.
     *
     * @param cookieName          the name of the cookie
     * @param expectedCookieValue the expected value of the cookie
     */
    void cookieEquals(String cookieName, String expectedCookieValue);

    default String checkCookieEquals(String cookieName, String expectedCookieValue, double waitFor, double timeTook) {
        //record the action
        recordAction(
                "to find cookie with the name <b>" + cookieName + VALUE + expectedCookieValue + STORED, waitFor);
        // check for the object to be present
        String cookieValue;
        boolean isCookiePresent = getApp().is().cookiePresent(cookieName);
        if (isCookiePresent) {
            cookieValue = getApp().get().cookieValue(cookieName);
        } else {
            recordActual(COOKIE + cookieName + NOTSTORED, timeTook, Success.FAIL);
            return "";
        }
        if (!cookieValue.equals(expectedCookieValue)) {
            recordActual(
                    COOKIE + cookieName + "</b> is stored for the page, but the value of the cookie is " + cookieValue,
                    timeTook, Success.FAIL);
        } else {
            recordActual(COOKIE + cookieName + VALUE + cookieValue + STORED, timeTook, Success.PASS);
        }
        return cookieValue;
    }

    /**
     * Verifies that a cookies with the provided name has a value matching the
     * expected value pattern. This information will be logged and recorded, with a
     * screenshot for traceability and added debugging support.
     *
     * @param cookieName            the name of the cookie
     * @param expectedCookiePattern the expected value of the cookie
     */
    void cookieMatches(String cookieName, String expectedCookiePattern);

    default String checkCookieMatches(String cookieName, String expectedCookiePattern, double waitFor, double timeTook) {
        //record the action
        recordAction(
                "to find cookie with the name <b>" + cookieName + "</b> and a value matching pattern of <b>" + expectedCookiePattern + STORED, waitFor);
        // check for the object to be present
        String cookieValue;
        boolean isCookiePresent = getApp().is().cookiePresent(cookieName);
        if (isCookiePresent) {
            cookieValue = getApp().get().cookieValue(cookieName);
        } else {
            recordActual(COOKIE + cookieName + NOTSTORED, timeTook, Success.FAIL);
            return "";
        }
        if (!cookieValue.matches(expectedCookiePattern)) {
            recordActual(
                    COOKIE + cookieName + "</b> is stored for the page, but the value of the cookie is " + cookieValue,
                    timeTook, Success.FAIL);
        } else {
            recordActual(COOKIE + cookieName + VALUE + cookieValue + STORED, timeTook, Success.PASS);
        }
        return cookieValue;
    }
}