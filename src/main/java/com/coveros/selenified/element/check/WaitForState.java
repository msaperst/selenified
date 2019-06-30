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

package com.coveros.selenified.element.check;

import com.coveros.selenified.element.Element;
import com.coveros.selenified.utilities.Reporter;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static com.coveros.selenified.utilities.Constants.ELEMENT_NOT_PRESENT;

/**
 * WaitForState implements State to provide some additional wait capabilities.
 * It will handle all waits performed on the actual element. These
 * waits are custom to the framework. WaitForState waits for elements to be
 * in a particular state.
 *
 * @author Max Saperstone
 * @version 3.2.1
 * @lastupdate 6/25/2019
 */
public class WaitForState extends State {

    public WaitForState(Element element, Reporter reporter) {
        this.element = element;
        this.reporter = reporter;
    }

    // ///////////////////////////////////////
    // waiting functionality
    // ///////////////////////////////////////

    /**
     * Waits for the element to be present. The default wait time will be used
     * and if the element isn't present after that time, it will fail, and log
     * the issue with a screenshot for traceability and added debugging support.
     */
    public void present() {
        present(defaultWait);
    }

    /**
     * Waits for the element to not be present. The default wait time will be used
     * and if the element isn't present after that time, it will fail, and log
     * the issue with a screenshot for traceability and added debugging support.
     */
    public void notPresent() {
        notPresent(defaultWait);
    }

    /**
     * Waits for the element to be displayed. The default wait time will be used
     * and if the element isn't present after that time, it will fail, and log
     * the issue with a screenshot for traceability and added debugging support.
     */
    public void displayed() {
        displayed(defaultWait);
    }

    /**
     * Waits for the element to not be displated. The default wait time will be used
     * and if the element isn't present after that time, it will fail, and log
     * the issue with a screenshot for traceability and added debugging support.
     */
    public void notDisplayed() {
        notDisplayed(defaultWait);
    }

    /**
     * Waits for the element to be checked. The default wait time will be used
     * and if the element isn't present after that time, it will fail, and log
     * the issue with a screenshot for traceability and added debugging support.
     */
    public void checked() {
        checked(defaultWait);
    }

    /**
     * Waits for the element to not be checked. The default wait time will be used
     * and if the element isn't present after that time, it will fail, and log
     * the issue with a screenshot for traceability and added debugging support.
     */
    public void notChecked() {
        notChecked(defaultWait);
    }

    /**
     * Waits for the element to be editable. The default wait time will be used
     * and if the element isn't present after that time, it will fail, and log
     * the issue with a screenshot for traceability and added debugging support.
     * If the element isn't an input, this will
     * constitute a failure, same as it not being editable.
     */
    public void editable() {
        editable(defaultWait);
    }

    /**
     * Waits for the element to not be editable. The default wait time will be used
     * and if the element isn't present after that time, it will fail, and log
     * the issue with a screenshot for traceability and added debugging support.
     * If the element isn't an input, this will
     * constitute a pass, same as it not being editable.
     */
    public void notEditable() {
        notEditable(defaultWait);
    }

    /**
     * Waits for the element to be enabled. The default wait time will be used
     * and if the element isn't present after that time, it will fail, and log
     * the issue with a screenshot for traceability and added debugging support.
     */
    public void enabled() {
        enabled(defaultWait);
    }

    /**
     * Waits for the element to not be enabled. The default wait time will be used
     * and if the element isn't present after that time, it will fail, and log
     * the issue with a screenshot for traceability and added debugging support.
     */
    public void notEnabled() {
        notEnabled(defaultWait);
    }

    ///////////////////////////////////////////////////
    // Our actual full implementation of the above overloaded methods
    ///////////////////////////////////////////////////

    /**
     * Waits for the element to be present. The provided wait time will be used
     * and if the element isn't present after that time, it will fail, and log
     * the issue with a screenshot for traceability and added debugging support.
     *
     * @param seconds - how many seconds to wait for
     */
    public void present(double seconds) {
        checkPresent(seconds, elementPresent(seconds));
    }

    /**
     * Waits for the element to not be present. The provided wait time will be used
     * and if the element isn't present after that time, it will fail, and log
     * the issue with a screenshot for traceability and added debugging support.
     *
     * @param seconds - how many seconds to wait for
     */
    public void notPresent(double seconds) {
        double end = System.currentTimeMillis() + (seconds * 1000);
        try {
            WebDriverWait wait = new WebDriverWait(element.getDriver(), (long) seconds, defaultPoll);
            wait.until(ExpectedConditions.not(ExpectedConditions.presenceOfAllElementsLocatedBy(element.defineByElement())));
            double timeTook = Math.min((seconds * 1000) - (end - System.currentTimeMillis()), seconds * 1000) / 1000;
            checkNotPresent(seconds, timeTook);
        } catch (TimeoutException e) {
            checkNotPresent(seconds, seconds);
        }
    }

    /**
     * Waits for the element to be displayed. The provided wait time will be used
     * and if the element isn't present after that time, it will fail, and log
     * the issue with a screenshot for traceability and added debugging support.
     *
     * @param seconds - how many seconds to wait for
     */
    public void displayed(double seconds) {
        double end = System.currentTimeMillis() + (seconds * 1000);
        try {
            double timeTook = elementPresent(seconds);
            if (timeTook >= seconds) {
                throw new TimeoutException(ELEMENT_NOT_PRESENT);
            }
            WebDriverWait wait = new WebDriverWait(element.getDriver(), (long) (seconds - timeTook), defaultPoll);
            wait.until(ExpectedConditions.visibilityOfElementLocated(element.defineByElement()));
            timeTook = Math.min((seconds * 1000) - (end - System.currentTimeMillis()), seconds * 1000) / 1000;
            checkDisplayed(seconds, timeTook);
        } catch (TimeoutException e) {
            checkDisplayed(seconds, seconds);
        }
    }

    /**
     * Waits for the element to not be displayed. The provided wait time will be used
     * and if the element isn't present after that time, it will fail, and log
     * the issue with a screenshot for traceability and added debugging support.
     *
     * @param seconds - how many seconds to wait for
     */
    public void notDisplayed(double seconds) {
        double end = System.currentTimeMillis() + (seconds * 1000);
        try {
            double timeTook = elementPresent(seconds);
            if (timeTook >= seconds) {
                throw new TimeoutException(ELEMENT_NOT_PRESENT);
            }
            WebDriverWait wait = new WebDriverWait(element.getDriver(), (long) (seconds - timeTook), defaultPoll);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(element.defineByElement()));
            timeTook = Math.min((seconds * 1000) - (end - System.currentTimeMillis()), seconds * 1000) / 1000;
            checkNotDisplayed(seconds, timeTook);
        } catch (TimeoutException e) {
            checkNotDisplayed(seconds, seconds);
        }
    }

    /**
     * Waits for the element to be checked. The provided wait time will be used
     * and if the element isn't present after that time, it will fail, and log
     * the issue with a screenshot for traceability and added debugging support.
     *
     * @param seconds - how many seconds to wait for
     */
    public void checked(double seconds) {
        double end = System.currentTimeMillis() + (seconds * 1000);
        try {
            double timeTook = elementPresent(seconds);
            if (timeTook >= seconds) {
                throw new TimeoutException(ELEMENT_NOT_PRESENT);
            }
            WebDriverWait wait = new WebDriverWait(element.getDriver(), (long) (seconds - timeTook), defaultPoll);
            wait.until((ExpectedCondition<Boolean>) d -> element.is().checked());
            timeTook = Math.min((seconds * 1000) - (end - System.currentTimeMillis()), seconds * 1000) / 1000;
            checkChecked(seconds, timeTook);
        } catch (TimeoutException e) {
            checkChecked(seconds, seconds);
        }
    }

    /**
     * Waits for the element to not be checked. The provided wait time will be used
     * and if the element isn't present after that time, it will fail, and log
     * the issue with a screenshot for traceability and added debugging support.
     *
     * @param seconds - how many seconds to wait for
     */
    public void notChecked(double seconds) {
        double end = System.currentTimeMillis() + (seconds * 1000);
        try {
            double timeTook = elementPresent(seconds);
            if (timeTook >= seconds) {
                throw new TimeoutException(ELEMENT_NOT_PRESENT);
            }
            WebDriverWait wait = new WebDriverWait(element.getDriver(), (long) (seconds - timeTook), defaultPoll);
            wait.until((ExpectedCondition<Boolean>) d -> !element.is().checked());
            timeTook = Math.min((seconds * 1000) - (end - System.currentTimeMillis()), seconds * 1000) / 1000;
            checkNotChecked(seconds, timeTook);
        } catch (TimeoutException e) {
            checkNotChecked(seconds, seconds);
        }
    }

    /**
     * Waits for the element to be editable. The provided wait time will be used
     * and if the element isn't present after that time, it will fail, and log
     * the issue with a screenshot for traceability and added debugging support.
     * If the element isn't an input, this will
     * constitute a failure, same as it not being editable.
     *
     * @param seconds - how many seconds to wait for
     */
    public void editable(double seconds) {
        double end = System.currentTimeMillis() + (seconds * 1000);
        try {
            double timeTook = elementPresent(seconds);
            if (timeTook >= seconds) {
                throw new TimeoutException(ELEMENT_NOT_PRESENT);
            }
            WebDriverWait wait = new WebDriverWait(element.getDriver(), (long) (seconds - timeTook), defaultPoll);
            wait.until((ExpectedCondition<Boolean>) d -> element.is().editable());
            timeTook = Math.min((seconds * 1000) - (end - System.currentTimeMillis()), seconds * 1000) / 1000;
            checkEditable(seconds, timeTook);
        } catch (TimeoutException e) {
            checkEditable(seconds, seconds);
        }
    }

    /**
     * Waits for the element to not be editable. The provided wait time will be used
     * and if the element isn't present after that time, it will fail, and log
     * the issue with a screenshot for traceability and added debugging support.
     * If the element isn't an input, this will
     * constitute a pass, same as it not being editable.
     *
     * @param seconds - how many seconds to wait for
     */
    public void notEditable(double seconds) {
        double end = System.currentTimeMillis() + (seconds * 1000);
        try {
            double timeTook = elementPresent(seconds);
            if (timeTook >= seconds) {
                throw new TimeoutException(ELEMENT_NOT_PRESENT);
            }
            WebDriverWait wait = new WebDriverWait(element.getDriver(), (long) (seconds - timeTook), defaultPoll);
            wait.until((ExpectedCondition<Boolean>) d -> !element.is().editable());
            timeTook = Math.min((seconds * 1000) - (end - System.currentTimeMillis()), seconds * 1000) / 1000;
            checkNotEditable(seconds, timeTook);
        } catch (TimeoutException e) {
            checkNotEditable(seconds, seconds);
        }
    }

    /**
     * Waits for the element to be enabled. The provided wait time will be used
     * and if the element isn't present after that time, it will fail, and log
     * the issue with a screenshot for traceability and added debugging support.
     *
     * @param seconds - how many seconds to wait for
     */
    public void enabled(double seconds) {
        double end = System.currentTimeMillis() + (seconds * 1000);
        try {
            double timeTook = elementPresent(seconds);
            if (timeTook >= seconds) {
                throw new TimeoutException(ELEMENT_NOT_PRESENT);
            }
            WebDriverWait wait = new WebDriverWait(element.getDriver(), (long) (seconds - timeTook), defaultPoll);
            wait.until(ExpectedConditions.elementToBeClickable(element.defineByElement()));
            timeTook = Math.min((seconds * 1000) - (end - System.currentTimeMillis()), seconds * 1000) / 1000;
            checkEnabled(seconds, timeTook);
        } catch (TimeoutException e) {
            checkEnabled(seconds, seconds);
        }
    }

    /**
     * Waits for the element to not be enabled. The provided wait time will be used
     * and if the element isn't present after that time, it will fail, and log
     * the issue with a screenshot for traceability and added debugging support.
     *
     * @param seconds - how many seconds to wait for
     */
    public void notEnabled(double seconds) {
        double end = System.currentTimeMillis() + (seconds * 1000);
        try {
            double timeTook = elementPresent(seconds);
            if (timeTook >= seconds) {
                throw new TimeoutException(ELEMENT_NOT_PRESENT);
            }
            WebDriverWait wait = new WebDriverWait(element.getDriver(), (long) (seconds - timeTook), defaultPoll);
            wait.until(ExpectedConditions.not(ExpectedConditions.elementToBeClickable(element.defineByElement())));
            timeTook = Math.min((seconds * 1000) - (end - System.currentTimeMillis()), seconds * 1000) / 1000;
            checkNotEnabled(seconds, timeTook);
        } catch (TimeoutException e) {
            checkNotEnabled(seconds, seconds);
        }
    }
}