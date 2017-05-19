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

package tools.output;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.testng.log4testng.Logger;

import selenified.exceptions.InvalidLocatorTypeException;
import tools.General;
import tools.output.Assert.Result;
import tools.output.Selenium.Locators;

/**
 * Selenium Webdriver Before each action is performed a screenshot is taken
 * After each check is performed a screenshot is taken These are all placed into
 * the output file
 *
 * @author Max Saperstone
 * @version 2.0.0
 * @lastupdate 5/15/2017
 */
public class LocatorAction {

	private static final Logger log = Logger.getLogger(General.class);

	// this will be the name of the file we write all commands out to
	private OutputFile file;

	// what locator actions are available in webdriver
	// this is our driver that will be used for all selenium actions
	private WebDriver driver;

	// constants
	private static final String VALUE = "value";
	private static final String IN = "' in ";

	private static final String WAIT = "Wait up to ";
	private static final String WAITING = "After waiting ";
	private static final String WAITED = "Waited ";
	private static final String SECONDS = " seconds for ";

	private static final String CHECKING = "Checking for ";

	private static final String PRESENT = " to be present";
	private static final String DISPLAYED = " to be displayed";
	private static final String ENABLED = " to be enabled";

	private static final String NOTPRESENT = " as it is not present";
	private static final String NOTDISPLAYED = " as it is not displayed";
	private static final String NOTENABLED = " as it is not enabled";

	private static final String CANTTYPE = "Unable to type in ";

	/**
	 * our constructor, determining which browser use and how to run the
	 * browser: either grid or standalone
	 *
	 * @param driver
	 *            - the webdriver used to control the browser
	 * @param file
	 *            - the TestOutput file. This is provided by the
	 *            SeleniumTestBase functionality
	 */
	public LocatorAction(WebDriver driver, OutputFile file) {
		this.driver = driver;
		this.file = file;
	}

	/**
	 * a method for waiting until an element is present
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @param seconds
	 *            : the number of seconds to wait
	 * @return Integer - the number of errors encountered while executing these
	 *         steps
	 * @throws IOException
	 */
	public int waitForElementPresent(Locators type, String locator, long seconds) throws IOException {
		String action = WAIT + seconds + SECONDS + type + " " + locator + PRESENT;
		String expected = type + " " + locator + " is present";
		// wait for up to XX seconds for our error message
		long end = System.currentTimeMillis() + (seconds * 1000);
		while (System.currentTimeMillis() < end) {
			try { // If results have been returned, the results are displayed in
					// a drop down.
				getWebElement(type, locator).getText();
				break;
			} catch (NoSuchElementException | StaleElementReferenceException e) {
				log.error(e);
			}
		}
		double timetook = Math.min((seconds * 1000) - (end - System.currentTimeMillis()), seconds * 1000);
		timetook = timetook / 1000;
		if (!isElementPresent(type, locator, false)) {
			file.recordAction(action, expected, WAITING + timetook + SECONDS + type + " " + locator + " is not present",
					Result.FAILURE);
			file.addError();
			return 1;
		}
		file.recordAction(action, expected, WAITED + timetook + SECONDS + type + " " + locator + PRESENT,
				Result.SUCCESS);
		return 0;
	}

	/**
	 * a method for waiting until an element is no longer present
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @param seconds
	 *            : the number of seconds to wait
	 * @return Integer - the number of errors encountered while executing these
	 *         steps
	 * @throws IOException
	 */
	public int waitForElementNotPresent(Locators type, String locator, long seconds) throws IOException {
		String action = WAIT + seconds + SECONDS + type + " " + locator + " to not be present";
		String expected = type + " " + locator + " is not present";
		// wait for up to XX seconds for our error message
		long end = System.currentTimeMillis() + (seconds * 1000);
		while (System.currentTimeMillis() < end) {
			if (!isElementPresent(type, locator, false)) {
				break;
			}
		}
		double timetook = Math.min((seconds * 1000) - (end - System.currentTimeMillis()), seconds * 1000);
		timetook = timetook / 1000;
		if (isElementPresent(type, locator, false)) {
			file.recordAction(action, expected,
					WAITING + timetook + SECONDS + type + " " + locator + " is still present", Result.FAILURE);
			file.addError();
			return 1;
		}
		file.recordAction(action, expected, WAITED + timetook + SECONDS + type + " " + locator + " to not be present",
				Result.SUCCESS);
		return 0;
	}

	/**
	 * a method for waiting until an element is displayed
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @param seconds
	 *            : the number of seconds to wait
	 * @return Integer - the number of errors encountered while executing these
	 *         steps
	 * @throws IOException
	 */
	public int waitForElementDisplayed(Locators type, String locator, int seconds) throws IOException {
		String action = WAIT + seconds + SECONDS + type + " " + locator + DISPLAYED;
		String expected = type + " " + locator + " is displayed";
		double start = System.currentTimeMillis();
		if (!isElementPresent(type, locator, false)) {
			int success = waitForElementPresent(type, locator, seconds);
			if (success == 1) {
				return success;
			}
		}
		WebElement element = getWebElement(type, locator);
		if (!element.isDisplayed()) {
			// wait for up to XX seconds
			long end = System.currentTimeMillis() + (seconds * 1000);
			while (System.currentTimeMillis() < end) {
				if (element.isDisplayed()) {
					break;
				}
			}
		}
		double timetook = (System.currentTimeMillis() - start) / 1000;
		if (!element.isDisplayed()) {
			file.recordAction(action, expected,
					WAITING + timetook + SECONDS + type + " " + locator + " is not displayed", Result.FAILURE);
			file.addError();
			return 1;
		}
		file.recordAction(action, expected, WAITED + timetook + SECONDS + type + " " + locator + DISPLAYED,
				Result.SUCCESS);
		return 0;
	}

	/**
	 * a method for waiting until an element is not displayed
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @param seconds
	 *            : the number of seconds to wait
	 * @return Integer - the number of errors encountered while executing these
	 *         steps
	 * @throws IOException
	 */
	public int waitForElementNotDisplayed(Locators type, String locator, int seconds) throws IOException {
		// this might fail if the element disappears completely
		String action = WAIT + seconds + SECONDS + type + " " + locator + " to not be displayed";
		String expected = type + " " + locator + " is not displayed";
		double start = System.currentTimeMillis();
		WebElement element = getWebElement(type, locator);
		if (element.isDisplayed()) {
			// wait for up to XX seconds
			long end = System.currentTimeMillis() + (seconds * 1000);
			while (System.currentTimeMillis() < end) {
				if (!element.isDisplayed()) {
					break;
				}
			}
		}
		double timetook = (System.currentTimeMillis() - start) / 1000;
		if (element.isDisplayed()) {
			file.recordAction(action, expected,
					WAITING + timetook + SECONDS + type + " " + locator + " is still displayed", Result.FAILURE);
			file.addError();
			return 1;
		}
		file.recordAction(action, expected, WAITED + timetook + SECONDS + type + " " + locator + " to not be displayed",
				Result.SUCCESS);
		return 0;
	}

	/**
	 * a method for waiting until an element is enabled
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @param seconds
	 *            : the number of seconds to wait
	 * @return Integer - the number of errors encountered while executing these
	 *         steps
	 * @throws IOException
	 */
	public int waitForElementEnabled(Locators type, String locator, int seconds) throws IOException {
		String action = WAIT + seconds + SECONDS + type + " " + locator + ENABLED;
		String expected = type + " " + locator + " is enabled";
		double start = System.currentTimeMillis();
		if (!isElementEnabled(type, locator, false)) {
			if (!isElementPresent(type, locator, false)) {
				waitForElementPresent(type, locator, seconds);
			}
			if (!isElementEnabled(type, locator, false)) {
				WebElement element = getWebElement(type, locator);
				// wait for up to XX seconds for our error message
				long end = System.currentTimeMillis() + (seconds * 1000);
				while (System.currentTimeMillis() < end) {
					// If results have been returned, the results are displayed
					// in a drop down.
					if (element.isEnabled()) {
						break;
					}
				}
			}
		}
		double timetook = (System.currentTimeMillis() - start) / 1000;
		if (!isElementEnabled(type, locator, false)) {
			file.recordAction(action, expected, WAITING + timetook + SECONDS + type + " " + locator + " is not enabled",
					Result.FAILURE);
			file.addError();
			return 1;
		}
		file.recordAction(action, expected, WAITED + timetook + SECONDS + type + " " + locator + ENABLED,
				Result.SUCCESS);
		return 0;
	}

	/**
	 * a method for waiting until an element is not enabled
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @param seconds
	 *            : the number of seconds to wait
	 * @return Integer - the number of errors encountered while executing these
	 *         steps
	 * @throws IOException
	 */
	public int waitForElementNotEnabled(Locators type, String locator, int seconds) throws IOException {
		// this might fail if the element is no longer present
		String action = WAIT + seconds + SECONDS + type + " " + locator + " to not be enabled";
		String expected = type + " " + locator + " is not enabled";
		double start = System.currentTimeMillis();
		WebElement element = getWebElement(type, locator);
		if (element.isEnabled()) {
			// wait for up to XX seconds
			long end = System.currentTimeMillis() + (seconds * 1000);
			while (System.currentTimeMillis() < end) {
				if (!element.isEnabled()) {
					break;
				}
			}
		}
		double timetook = (System.currentTimeMillis() - start) / 1000;
		if (element.isEnabled()) {
			file.recordAction(action, expected,
					WAITING + timetook + SECONDS + type + " " + locator + " is still enabled", Result.FAILURE);
			file.addError();
			return 1;
		}
		file.recordAction(action, expected, WAITED + timetook + SECONDS + type + " " + locator + " to not be enabled",
				Result.SUCCESS);
		return 0;
	}

	/**
	 * a method for checking if an element is present
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @param print
	 *            : whether or not to printout the action
	 * @return boolean: whether the element is present or not
	 * @throws IOException
	 */
	public boolean isElementPresent(Locators type, String locator, boolean print) throws IOException {
		boolean isPresent = false;
		try {
			getWebElement(type, locator).getText();
			isPresent = true;
		} catch (NoSuchElementException | StaleElementReferenceException e) {
			log.error(e);
		}
		if (print) {
			file.recordExpected(CHECKING + type + " " + locator + PRESENT);
		}
		return isPresent;
	}

	/**
	 * a method for checking if an element is an input; it needs to be an input,
	 * select, or textarea
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @param print
	 *            : whether or not to printout the action
	 * @return boolean: whether the element is present or not
	 * @throws IOException
	 */
	public boolean isElementInput(Locators type, String locator, boolean print) throws IOException {
		boolean isInput = false;
		try {
			WebElement element = getWebElement(type, locator);
			if ("input".equalsIgnoreCase(element.getTagName()) || "textarea".equalsIgnoreCase(element.getTagName())
					|| "select".equalsIgnoreCase(element.getTagName())) {
				isInput = true;
			}
		} catch (NoSuchElementException e) {
			log.error(e);
		}
		if (print) {
			file.recordExpected(CHECKING + type + " " + locator + " to be an input element");
		}
		return isInput;
	}

	/**
	 * a method for checking if an element is enabled
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @param print
	 *            : whether or not to printout the action
	 * @return boolean: whether the element is present or not
	 * @throws IOException
	 */
	public boolean isElementEnabled(Locators type, String locator, boolean print) throws IOException {
		boolean isEnabled = false;
		try {
			isEnabled = getWebElement(type, locator).isEnabled();
		} catch (NoSuchElementException e) {
			log.error(e);
		}
		if (print) {
			file.recordExpected(CHECKING + type + " " + locator + ENABLED);
		}
		return isEnabled;
	}

	/**
	 * a method for checking if an element is checked
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @param print
	 *            : whether or not to printout the action
	 * @return boolean: whether the element is checked or not
	 * @throws IOException
	 */
	public boolean isElementChecked(Locators type, String locator, boolean print) throws IOException {
		boolean isChecked = false;
		try {
			isChecked = getWebElement(type, locator).isSelected();
		} catch (NoSuchElementException e) {
			log.error(e);
		}
		if (print) {
			file.recordExpected(CHECKING + type + " " + locator + " to be checked");
		}
		return isChecked;
	}

	/**
	 * a method for checking if an element is displayed
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @param print
	 *            : whether or not to printout the action
	 * @return boolean: whether the element is displayed or not
	 * @throws IOException
	 */
	public boolean isElementDisplayed(Locators type, String locator, boolean print) throws IOException {
		boolean isDisplayed = false;
		try {
			isDisplayed = getWebElement(type, locator).isDisplayed();
		} catch (NoSuchElementException e) {
			log.error(e);
		}
		if (print) {
			file.recordExpected(CHECKING + type + " " + locator + DISPLAYED);
		}
		return isDisplayed;
	}

	// //////////////////////////////////
	// extra base selenium functionality
	// //////////////////////////////////

	/**
	 * get the number of options from the select drop down
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @return Integer: the number of select options
	 * @throws IOException
	 */
	public int getNumOfSelectOptions(Locators type, String locator) throws IOException {
		// wait for element to be present
		if (!isElementPresent(type, locator, false)) {
			waitForElementPresent(type, locator, 5);
		}
		if (!isElementPresent(type, locator, false)) {
			return 0;
		}
		WebElement element = getWebElement(type, locator);
		List<WebElement> allOptions = element.findElements(By.tagName("option"));
		return allOptions.size();
	}

	/**
	 * get the options from the select drop down
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @return String[]: the options from the select element
	 * @throws IOException
	 */
	public String[] getSelectOptions(Locators type, String locator) throws IOException {
		// wait for element to be present
		if (!isElementPresent(type, locator, false)) {
			waitForElementPresent(type, locator, 5);
		}
		if (!isElementPresent(type, locator, false)) {
			return new String[0];
		}
		WebElement element = getWebElement(type, locator);
		List<WebElement> allOptions = element.findElements(By.tagName("option"));
		String[] options = new String[allOptions.size()];
		for (int i = 0; i < allOptions.size(); i++) {
			options[i] = allOptions.get(i).getAttribute(VALUE);
		}
		return options;
	}

	// ///////////////////////////////////
	// selenium retreval functions
	// ///////////////////////////////////

	/**
	 * get the rows of a table
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @return List: a list of the table rows as WebElements
	 * @throws IOException
	 */
	public List<WebElement> getTableRows(Locators type, String locator) throws IOException {
		// wait for element to be present
		if (!isElementPresent(type, locator, false)) {
			waitForElementPresent(type, locator, 5);
		}
		if (!isElementPresent(type, locator, false)) {
			return new ArrayList<>();
		}
		WebElement element = getWebElement(type, locator);
		// this locator may need to be updated
		return element.findElements(By.tagName("tr"));
	}

	/**
	 * get the number of rows of a table
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @return Integer: the number of table rows
	 * @throws IOException
	 */
	public int getNumOfTableRows(Locators type, String locator) throws IOException {
		List<WebElement> rows = getTableRows(type, locator);
		return rows.size();
	}

	/**
	 * get the columns of a table
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @return List: a list of the table columns as WebElements
	 * @throws IOException
	 */
	public List<WebElement> getTableColumns(Locators type, String locator) throws IOException {
		// wait for element to be present
		if (!isElementPresent(type, locator, false)) {
			waitForElementPresent(type, locator, 5);
		}
		if (!isElementPresent(type, locator, false)) {
			return new ArrayList<>();
		}
		WebElement element = getWebElement(type, locator);
		return element.findElements(By.xpath(".//tr[1]/*"));
	}

	/**
	 * a method to retrieve the row number in a table that has a header (th) of
	 * the indicated value
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @param header
	 *            : the full text value expected in a th cell
	 * @return Integer: the row number containing the header
	 * @throws IOException
	 */
	public int getTableRowWHeader(Locators type, String locator, String header) throws IOException {
		// wait for element to be present
		if (!isElementPresent(type, locator, false)) {
			waitForElementPresent(type, locator, 5);
		}
		if (!isElementPresent(type, locator, false)) {
			return 0; // indicates table not found
		}
		List<WebElement> tables = getWebElements(type, locator);
		for (WebElement table : tables) {
			// might want to redo this logical check
			List<WebElement> rows = table.findElements(By.tagName("tr"));
			int counter = 1;
			for (WebElement row : rows) {
				// might want to redo this logical check
				if (row.findElement(By.xpath(".//td[1]|.//th[1]")).getText().equals(header)) {
					return counter;
				}
				counter++;
			}
		}
		return 0; // indicates header not found
	}

	/**
	 * get a specific column from a table
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @param colNum
	 *            : the column number of the table to obtain - note, column
	 *            numbering starts at 1, NOT 0
	 * @return List: a list of the table cells in the columns as WebElements
	 * @throws IOException
	 */
	public List<WebElement> getTableColumn(Locators type, String locator, int colNum) throws IOException {
		// wait for element to be present
		if (!isElementPresent(type, locator, false)) {
			waitForElementPresent(type, locator, 5);
		}
		if (!isElementPresent(type, locator, false)) {
			return new ArrayList<>(); // indicates table not found
		}
		List<WebElement> tables = getWebElements(type, locator);
		List<WebElement> column = tables.get(0).findElements(By.className("NONEEXISTS")); // cludge
		// to
		// initialize
		for (WebElement table : tables) {
			// this locator may need to be updated
			List<WebElement> cells = table.findElements(By.xpath(".//th[" + colNum + "]|.//td[" + colNum + "]"));
			column.addAll(cells);
		}
		return column;
	}

	/**
	 * get the contents of a specific cell
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @param row
	 *            : the number of the row in the table - note, row numbering
	 *            starts at 1, NOT 0
	 * @param col
	 *            : the number of the column in the table - note, column
	 *            numbering starts at 1, NOT 0
	 * @return WebElement: the cell element object, and all associated values
	 *         with it
	 * @throws IOException
	 */
	public WebElement getTableCell(Locators type, String locator, int row, int col) throws IOException {
		// wait for element to be present
		if (!isElementPresent(type, locator, false)) {
			waitForElementPresent(type, locator, 5);
		}
		if (!isElementPresent(type, locator, false)) {
			return null; // indicates table not found
		}
		List<WebElement> tables = getWebElements(type, locator);
		for (WebElement table : tables) {
			// this locator may need to be updated
			return table.findElement(By.xpath(".//tr[" + row + "]/td[" + col + "]"));
		}
		return null; // indicates cell not present
	}

	// //////////////////////////////////
	// extra base selenium functionality
	// //////////////////////////////////

	/**
	 * determine if something is selected from a drop down menu
	 * 
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @return Boolean: was something selected in the drop down
	 * @throws IOException
	 */
	public boolean isSomethingSelected(Locators type, String locator) throws IOException {
		WebElement element = getWebElement(type, locator);
		if ("input".equalsIgnoreCase(element.getTagName()) && element.isSelected()) {
			return true;
		}
		return "select".equalsIgnoreCase(element.getTagName()) && getSelectedValues(type, locator).length > 0;
	}

	/**
	 * get the option from the select drop down
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @return String: the option from the select element
	 * @throws IOException
	 */
	public String getSelectedText(Locators type, String locator) throws IOException {
		// wait for element to be present
		if (!isElementPresent(type, locator, false)) {
			waitForElementPresent(type, locator, 5);
		}
		if (!isElementPresent(type, locator, false)) {
			return "";
		}
		WebElement element = getWebElement(type, locator);
		Select dropdown = new Select(element);
		WebElement option = dropdown.getFirstSelectedOption();
		return option.getText();
	}

	/**
	 * get the options from the select drop down
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @return String[]: the options from the select element
	 * @throws IOException
	 */
	public String[] getSelectedTexts(Locators type, String locator) throws IOException {
		// wait for element to be present
		if (!isElementPresent(type, locator, false)) {
			waitForElementPresent(type, locator, 5);
		}
		if (!isElementPresent(type, locator, false)) {
			return new String[0];
		}
		WebElement element = getWebElement(type, locator);
		Select dropdown = new Select(element);
		List<WebElement> options = dropdown.getAllSelectedOptions();
		String[] stringOptions = new String[options.size()];
		for (int i = 0; i < options.size(); i++) {
			stringOptions[i] = options.get(i).getText();
		}
		return stringOptions;
	}

	/**
	 * get the option value from the select drop down
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @return String: the options from the select element
	 * @throws IOException
	 */
	public String getSelectedValue(Locators type, String locator) throws IOException {
		// wait for element to be present
		if (!isElementPresent(type, locator, false)) {
			waitForElementPresent(type, locator, 5);
		}
		if (!isElementPresent(type, locator, false)) {
			return "";
		}
		WebElement element = getWebElement(type, locator);
		Select dropdown = new Select(element);
		WebElement option = dropdown.getFirstSelectedOption();
		return option.getAttribute(VALUE);
	}

	/**
	 * get the option values from the select drop down
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @return String[]: the options from the select element
	 * @throws IOException
	 */
	public String[] getSelectedValues(Locators type, String locator) throws IOException {
		// wait for element to be present
		if (!isElementPresent(type, locator, false)) {
			waitForElementPresent(type, locator, 5);
		}
		if (!isElementPresent(type, locator, false)) {
			return new String[0];
		}
		WebElement element = getWebElement(type, locator);
		Select dropdown = new Select(element);
		List<WebElement> options = dropdown.getAllSelectedOptions();
		String[] stringOptions = new String[options.size()];
		for (int i = 0; i < options.size(); i++) {
			stringOptions[i] = options.get(i).getAttribute(VALUE);
		}
		return stringOptions;
	}

	/**
	 * our generic selenium get text from an element functionality implemented
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @return String - the text of the element
	 * @throws InvalidLocatorTypeException
	 */
	public String getText(Locators type, String locator) throws InvalidLocatorTypeException {
		WebElement element = getWebElement(type, locator);
		return element.getText();
	}

	/**
	 * our generic selenium get value from an element functionality implemented
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @return String - the text of the element
	 * @throws InvalidLocatorTypeException
	 */
	public String getValue(Locators type, String locator) throws InvalidLocatorTypeException {
		WebElement element = getWebElement(type, locator);
		return element.getAttribute(VALUE);
	}

	/**
	 * a function to return one css attribute of the provided element
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @param attribute
	 *            - the css attribute to be returned
	 * @return String - the value of the css attribute
	 * @throws InvalidLocatorTypeException
	 */
	public String getCss(Locators type, String locator, String attribute) throws InvalidLocatorTypeException {
		WebElement element = getWebElement(type, locator);
		return element.getCssValue(attribute);
	}

	/**
	 * a function to return one attribute of the provided element
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @param attribute
	 *            - the css attribute to be returned
	 * @return String - the value of the css attribute
	 * @throws InvalidLocatorTypeException
	 */
	public String getAttribute(Locators type, String locator, String attribute) throws InvalidLocatorTypeException {
		WebElement element = getWebElement(type, locator);
		return element.getAttribute(attribute);
	}

	/**
	 * a function to return all attributes of the provided element
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @return String - the value of the css attribute
	 * @throws InvalidLocatorTypeException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> getAllAttributes(Locators type, String locator) throws InvalidLocatorTypeException {
		WebElement element = getWebElement(type, locator);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		return (Map<String, String>) js.executeScript(
				"var items = {}; for (index = 0; index < arguments[0].attributes.length; ++index) { items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value }; return items;",
				element);
	}

	/**
	 * a way to execute custom javascript functions
	 *
	 * @param javascriptFunction
	 * @throws InvalidLocatorTypeException
	 */
	public void getEval(Locators type, String locator, String javascriptFunction) throws InvalidLocatorTypeException {
		WebElement element = getWebElement(type, locator);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(javascriptFunction, element);
	}

	/**
	 * our generic selenium click functionality implemented
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @return Integer - the number of errors encountered while executing these
	 *         steps
	 * @throws IOException
	 */
	public int click(Locators type, String locator) throws IOException {
		String cantClick = "Unable to click ";
		String action = "Clicking " + type + " " + locator;
		String expected = type + " " + locator + " is present, displayed, and enabled to be clicked";
		// wait for element to be present
		if (!isElementPresent(type, locator, false)) {
			waitForElementPresent(type, locator, 5);
		}
		if (!isElementPresent(type, locator, false)) {
			file.recordAction(action, expected, cantClick + type + " " + locator + NOTPRESENT, Result.FAILURE);
			file.addError();
			return 1; // indicates element not present
		}
		// wait for element to be displayed
		if (!isElementDisplayed(type, locator, false)) {
			waitForElementDisplayed(type, locator, 5);
		}
		if (!isElementDisplayed(type, locator, false)) {
			file.recordAction(action, expected, cantClick + type + " " + locator + NOTDISPLAYED, Result.FAILURE);
			file.addError();
			return 1; // indicates element not displayed
		}
		// wait for element to be enabled
		if (!isElementEnabled(type, locator, false)) {
			waitForElementEnabled(type, locator, 5);
		}
		if (!isElementEnabled(type, locator, false)) {
			file.recordAction(action, expected, cantClick + type + " " + locator + NOTENABLED, Result.FAILURE);
			file.addError();
			return 1; // indicates element not enabled
		}
		WebElement element = getWebElement(type, locator);
		Actions selAction = new Actions(driver);
		selAction.click(element).perform();
		file.recordAction(action, expected, "Clicked " + type + " " + locator, Result.SUCCESS);
		return 0;
	}

	/**
	 * our generic selenium submit functionality implemented
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @return Integer - the number of errors encountered while executing these
	 *         steps
	 * @throws IOException
	 */
	public int submit(Locators type, String locator) throws IOException {
		String cantSubmit = "Unable to submit ";
		String action = "Submitting " + type + " " + locator;
		String expected = type + " " + locator + " is present, displayed, and enabled to be submitted	";
		// wait for element to be present
		if (!isElementPresent(type, locator, false)) {
			waitForElementPresent(type, locator, 5);
		}
		if (!isElementPresent(type, locator, false)) {
			file.recordAction(action, expected, cantSubmit + type + " " + locator + NOTPRESENT, Result.FAILURE);
			file.addError();
			return 1; // indicates element not present
		}
		// wait for element to be displayed
		if (!isElementDisplayed(type, locator, false)) {
			waitForElementDisplayed(type, locator, 5);
		}
		if (!isElementDisplayed(type, locator, false)) {
			file.recordAction(action, expected, cantSubmit + type + " " + locator + NOTDISPLAYED, Result.FAILURE);
			file.addError();
			return 1; // indicates element not displayed
		}
		// wait for element to be enabled
		if (!isElementEnabled(type, locator, false)) {
			waitForElementEnabled(type, locator, 5);
		}
		if (!isElementEnabled(type, locator, false)) {
			file.recordAction(action, expected, cantSubmit + type + " " + locator + NOTENABLED, Result.FAILURE);
			file.addError();
			return 1; // indicates element not enabled
		}
		WebElement element = getWebElement(type, locator);
		element.submit();
		file.recordAction(action, expected, "Submitted " + type + " " + locator, Result.SUCCESS);
		return 0;
	}

	// ///////////////////////////////////
	// selenium actions functionality
	// ///////////////////////////////////

	/**
	 * a method to simulate the mouse hovering over an element
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @return Integer - the number of errors encountered while executing these
	 *         steps
	 * @throws IOException
	 */
	public int hover(Locators type, String locator) throws IOException {
		String action = "Hovering over " + type + " " + locator;
		String expected = type + " " + locator + " is present, and displayed to be hovered over";
		// wait for element to be present
		if (!isElementPresent(type, locator, false)) {
			waitForElementPresent(type, locator, 5);
		}
		if (!isElementPresent(type, locator, false)) {
			file.recordAction(action, expected, "Unable to hover over " + type + " " + locator + NOTPRESENT,
					Result.FAILURE);
			file.addError();
			return 1; // indicates element not present
		}
		// wait for element to be displayed
		if (!isElementDisplayed(type, locator, false)) {
			waitForElementDisplayed(type, locator, 5);
		}
		if (!isElementDisplayed(type, locator, false)) {
			file.recordAction(action, expected, "Unable to hover over " + type + " " + locator + NOTDISPLAYED,
					Result.FAILURE);
			file.addError();
			return 1; // indicates element not displayed
		}
		Actions selAction = new Actions(driver);
		WebElement element = getWebElement(type, locator);
		selAction.moveToElement(element).perform();
		file.recordAction(action, expected, "Hovered over " + type + " " + locator, Result.SUCCESS);
		return 0;
	}

	/**
	 * a custom selenium functionality to apply a blur to an element
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @return Integer - the number of errors encountered while executing these
	 *         steps
	 * @throws IOException
	 */
	public int blur(Locators type, String locator) throws IOException {
		String cantFocus = "Unable to focus on ";
		String action = "Focusing, then unfocusing (blurring) on " + type + " " + locator;
		String expected = type + " " + locator + " is present, displayed, and enabled to be blurred";
		// wait for element to be present
		if (!isElementPresent(type, locator, false)) {
			waitForElementPresent(type, locator, 5);
		}
		if (!isElementPresent(type, locator, false)) {
			file.recordAction(action, expected, cantFocus + type + " " + locator + NOTPRESENT, Result.FAILURE);
			file.addError();
			return 1; // indicates element not present
		}
		// wait for element to be displayed
		if (!isElementDisplayed(type, locator, false)) {
			waitForElementDisplayed(type, locator, 5);
		}
		if (!isElementDisplayed(type, locator, false)) {
			file.recordAction(action, expected, cantFocus + type + " " + locator + NOTDISPLAYED, Result.FAILURE);
			file.addError();
			return 1; // indicates element not displayed
		}
		// wait for element to be enabled
		if (!isElementEnabled(type, locator, false)) {
			waitForElementEnabled(type, locator, 5);
		}
		if (!isElementEnabled(type, locator, false)) {
			file.recordAction(action, expected, cantFocus + type + " " + locator + NOTENABLED, Result.FAILURE);
			file.addError();
			return 1; // indicates element not enabled
		}
		WebElement element = getWebElement(type, locator);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].focus(); arguments[0].blur(); return true", element);
		file.recordAction(action, expected, "Focused, then unfocused (blurred) on " + type + " " + locator,
				Result.SUCCESS);
		return 0;
	}

	/**
	 * generically wait for a field to enter text into
	 * 
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @param action
	 *            - the action being performed
	 * @param expected
	 *            - the expected outcome of this task
	 * @return Integer - the number of errors encountered while executing these
	 *         steps
	 * @throws IOException
	 */
	private int waitForTextField(Locators type, String locator, String action, String expected) throws IOException {
		// wait for element to be present
		if (!isElementPresent(type, locator, false)) {
			waitForElementPresent(type, locator, 5);
		}
		if (!isElementPresent(type, locator, false)) {
			file.recordAction(action, expected, CANTTYPE + type + " " + locator + NOTPRESENT, Result.FAILURE);
			file.addError();
			return 1; // indicates element not present
		}
		// wait for element to be displayed
		if (!isElementDisplayed(type, locator, false)) {
			waitForElementDisplayed(type, locator, 5);
		}
		if (!isElementDisplayed(type, locator, false)) {
			file.recordAction(action, expected, CANTTYPE + type + " " + locator + NOTDISPLAYED, Result.FAILURE);
			file.addError();
			return 1; // indicates element not displayed
		}
		// wait for element to be enabled
		if (!isElementEnabled(type, locator, false)) {
			waitForElementEnabled(type, locator, 5);
		}
		if (!isElementEnabled(type, locator, false)) {
			file.recordAction(action, expected, CANTTYPE + type + " " + locator + NOTENABLED, Result.FAILURE);
			file.addError();
			return 1; // indicates element not enabled
		}
		return 0;
	}

	/**
	 * our generic selenium type functionality implemented
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @param text
	 *            : the text to be typed in
	 * @return Integer - the number of errors encountered while executing these
	 *         steps
	 * @throws IOException
	 */
	public int type(Locators type, String locator, String text) throws IOException {
		String action = "Typing text '" + text + IN + type + " " + locator;
		String expected = type + " " + locator + " is present, displayed, and enabled to have text " + text
				+ " typed in";
		if (waitForTextField(type, locator, action, expected) != 0) {
			return 1;
		}
		WebElement element = getWebElement(type, locator);
		element.sendKeys(text);
		file.recordAction(action, expected, "Typed text '" + text + IN + type + " " + locator, Result.SUCCESS);
		return 0;
	}

	/**
	 * our generic selenium type functionality implemented for specific keys
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @param key
	 *            : the key to be pressed
	 * @return Integer - the number of errors encountered while executing these
	 *         steps
	 * @throws IOException
	 */
	public int type(Locators type, String locator, Keys key) throws IOException {
		String action = "Typing text '" + key + IN + type + " " + locator;
		String expected = type + " " + locator + " is present, displayed, and enabled to have text " + key
				+ " typed in";
		if (waitForTextField(type, locator, action, expected) != 0) {
			return 1;
		}
		WebElement element = getWebElement(type, locator);
		element.sendKeys(key);
		file.recordAction(action, expected, "Typed text '" + key + IN + type + " " + locator, Result.SUCCESS);
		return 0;
	}

	/**
	 * our generic selenium clear functionality implemented
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @return Integer - the number of errors encountered while executing these
	 *         steps
	 * @throws IOException
	 */
	public int clear(Locators type, String locator) throws IOException {
		String cantClear = "Unable to clear ";
		String action = "Clearing text in " + type + " " + locator;
		String expected = type + " " + locator + " is present, displayed, and enabled to have text cleared";
		// wait for element to be present
		if (!isElementPresent(type, locator, false)) {
			waitForElementPresent(type, locator, 5);
		}
		if (!isElementPresent(type, locator, false)) {
			file.recordAction(action, expected, cantClear + type + " " + locator + NOTPRESENT, Result.FAILURE);
			file.addError();
			return 1; // indicates element not present
		}
		// wait for element to be displayed
		if (!isElementDisplayed(type, locator, false)) {
			waitForElementDisplayed(type, locator, 5);
		}
		if (!isElementDisplayed(type, locator, false)) {
			file.recordAction(action, expected, cantClear + type + " " + locator + NOTDISPLAYED, Result.FAILURE);
			file.addError();
			return 1; // indicates element not displayed
		}
		// wait for element to be enabled
		if (!isElementEnabled(type, locator, false)) {
			waitForElementEnabled(type, locator, 5);
		}
		if (!isElementEnabled(type, locator, false)) {
			file.recordAction(action, expected, cantClear + type + " " + locator + NOTENABLED, Result.FAILURE);
			file.addError();
			return 1; // indicates element not enabled
		}
		WebElement element = getWebElement(type, locator);
		element.clear();
		file.recordAction(action, expected, "Cleared text in " + type + " " + locator, Result.SUCCESS);
		return 0;
	}

	/**
	 * our generic select selenium functionality
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @param value
	 *            : the select option to be selected
	 * @return Integer - the number of errors encountered while executing these
	 *         steps
	 * @throws IOException
	 */
	public int select(Locators type, String locator, String value) throws IOException {
		String cantSelect = "Unable to select ";
		String action = "Selecting " + value + " in " + type + " " + locator;
		String expected = type + " " + locator + " is present, displayed, and enabled to have the value " + value
				+ " selected";
		// wait for element to be present
		if (!isElementPresent(type, locator, false)) {
			waitForElementPresent(type, locator, 5);
		}
		if (!isElementPresent(type, locator, false)) {
			file.recordAction(action, expected, cantSelect + type + " " + locator + NOTPRESENT, Result.FAILURE);
			file.addError();
			return 1; // indicates element not present
		}
		// wait for element to be displayed
		if (!isElementDisplayed(type, locator, false)) {
			waitForElementDisplayed(type, locator, 5);
		}
		if (!isElementDisplayed(type, locator, false)) {
			file.recordAction(action, expected, cantSelect + type + " " + locator + NOTDISPLAYED, Result.FAILURE);
			file.addError();
			return 1; // indicates element not displayed
		}
		// wait for element to be enabled
		if (!isElementEnabled(type, locator, false)) {
			waitForElementEnabled(type, locator, 5);
		}
		if (!isElementEnabled(type, locator, false)) {
			file.recordAction(action, expected, cantSelect + type + " " + locator + NOTENABLED, Result.FAILURE);
			file.addError();
			return 1; // indicates element not enabled
		}
		// ensure the option exists
		if (!Arrays.asList(getSelectOptions(type, locator)).contains(value)) {
			file.recordAction(action, expected,
					cantSelect + value + " in " + type + " " + locator
							+ " as that option isn't present. Available options are:<i><br/>" + "&nbsp;&nbsp;&nbsp;"
							+ String.join("<br/>&nbsp;&nbsp;&nbsp;", getSelectOptions(type, locator)) + "</i>",
					Result.FAILURE);
			file.addError();
			return 1;
		}
		// do the select
		WebElement element = getWebElement(type, locator);
		Select dropdown = new Select(element);
		dropdown.selectByValue(value);
		file.recordAction(action, expected, "Selected " + value + " in " + type + " " + locator, Result.SUCCESS);
		return 0;
	}

	/**
	 * An extension of the basic Selenium action of 'moveToElement' This will
	 * scroll or move the page to ensure the element is visible
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @return Integer - the number of errors encountered while executing these
	 *         steps
	 * @throws IOException
	 */
	public int move(Locators type, String locator) throws IOException {
		String action = "Moving screen to " + type + " " + locator;
		String expected = type + " " + locator + " is now present on the visible page";
		// wait for element to be present
		if (!isElementPresent(type, locator, false)) {
			waitForElementPresent(type, locator, 5);
		}
		if (!isElementPresent(type, locator, false)) {
			file.recordAction(action, expected, "Unable to move to " + type + " " + locator + NOTPRESENT,
					Result.FAILURE);
			file.addError();
			return 1; // indicates element not present
		}
		WebElement element = getWebElement(type, locator);
		Actions builder = new Actions(driver);
		builder.moveToElement(element);

		if (!isElementDisplayed(type, locator, false)) {
			file.recordAction(action, expected, type + " " + locator + " is not present on visible page",
					Result.FAILURE);
			file.addError();
			return 1; // indicates element not visible
		}
		file.recordAction(action, expected, type + " " + locator + " is present on visible page", Result.SUCCESS);
		return 0; // indicates element successfully moved to
	}

	//////////////////////////////////////////////////////
	// obtaining element values
	//////////////////////////////////////////////////////

	/**
	 * An extension of the basic Selenium action of 'moveToElement' This will
	 * scroll or move the page to ensure the element is visible
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @param position
	 *            - how many pixels above the element to scroll to
	 * @return Integer - the number of errors encountered while executing these
	 *         steps
	 * @throws IOException
	 */
	public int move(Locators type, String locator, int position) throws IOException {
		String action = "Moving screen to " + position + " pixels above " + type + " " + locator;
		String expected = type + " " + locator + " is now present on the visible page";
		// wait for element to be present
		if (!isElementPresent(type, locator, false)) {
			waitForElementPresent(type, locator, 5);
		}
		if (!isElementPresent(type, locator, false)) {
			file.recordAction(action, expected, "Unable to move to " + type + " " + locator + NOTPRESENT,
					Result.FAILURE);
			file.addError();
			return 1; // indicates element not present
		}

		JavascriptExecutor jse = (JavascriptExecutor) driver;
		WebElement element = getWebElement(type, locator);
		int elementPosition = element.getLocation().getY();
		int newPosition = elementPosition - position;
		jse.executeScript("window.scrollBy(0, " + newPosition + ")");

		if (!isElementDisplayed(type, locator, false)) {
			file.recordAction(action, expected, type + " " + locator + " is not present on visible page",
					Result.FAILURE);
			file.addError();
			return 1; // indicates element not visible
		}
		file.recordAction(action, expected, type + " " + locator + " is present on visible page", Result.SUCCESS);
		return 0; // indicates element successfully moved to
	}

	/**
	 * a method to determine selenium's By object using selenium webdriver
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @return By: the selenium object
	 * @throws InvalidLocatorTypeException
	 */
	private By defineByElement(Locators type, String locator) throws InvalidLocatorTypeException {
		// consider adding strengthening
		By byElement;
		switch (type) { // determine which locator type we are interested in
		case XPATH:
			byElement = By.xpath(locator);
			break;
		case ID:
			byElement = By.id(locator);
			break;
		case NAME:
			byElement = By.name(locator);
			break;
		case CLASSNAME:
			byElement = By.className(locator);
			break;
		case LINKTEXT:
			byElement = By.linkText(locator);
			break;
		case PARTIALLINKTEXT:
			byElement = By.partialLinkText(locator);
			break;
		case TAGNAME:
			byElement = By.tagName(locator);
			break;
		default:
			throw new InvalidLocatorTypeException(type + " is not a valid locator type");
		}
		return byElement;
	}

	/**
	 * a method to grab the first matching web element using selenium webdriver
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @return WebElement: the element object, and all associated values with it
	 * @throws InvalidLocatorTypeException
	 */
	public WebElement getWebElement(Locators type, String locator) throws InvalidLocatorTypeException {
		By byElement = defineByElement(type, locator);
		return driver.findElement(byElement);
	}

	// //////////////////////////////////
	// extra base selenium functionality
	// //////////////////////////////////

	/**
	 * a method to grab all matching web elements using selenium webdriver
	 *
	 * @param type
	 *            - the locator type e.g. Locators.id, Locators.xpath
	 * @param locator
	 *            - the locator string e.g. login, //input[@id='login']
	 * @return List<WebElement>: a list of element objects, and all associated
	 *         values with them
	 * @throws InvalidLocatorTypeException
	 */
	private List<WebElement> getWebElements(Locators type, String locator) throws InvalidLocatorTypeException {
		By byElement = defineByElement(type, locator);
		return driver.findElements(byElement);
	}
}