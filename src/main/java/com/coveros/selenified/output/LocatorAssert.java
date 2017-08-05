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
import com.coveros.selenified.output.Assert.Success;
import com.coveros.selenified.selenium.Action;
import com.coveros.selenified.selenium.Selenium.Locator;

/**
 * An extension of the custom reporting class, focusing on checks involving
 * elements. This should never be called directly from any test steps, but
 * instead supports the Assert class
 *
 * @author Max Saperstone
 * @version 2.0.1
 * @lastupdate 7/20/2017
 */
public class LocatorAssert {

    private Action action;
    private OutputFile outputFile;

    // constants
    private static final String EXPECTED = "Expected to find element with ";
    private static final String ELEMENT = "The element with ";
    private static final String CLASS = "class";

    private static final String PRESENT = "</i> is not present on the page";

    private static final String NOTINPUT = "</i> is not an input on the page";
    private static final String NOTEDITABLE = "</i> is not editable on the page";

    private static final String VALUE = "</i> has the value of <b>";
    private static final String CLASSVALUE = "</i> has a class value of <b>";

    public LocatorAssert(Action action, OutputFile outputFile) {
        this.action = action;
        this.outputFile = outputFile;
    }

    // /////////////////////////////////////////////////////////////////////////
    // a bunch of methods to negatively check for objects using selenium calls
    // ///////////////////////////////////////////////////////////////////////

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
        // wait for the element
        if (!action.is().elementPresent(type, locator, elementMatch)
                && action.waitFor().elementPresent(type, locator, elementMatch) == 1) {
            return 1;
        }
        outputFile.recordExpected(EXPECTED + type + " <i>" + locator + "</i> with class <b>" + expectedClass + "</b>");
        String actualClass = action.get().attribute(type, locator, elementMatch, CLASS);
        // outputFile.record the action
        if (actualClass.equals(expectedClass)) {
            outputFile.recordActual(ELEMENT + type + " <i>" + locator + CLASSVALUE + expectedClass + "</b>",
                    Success.PASS);
            return 0;
        }
        outputFile.recordActual(ELEMENT + type + " <i>" + locator + CLASSVALUE + actualClass + "</b>", Success.FAIL);
        return 1;
    }

    /**
     * Determines if the element is present
     * 
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param elementMatch
     *            - if there are multiple matches of the selector, this is which
     *            match (starting at 0) to interact with
     * @return Boolean: whether the element is present or not
     */
    private boolean isPresent(Locator type, String locator, int elementMatch) {
        if (!action.is().elementPresent(type, locator, elementMatch)) {
            outputFile.recordActual(ELEMENT + type + " <i>" + locator + PRESENT, Success.FAIL);
            return false;
        }
        return true;
    }

    /**
     * Determines if the element is an input
     * 
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param elementMatch
     *            - if there are multiple matches of the selector, this is which
     *            match (starting at 0) to interact with
     * @return Boolean: whether the element is an input or not
     */
    private boolean isInput(Locator type, String locator, int elementMatch) {
        if (!action.is().elementInput(type, locator, elementMatch)) {
            outputFile.recordActual(ELEMENT + type + " <i>" + locator + NOTINPUT, Success.FAIL);
            return false;
        }
        return true;
    }

    /**
     * Determines if the element is enabled
     * 
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param elementMatch
     *            - if there are multiple matches of the selector, this is which
     *            match (starting at 0) to interact with
     * @return Boolean: whether the element is enabled or not
     */
    private boolean isEnabled(Locator type, String locator, int elementMatch) {
        if (!action.is().elementEnabled(type, locator, elementMatch)) {
            outputFile.recordActual(ELEMENT + type + " <i>" + locator + NOTEDITABLE, Success.FAIL);
            return false;
        }
        return true;
    }

    /**
     * Determines if the element is present, an input, and enabled
     * 
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param elementMatch
     *            - if there are multiple matches of the selector, this is which
     *            match (starting at 0) to interact with
     * @return Boolean: whether the element is present, an input, and enabled or
     *         not
     */
    private boolean isPresentInputEnabled(Locator type, String locator, int elementMatch) {
        if (!isPresent(type, locator, elementMatch)) {
            return false;
        }
        if (!isInput(type, locator, elementMatch)) {
            return false;
        }
        return isEnabled(type, locator, elementMatch);
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
        // outputFile.record the action
        outputFile.recordExpected(
                EXPECTED + type + " <i>" + locator + "</i> having a value of <b>" + expectedValue + "</b>");
        // check for the object to the present on the page
        String elementValue;
        if (!isPresent(type, locator, elementMatch)) {
            return 1;
        } else {
            elementValue = action.get().text(type, locator, elementMatch);
        }
        if (!elementValue.equals(expectedValue)) {
            outputFile.recordActual(ELEMENT + type + " <i>" + locator + VALUE + elementValue + "</b>", Success.FAIL);
            return 1;
        }
        outputFile.recordActual(ELEMENT + type + " <i>" + locator + VALUE + elementValue + "</b>", Success.PASS);
        return 0;
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
        // outputFile.record the action
        outputFile.recordExpected(
                EXPECTED + type + " <i>" + locator + "</i> having a value of <b>" + expectedValue + "</b>");
        // check for the object to the present on the page
        String elementValue;
        if (!isPresent(type, locator, elementMatch)) {
            return 1;
        } else {
            elementValue = action.get().value(type, locator, elementMatch);
        }
        if (!elementValue.equals(expectedValue)) {
            outputFile.recordActual(ELEMENT + type + " <i>" + locator + VALUE + elementValue + "</b>", Success.FAIL);
            return 1;
        }
        outputFile.recordActual(ELEMENT + type + " <i>" + locator + VALUE + elementValue + "</b>", Success.PASS);
        return 0;
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
        // outputFile.record the action
        outputFile.recordExpected(EXPECTED + type + " <i>" + locator + "</i> having a css attribute of <i>" + attribute
                + "</i> with a value of <b>" + expectedValue + "</b>");
        // check for the object to the present on the page
        String elementCssValue;
        if (!isPresent(type, locator, elementMatch)) {
            return 1;
        } else {
            elementCssValue = action.get().css(type, locator, elementMatch, attribute);
        }
        if (!elementCssValue.equals(expectedValue)) {
            outputFile.recordActual(ELEMENT + type + " <i>" + locator + "</i> has a css attribute of <i>" + attribute
                    + "</i> with the value of <b>" + elementCssValue + "</b>", Success.FAIL);
            return 1;
        }
        outputFile.recordActual(ELEMENT + type + " <i>" + locator + "</i> has a css attribute of <i>" + attribute
                + "</i> with the value of <b>" + elementCssValue + "</b>", Success.PASS);
        return 0;
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
        // outputFile.record the action
        outputFile.recordExpected(
                EXPECTED + type + " <i>" + locator + "</i> having a selected value of <b>" + expectedValue + "</b>");
        // check for the object to the present on the page
        String elementValue;
        if (!isPresentInputEnabled(type, locator, elementMatch)) {
            return 1;
        } else {
            elementValue = action.get().selectedValue(type, locator, elementMatch);
        }
        if (!elementValue.equals(expectedValue)) {
            outputFile.recordActual(ELEMENT + type + " <i>" + locator + VALUE + elementValue + "</b>", Success.FAIL);
            return 1;
        }
        outputFile.recordActual(ELEMENT + type + " <i>" + locator + VALUE + elementValue + "</b>", Success.PASS);
        return 0;
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
        // outputFile.record the action
        outputFile.recordExpected(
                EXPECTED + type + " <i>" + locator + "</i> having a selected text of <b>" + expectedText + "</b>");
        // check for the object to the present on the page
        String elementText;
        if (!isPresentInputEnabled(type, locator, elementMatch)) {
            return 1;
        } else {
            elementText = action.get().selectedOption(type, locator, elementMatch);
        }
        if (!elementText.equals(expectedText)) {
            outputFile.recordActual(ELEMENT + type + " <i>" + locator + VALUE + elementText + "</b>", Success.FAIL);
            return 1;
        }
        outputFile.recordActual(ELEMENT + type + " <i>" + locator + VALUE + elementText + "</b>", Success.PASS);
        return 0;
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
        // outputFile.record the action
        outputFile.recordExpected(EXPECTED + type + " <i>" + locator + "</i> not having a selected value of <b>"
                + expectedValue + "</b>");
        // check for the object to the present on the page
        String elementValue;
        if (!isPresentInputEnabled(type, locator, elementMatch)) {
            return 1;
        } else {
            elementValue = action.get().selectedValue(type, locator, elementMatch);
        }
        if (elementValue.equals(expectedValue)) {
            outputFile.recordActual(ELEMENT + type + " <i>" + locator + VALUE + elementValue + "</b>", Success.FAIL);
            return 1;
        }
        outputFile.recordActual(ELEMENT + type + " <i>" + locator + VALUE + elementValue + "</b>", Success.PASS);
        return 0;
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
        // outputFile.record the action
        outputFile.recordExpected(
                EXPECTED + type + " <i>" + locator + "</i> with select values of <b>" + expectedValues + "</b>");
        // check for the object to the present on the page
        String[] elementValues;
        if (!isPresentInputEnabled(type, locator, elementMatch)) {
            return 1;
        } else {
            elementValues = action.get().selectOptions(type, locator, elementMatch);
        }
        for (String entry : expectedValues) {
            if (!Arrays.asList(elementValues).contains(entry)) {
                outputFile.recordActual(ELEMENT + type + " <i>" + locator + "</i> does not have the select value of <b>"
                        + entry + "</b>", Success.FAIL);
                return 1;
            }
        }
        for (String entry : elementValues) {
            if (!Arrays.asList(expectedValues).contains(entry)) {
                outputFile.recordActual(
                        ELEMENT + type + " <i>" + locator + VALUE + entry + "</b> which was not expected",
                        Success.FAIL);
                return 1;
            }
        }
        outputFile.recordActual(ELEMENT + type + " <i>" + locator + VALUE + elementValues + "</b>", Success.PASS);
        return 0;
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
        String column = " and column ";
        String element = " within element ";
        // outputFile.record the action
        outputFile.recordExpected("Expected to find cell at row " + row + column + col + element + type + " <i>"
                + locator + "</i> to have the text value of <b>" + text + "</b>");
        // check for the object to the present on the page
        if (!isPresent(type, locator, elementMatch)) {
            return 1;
        }
        String actualText = action.get().tableCell(type, locator, elementMatch, row, col).getText();
        if (!actualText.equals(text)) {
            outputFile.recordActual("Cell at row " + row + column + col + element + type + " <i>" + locator
                    + "</i> has the text value of <b>" + actualText + "</b>", Success.FAIL);
            return 1;
        }
        outputFile.recordActual("Cell at row " + row + column + col + element + type + " <i>" + locator
                + "</i> has the text value of <b>" + actualText + "</b>", Success.PASS);
        return 0;
    }
}