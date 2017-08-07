package com.coveros.selenified.selenium;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import com.coveros.selenified.selenium.Assert.Success;
import com.coveros.selenified.selenium.Selenium.Locator;
import com.coveros.selenified.output.OutputFile;

public class Contains implements Subset {
    // this will be the name of the file we write all commands out to
    private OutputFile file;

    // what locator actions are available in webdriver
    // this is the driver that will be used for all selenium actions
    private Action action;

    // constants
    private static final String EXPECTED = "Expected to find ";
    private static final String CLASS = "class";

    private static final String NOTINPUT = " is not an input on the page";

    private static final String VALUE = " has the value of <b>";
    private static final String HASVALUE = " contains the value of <b>";
    private static final String ONLYVALUE = ", only the values <b>";
    private static final String CLASSVALUE = " has a class value of <b>";

    public Contains(Action action, OutputFile file) {
        this.action = action;
        this.file = file;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setOutputFile(OutputFile file) {
        this.file = file;
    }

    // ///////////////////////////////////////
    // assessing functionality
    // ///////////////////////////////////////

    /**
     * compares the expected attributes from a select value with the actual
     * attributes from the element
     *
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param numOfOptions
     *            the expected number of options in the select element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int selectOptions(Locator type, String locator, int numOfOptions) {
        return selectOptions(new Element(type, locator), numOfOptions);
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
     * @param numOfOptions
     *            the expected number of options in the select element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int selectOptions(Locator type, String locator, int elementMatch, int numOfOptions) {
        return selectOptions(new Element(type, locator, elementMatch), numOfOptions);
    }

    /**
     * compares the number of expected rows with the actual number of rows of a
     * table with from a table element
     *
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param numOfRows
     *            the number of rows in a table
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int rows(Locator type, String locator, int numOfRows) {
        return rows(new Element(type, locator), numOfRows);
    }

    /**
     * compares the number of expected rows with the actual number of rows of a
     * table with from a table element
     *
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param elementMatch
     *            - if there are multiple matches of the selector, this is which
     *            match (starting at 0) to interact with
     * @param numOfRows
     *            the number of rows in a table
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int rows(Locator type, String locator, int elementMatch, int numOfRows) {
        return rows(new Element(type, locator, elementMatch), numOfRows);
    }

    /**
     * compares the number of expected columns with the actual number of columns
     * of a table with from a table element
     *
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param numOfColumns
     *            the number of columns in a table
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int columns(Locator type, String locator, int numOfColumns) {
        return columns(new Element(type, locator), numOfColumns);
    }

    /**
     * compares the number of expected columns with the actual number of columns
     * of a table with from a table element
     *
     * @param type
     *            - the locator type e.g. Locator.id, Locator.xpath
     * @param locator
     *            - the locator string e.g. login, //input[@id='login']
     * @param elementMatch
     *            - if there are multiple matches of the selector, this is which
     *            match (starting at 0) to interact with
     * @param numOfColumns
     *            the number of columns in a table
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int columns(Locator type, String locator, int elementMatch, int numOfColumns) {
        return columns(new Element(type, locator, elementMatch), numOfColumns);
    }

    ///////////////////////////////////////////////////
    // Our actual full implementation of the above and inherited overloaded
    /////////////////////////////////////////////////// methods
    ///////////////////////////////////////////////////

    /**
     * checks to see if an element contains a particular class
     *
     * @param element
     *            - the element to be assessed
     * @param expectedClass
     *            - the expected class value
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int classs(Element element, String expectedClass) {
        // wait for the element
        if (!action.is().elementPresent(element) && action.waitFor().elementPresent(element) == 1) {
            return 1;
        }
        // file.record the action
        file.recordExpected(EXPECTED + element.prettyOutput() + " containing class <b>" + expectedClass + "</b>");
        // get the class
        String actualClass = action.get().attribute(element, CLASS);
        // file.record the action
        if (!actualClass.contains(expectedClass)) {
            file.recordActual(element.prettyOutputStart() + CLASSVALUE + actualClass + "</b>", Success.FAIL);
            file.addError();
            return 1;
        }
        file.recordActual(element.prettyOutputStart() + CLASSVALUE + actualClass + "</b>, which contains <b>"
                + expectedClass + "</b>", Success.PASS);
        return 0;
    }

    /**
     * checks to see if an element has an attribute associated with it
     *
     * @param element
     *            - the element to be assessed
     * @param attribute
     *            - the attribute to check for
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int attribute(Element element, String attribute) {
        // wait for the element
        if (!action.is().elementPresent(element) && action.waitFor().elementPresent(element) == 1) {
            return 1;
        }
        // file.record the action
        file.recordExpected(EXPECTED + element.prettyOutput() + " with attribute <b>" + attribute + "</b>");
        // get all attributes
        Map<String, String> attributes = action.get().allAttributes(element);
        if (attributes == null) {
            file.recordActual("Unable to assess the attributes of " + element.prettyOutput(), Success.FAIL);
            file.addError();
            return 1;
        }
        Set<String> keys = attributes.keySet();
        String[] allAttributes = keys.toArray(new String[keys.size()]);
        // file.record the action
        if (!Arrays.asList(allAttributes).contains(attribute)) {
            file.recordActual(element.prettyOutputStart() + " does not contain the attribute of <b>" + attribute
                    + "</b>" + ONLYVALUE + Arrays.toString(allAttributes) + "</b>", Success.FAIL);
            file.addError();
            return 1;
        }
        file.recordActual(element.prettyOutputStart() + " contains the attribute of <b>" + attribute + "</b>",
                Success.PASS);
        return 0;
    }

    /**
     * compares the expected element value with the actual value from an element
     *
     * @param element
     *            - the element to be assessed
     * @param expectedValue
     *            the expected value of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int text(Element element, String expectedValue) {
        // wait for the element
        if (!action.is().elementPresent(element) && action.waitFor().elementPresent(element) == 1) {
            return 1;
        }
        // file.record the action
        file.recordExpected(EXPECTED + element.prettyOutput() + HASVALUE + expectedValue + "</b>");
        // check for the object to the present on the page
        String elementValue = action.get().text(element);
        if (!elementValue.contains(expectedValue)) {
            file.recordActual(element.prettyOutputStart() + VALUE + elementValue + "</b>", Success.FAIL);
            file.addError();
            return 1;
        }
        file.recordActual(element.prettyOutputStart() + VALUE + elementValue + "</b>", Success.PASS);
        return 0;
    }

    /**
     * compares the expected element value with the actual value from an element
     *
     * @param element
     *            - the element to be assessed
     * @param expectedValue
     *            the expected value of the element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int value(Element element, String expectedValue) {
        // wait for the element
        if (!action.is().elementPresent(element) && action.waitFor().elementPresent(element) == 1) {
            return 1;
        }
        // file.record the action
        file.recordExpected(EXPECTED + element.prettyOutput() + HASVALUE + expectedValue + "</b>");
        // verify this is an input element
        if (!action.is().elementInput(element)) {
            file.recordActual(element.prettyOutputStart() + NOTINPUT, Success.FAIL);
            file.addError();
            return 1;
        }
        // check for the object to the present on the page
        String elementValue = action.get().value(element);
        if (!elementValue.contains(expectedValue)) {
            file.recordActual(element.prettyOutputStart() + VALUE + elementValue + "</b>", Success.FAIL);
            file.addError();
            return 1;
        }
        file.recordActual(element.prettyOutputStart() + VALUE + elementValue + "</b>", Success.PASS);
        return 0;
    }

    /**
     * checks to see if an option is available to be selected on the page
     *
     * @param element
     *            - the element to be assessed
     * @param option
     *            the option expected in the list
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int selectOption(Element element, String option) {
        // wait for the select
        if (!Helper.isPresentSelect(action, file, element, EXPECTED + element.prettyOutput() + " with the option <b>"
                + option + "</b> available to be selected on the page")) {
            return 1;
        }
        // check for the object to the editable
        String[] allOptions = action.get().selectOptions(element);
        if (!Arrays.asList(allOptions).contains(option)) {
            file.recordActual(element.prettyOutputStart() + " is present but does not contain the option " + "<b>"
                    + option + "</b>", Success.FAIL);
            file.addError();
            return 1;
        }
        file.recordActual(
                element.prettyOutputStart() + " is present and contains the option " + "<b>" + option + "</b>",
                Success.PASS);
        return 0;
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
    public int selectValue(Element element, String selectValue) {
        // wait for the select
        if (!Helper.isPresentSelect(action, file, element, EXPECTED + element.prettyOutput()
                + " having a select value of <b>" + selectValue + "</b> available to be selected on the page")) {
            return 1;
        }
        // check for the object to the present on the page
        String[] elementValues = action.get().selectValues(element);
        if (!Arrays.asList(elementValues).contains(selectValue)) {
            file.recordActual(element.prettyOutputStart() + " does not contain the value of <b>" + selectValue + "</b>"
                    + ONLYVALUE + Arrays.toString(elementValues) + "</b>", Success.FAIL);
            file.addError();
            return 1;
        }
        file.recordActual(element.prettyOutputStart() + HASVALUE + selectValue + "</b>", Success.PASS);
        return 0;
    }

    /**
     * compares the number of expected attributes from a select value with the
     * actual number of attributes from the element
     *
     * @param element
     *            - the element to be assessed
     * @param numOfOptions
     *            the expected number of options in the select element
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int selectOptions(Element element, int numOfOptions) {
        // wait for the select
        if (!Helper.isPresentSelect(action, file, element, EXPECTED + element.prettyOutput()
                + " with number of select values equal to <b>" + numOfOptions + "</b>")) {
            return 1;
        }
        // check for the object to the present on the page
        int elementValues = action.get().numOfSelectOptions(element);
        if (elementValues != numOfOptions) {
            file.recordActual(element.prettyOutputStart() + " has <b>" + numOfOptions + "</b>" + " select options",
                    Success.FAIL);
            file.addError();
            return 1;
        }
        file.recordActual(element.prettyOutputStart() + " has <b>" + numOfOptions + "</b>" + " select options",
                Success.PASS);
        return 0;
    }

    /**
     * compares the number of expected columns with the actual number of columns
     * of a table with from a table element
     *
     * @param element
     *            - the element to be assessed
     * @param numOfColumns
     *            the expected number of column elements of a table
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int columns(Element element, int numOfColumns) {
        // wait for the table
        if (!Helper.isPresentTable(action, file, element, EXPECTED + element.prettyOutput()
                + " with the number of table columns equal to <b>" + numOfColumns + "</b>")) {
            return 1;
        }
        int actualNumOfCols = action.get().numOfTableColumns(element);
        if (actualNumOfCols != numOfColumns) {
            file.recordActual(element.prettyOutputStart() + " does not have the number of columns <b>" + numOfColumns
                    + "</b>. Instead, " + actualNumOfCols + " columns were found", Success.FAIL);
            file.addError();
            return 1;
        }
        file.recordActual(element.prettyOutputStart() + "has " + actualNumOfCols + "</b> columns", Success.PASS);
        return 0;
    }

    /**
     * compares the number of expected rows with the actual number of rows of a
     * table with from a table element
     *
     * @param element
     *            - the element to be assessed
     * @param numOfRows
     *            the expected number of row elements of a table
     * @return Integer: 1 if a failure and 0 if a pass
     */
    public int rows(Element element, int numOfRows) {
        // wait for the table
        if (!Helper.isPresentTable(action, file, element, EXPECTED + element.prettyOutput()
                + " with the number of table rows equal to <b>" + numOfRows + "</b>")) {
            return 1;
        }
        int actualNumOfRows = action.get().numOfTableRows(element);
        if (actualNumOfRows != numOfRows) {
            file.recordActual(element.prettyOutputStart() + " does not have the number of rows <b>" + numOfRows
                    + "</b>. Instead, " + actualNumOfRows + " rows were found", Success.FAIL);
            file.addError();
            return 1;
        }
        file.recordActual(element.prettyOutputStart() + "has " + actualNumOfRows + "</b> rows", Success.PASS);
        return 0;
    }
}