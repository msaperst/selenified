package integration;

import com.coveros.selenified.Browser.BrowserUse;
import com.coveros.selenified.application.App;
import com.coveros.selenified.utilities.Property;
import org.apache.commons.io.FileUtils;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

public class NoLoadIT extends WebBase {

    @BeforeMethod(alwaysRun = true)
    protected void startTest(Object[] dataProvider, Method method, ITestContext test, ITestResult result) throws IOException {
        super.startTest(dataProvider, method, test, result, BrowserUse.OPEN);
    }

    @SuppressWarnings("deprecation")
    @Test(groups = {"integration"},
            description = "An integration test to verify we can start a test with a browser, but won't load any app")
    public void verifyNoLoad(ITestContext context) throws IOException {
        // use this object to manipulate the app
        App app = this.apps.get();
        // verify a selenium actions class was setup
        assertNotNull(app);
        String directory = context.getOutputDirectory();
        String file = app.getReporter().getFileName();
        assertFalse(FileUtils.readFileToString(new File(directory, file + ".html"))
                .contains("Opening new browser and loading up starting app"));
        // verify the app wasn't attempted to load
        app.verify().urlEquals(Property.getAppURL(this.getClass().getName(), context));
        // verify one issue from the above check
        finish(1);
    }
}