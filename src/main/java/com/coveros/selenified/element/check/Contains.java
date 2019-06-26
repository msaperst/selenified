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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.coveros.selenified.utilities.Constants.*;

/**
 * Contains extends Check to provide some additional checking
 * capabilities. It will handle all checks performed on the actual
 * element. These asserts are custom to the framework, and in addition to
 * providing easy object oriented capabilities, they take screenshots with each
 * check to provide additional traceability, and assist in
 * troubleshooting and debugging failing tests. Contains checks that elements
 * have a particular value associated to them.
 *
 * @author Max Saperstone
 * @version 3.2.0
 * @lastupdate 6/25/2019
 */
abstract class Contains extends Check {

    // ///////////////////////////////////////
    // assessing functionality
    // ///////////////////////////////////////

    /**
     * Checks that the element's class contains the provided expected class.
     * If the element isn't present, this will constitute a failure, same as a
     * mismatch. This information will be logged and recorded, with a screenshot
     * for traceability and added debugging support.
     *
     * @param expectedClass - the expected class value
     */
    abstract void clazz(String expectedClass);

    /**
     * Checks that the element's class contains the provided expected class.
     * If the element isn't present, this will constitute a failure, same as a
     * mismatch. This information will be logged and recorded, with a screenshot
     * for traceability and added debugging support.
     *
     * @param expectedClass - the expected class value
     * @param waitFor       - if waiting, how long to wait for (set to 0 if no wait is desired)
     * @param timeTook      - the amount of time it took for wait for something (assuming we had to wait)
     * @return String: the actual class of the element. null will be returned if the element isn't present
     */
    String checkClazz(String expectedClass, double waitFor, double timeTook) {
        // get the value
        String actualClass = this.element.get().attribute(CLASS);
        // record the result
        if (actualClass == null || !actualClass.contains(expectedClass)) {
            this.reporter.fail(this.element.prettyOutput() + " containing class <b>" + expectedClass + ENDB, waitFor, this.element.prettyOutputStart() + CLASS_VALUE + actualClass + ENDB, timeTook);
        } else {
            this.reporter.pass(this.element.prettyOutput() + " containing class <b>" + expectedClass + ENDB, waitFor, this.element.prettyOutputStart() + CLASS_VALUE + actualClass + "</b>, which contains <b>" +
                    expectedClass + ENDB, timeTook);
        }
        return actualClass;
    }

    /**
     * Checks that the element contains the provided expected attribute. If
     * the element isn't present, this will constitute a failure, same as a
     * mismatch. This information will be logged and recorded, with a screenshot
     * for traceability and added debugging support.
     *
     * @param expectedAttribute - the attribute to check for
     */
    abstract void attribute(String expectedAttribute);

    /**
     * Checks that the element contains the provided expected attribute. If
     * the element isn't present, this will constitute a failure, same as a
     * mismatch. This information will be logged and recorded, with a screenshot
     * for traceability and added debugging support.
     *
     * @param expectedAttribute - the attribute to check for
     * @param waitFor           - if waiting, how long to wait for (set to 0 if no wait is desired)
     * @param timeTook          - the amount of time it took for wait for something (assuming we had to wait)
     * @return String[]: all of the attributes of the element. null will be returned if the element isn't present
     */
    Set<String> checkAttribute(String expectedAttribute, double waitFor, double timeTook) {
        // record the action and get the attributes
        Map<String, String> atts = this.element.get().allAttributes();
        Set<String> allAttributes = new HashSet<>();
        if (atts != null) {
            allAttributes = atts.keySet();
        }
        // record the result
        if (atts == null || !allAttributes.contains(expectedAttribute)) {
            this.reporter.fail(this.element.prettyOutput() + " with attribute <b>" + expectedAttribute + ENDB, waitFor,
                    this.element.prettyOutputStart() + " does not contain the attribute of <b>" + expectedAttribute + ENDB +
                            ONLY_VALUE + String.join(", " + allAttributes) + ENDB, timeTook);
        } else {
            this.reporter.pass(this.element.prettyOutput() + " with attribute <b>" + expectedAttribute + ENDB, waitFor,
                    this.element.prettyOutputStart() + " contains the attribute of <b>" + expectedAttribute + ENDB, timeTook);
        }
        return allAttributes;
    }

    /**
     * Checks that the element's text contains the provided expected text. If
     * the element isn't present, this will constitute a failure, same as a
     * mismatch. This information will be logged and recorded, with a screenshot
     * for traceability and added debugging support.
     *
     * @param expectedText - the expected value of the element
     */
    abstract void text(String expectedText);

    /**
     * Checks that the element's text contains the provided expected text. If
     * the element isn't present, this will constitute a failure, same as a
     * mismatch. This information will be logged and recorded, with a screenshot
     * for traceability and added debugging support.
     *
     * @param expectedText - the expected value of the element
     * @param waitFor      - if waiting, how long to wait for (set to 0 if no wait is desired)
     * @param timeTook     - the amount of time it took for wait for something (assuming we had to wait)
     * @return String: the actual text of the element. null will be returned if the element isn't present
     */
    String checkText(String expectedText, double waitFor, double timeTook) {
        // get the value
        String elementValue = this.element.get().text();
        // record the result
        if (elementValue == null || !elementValue.contains(expectedText)) {
            this.reporter.fail(this.element.prettyOutput() + CONTAINS_TEXT + expectedText + ENDB, waitFor, this.element.prettyOutputStart() + HAS_TEXT + elementValue + ENDB, timeTook);
        } else {
            this.reporter.pass(this.element.prettyOutput() + CONTAINS_TEXT + expectedText + ENDB, waitFor, this.element.prettyOutputStart() + HAS_TEXT + elementValue + ENDB, timeTook);
        }
        return elementValue;
    }

    /**
     * Checks that the element's value contains the provided expected value.
     * If the element isn't present or an input, this will constitute a failure,
     * same as a mismatch. This information will be logged and recorded, with a
     * screenshot for traceability and added debugging support.
     *
     * @param expectedValue the expected value of the element
     */
    abstract void value(String expectedValue);

    /**
     * Checks that the element's value contains the provided expected value.
     * If the element isn't present or an input, this will constitute a failure,
     * same as a mismatch. This information will be logged and recorded, with a
     * screenshot for traceability and added debugging support.
     *
     * @param expectedValue the expected value of the element
     * @param waitFor       - if waiting, how long to wait for (set to 0 if no wait is desired)
     * @param timeTook      - the amount of time it took for wait for something (assuming we had to wait)
     * @return String: the actual value of the element. null will be returned if the element isn't present or an input
     */
    String checkValue(String expectedValue, double waitFor, double timeTook) {
        // record the action and get the attributes
        String elementValue = this.element.get().value();
        // record the expected
        if (elementValue == null || !elementValue.contains(expectedValue)) {
            this.reporter.fail(this.element.prettyOutput() + expectedValue + elementValue + ENDB, waitFor, this.element.prettyOutputStart() + HAS_VALUE + elementValue + ENDB, timeTook);
        } else {
            this.reporter.pass(this.element.prettyOutput() + expectedValue + elementValue + ENDB, waitFor, this.element.prettyOutputStart() + HAS_VALUE + elementValue + ENDB, timeTook);
        }
        return elementValue;
    }

    /**
     * Checks that the element's options contains the provided expected
     * option. If the element isn't present or a select, this will constitute a
     * failure, same as a mismatch. This information will be logged and
     * recorded, with a screenshot for traceability and added debugging support.
     *
     * @param expectedOption the option expected in the list
     */
    abstract void selectOption(String expectedOption);

    /**
     * Checks that the element's options contains the provided expected
     * option. If the element isn't present or a select, this will constitute a
     * failure, same as a mismatch. This information will be logged and
     * recorded, with a screenshot for traceability and added debugging support.
     *
     * @param expectedOption the option expected in the list
     * @param waitFor        - if waiting, how long to wait for (set to 0 if no wait is desired)
     * @param timeTook       - the amount of time it took for wait for something (assuming we had to wait)
     * @return String[]: all of the select options of the element. null will be returned if the element isn't present or a select
     */
    @SuppressWarnings("squid:S1168")
    String[] checkSelectOption(String expectedOption, double waitFor, double timeTook) {
        String withTheOption = " with the option <b>";
        // record the action, and check for select
        if (!isPresentSelect(this.element.prettyOutput() + withTheOption + expectedOption +
                AVAILABLE_TO_BE_SELECTED, waitFor)) {
            return null;    // returning null to indicate that element isn't present/select, instead of indicating no options exist
        }
        // get the select options
        String[] allOptions = this.element.get().selectOptions();
        // record the expected
        if (!Arrays.asList(allOptions).contains(expectedOption)) {
            this.reporter.fail(this.element.prettyOutput() + withTheOption + expectedOption +
                    AVAILABLE_TO_BE_SELECTED, waitFor, this.element.prettyOutputStart() +
                    " is present but does not contain the option <b>" + expectedOption + ENDB, timeTook);
        } else {
            this.reporter.pass(this.element.prettyOutput() + withTheOption + expectedOption +
                    AVAILABLE_TO_BE_SELECTED, waitFor, this.element.prettyOutputStart() +
                    " is present and contains the option <b>" + expectedOption + ENDB, timeTook);
        }
        return allOptions;
    }

    /**
     * Checks that the element's options contains the provided expected value.
     * If the element isn't present or a select, this will constitute a failure,
     * same as a mismatch. This information will be logged and recorded, with a
     * screenshot for traceability and added debugging support.
     *
     * @param expectedValue the expected input value of the element
     */
    abstract void selectValue(String expectedValue);

    /**
     * Checks that the element's options contains the provided expected value.
     * If the element isn't present or a select, this will constitute a failure,
     * same as a mismatch. This information will be logged and recorded, with a
     * screenshot for traceability and added debugging support.
     *
     * @param expectedValue the expected input value of the element
     * @param waitFor       - if waiting, how long to wait for (set to 0 if no wait is desired)
     * @param timeTook      - the amount of time it took for wait for something (assuming we had to wait)
     * @return String[]: all of the select values of the element. null will be returned if the element isn't present or a select
     */
    @SuppressWarnings("squid:S1168")
    String[] checkSelectValue(String expectedValue, double waitFor, double timeTook) {
        String havingSelectValue = " having a select value of <b>";
        // record the action, and check for select
        if (!isPresentSelect(this.element.prettyOutput() + havingSelectValue + expectedValue +
                AVAILABLE_TO_BE_SELECTED, waitFor)) {
            return null;    // returning null to indicate that element isn't present/select, instead of indicating no options exist
        }
        // get the select values
        String[] allValues = this.element.get().selectValues();
        // record the expected
        if (!Arrays.asList(allValues).contains(expectedValue)) {
            this.reporter.fail(this.element.prettyOutput() + havingSelectValue + expectedValue +
                    AVAILABLE_TO_BE_SELECTED, waitFor, this.element.prettyOutputStart() + EXCLUDES_VALUE + expectedValue + ENDB + ONLY_VALUE +
                    Arrays.toString(allValues) + ENDB, timeTook);
        } else {
            this.reporter.pass(this.element.prettyOutput() + havingSelectValue + expectedValue +
                    AVAILABLE_TO_BE_SELECTED, waitFor, this.element.prettyOutputStart() + CONTAINS_VALUE + expectedValue + ENDB, timeTook);
        }
        return allValues;
    }

    /**
     * Checks that the element has the expected number of options. If the
     * element isn't present or a select, this will constitute a failure, same
     * as a mismatch. This information will be logged and recorded, with a
     * screenshot for traceability and added debugging support.
     *
     * @param numOfOptions the expected number of options in the select element
     */
    abstract void selectOptions(int numOfOptions);

    /**
     * Checks that the element has the expected number of options. If the
     * element isn't present or a select, this will constitute a failure, same
     * as a mismatch. This information will be logged and recorded, with a
     * screenshot for traceability and added debugging support.
     *
     * @param numOfOptions the expected number of options in the select element
     * @param waitFor      - if waiting, how long to wait for (set to 0 if no wait is desired)
     * @param timeTook     - the amount of time it took for wait for something (assuming we had to wait)
     * @return Integer: the number of select options of the element. -1 will be returned if the element isn't present or a select
     */
    int checkSelectOptions(int numOfOptions, double waitFor, double timeTook) {
        String numberOfSelectValues = " with number of select values equal to <b>";
        // record the action, and check for select
        if (!isPresentSelect(this.element.prettyOutput() + numberOfSelectValues + numOfOptions + ENDB, waitFor)) {
            return -1;
        }
        // get the select options
        int elementValues = this.element.get().numOfSelectOptions();
        // record the expected
        if (elementValues != numOfOptions) {
            this.reporter.fail(this.element.prettyOutput() + numberOfSelectValues + numOfOptions + ENDB,
                    waitFor, this.element.prettyOutputStart() + " has <b>" + numOfOptions + "</b> select options", timeTook);
        } else {
            this.reporter.pass(this.element.prettyOutput() + numberOfSelectValues + numOfOptions + ENDB,
                    waitFor, this.element.prettyOutputStart() + " has <b>" + numOfOptions + "</b> select options", timeTook);
        }
        return elementValues;
    }

    /**
     * Checks that the element has the expected number of columns. If the
     * element isn't present or a table, this will constitute a failure, same as
     * a mismatch. This information will be logged and recorded, with a
     * screenshot for traceability and added debugging support.
     *
     * @param numOfColumns the expected number of column elements of a table
     */
    abstract void columns(int numOfColumns);

    /**
     * Checks that the element has the expected number of columns. If the
     * element isn't present or a table, this will constitute a failure, same as
     * a mismatch. This information will be logged and recorded, with a
     * screenshot for traceability and added debugging support.
     *
     * @param numOfColumns the expected number of column elements of a table
     * @param waitFor      - if waiting, how long to wait for (set to 0 if no wait is desired)
     * @param timeTook     - the amount of time it took for wait for something (assuming we had to wait)
     * @return Integer: the number of columns of the element. -1 will be returned if the element isn't present or a table
     */
    int checkColumns(int numOfColumns, double waitFor, double timeTook) {
        String numberOfTableColumnsEqual = " with the number of table columns equal to <b>";
        // record the action, and check for table
        if (!isPresentTable(this.element.prettyOutput() + numberOfTableColumnsEqual + numOfColumns + ENDB, waitFor)) {
            return -1;
        }
        // get the table columns
        int actualNumOfCols = this.element.get().numOfTableColumns();
        // record the expected
        if (actualNumOfCols != numOfColumns) {
            this.reporter.fail(this.element.prettyOutput() + numberOfTableColumnsEqual + numOfColumns + ENDB, waitFor,
                    this.element.prettyOutputStart() + " does not have the number of columns <b>" + numOfColumns +
                            "</b>. Instead, " + actualNumOfCols + " columns were found", timeTook);
        } else {
            this.reporter.pass(this.element.prettyOutput() + numberOfTableColumnsEqual + numOfColumns + ENDB, waitFor,
                    this.element.prettyOutputStart() + " has " + actualNumOfCols + "</b> columns", timeTook);
        }
        return actualNumOfCols;
    }

    /**
     * Checks that the element has the expected number of rows. If the element
     * isn't present or a table, this will constitute a failure, same as a
     * mismatch. This information will be logged and recorded, with a screenshot
     * for traceability and added debugging support.
     *
     * @param numOfRows the expected number of row elements of a table
     */
    abstract void rows(int numOfRows);

    /**
     * Checks that the element has the expected number of rows. If the element
     * isn't present or a table, this will constitute a failure, same as a
     * mismatch. This information will be logged and recorded, with a screenshot
     * for traceability and added debugging support.
     *
     * @param numOfRows the expected number of row elements of a table
     * @param waitFor   - if waiting, how long to wait for (set to 0 if no wait is desired)
     * @param timeTook  - the amount of time it took for wait for something (assuming we had to wait)
     * @return Integer: the number of columns of the element. -1 will be returned if the element isn't present or a table
     */
    int checkRows(int numOfRows, double waitFor, double timeTook) {
        String numberOfTableRows = " with the number of table rows equal to <b>";
        // record the action, and check for table
        if (!isPresentTable(this.element.prettyOutput() + numberOfTableRows + numOfRows + ENDB, waitFor)) {
            return -1;
        }
        // get the table columns
        int actualNumOfRows = this.element.get().numOfTableRows();
        // record the expected
        if (actualNumOfRows != numOfRows) {
            this.reporter.fail(this.element.prettyOutput() + numberOfTableRows + numOfRows + ENDB, waitFor,
                    this.element.prettyOutputStart() + " does not have the number of rows <b>" + numOfRows +
                            "</b>. Instead, " + actualNumOfRows + " rows were found", timeTook);
        } else {
            this.reporter.pass(this.element.prettyOutput() + numberOfTableRows + numOfRows + ENDB, waitFor,
                    this.element.prettyOutputStart() + " has " + actualNumOfRows + "</b> rows", timeTook);
        }
        return actualNumOfRows;
    }
}