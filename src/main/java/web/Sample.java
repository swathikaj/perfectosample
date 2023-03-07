package web;

import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.client.ReportiumClientFactory;
import com.perfecto.reportium.model.Job;
import com.perfecto.reportium.model.PerfectoExecutionContext;
import com.perfecto.reportium.model.Project;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResultFactory;

import com.github.tomaslanger.chalk.Ansi;
import com.github.tomaslanger.cli.progress.ProgressBar;
import com.github.tomaslanger.cli.progress.StatusLoc;

public class Sample {

	public static void main(String[] args) throws Exception {

		int exitCode = 0;
		ProgressBar progressBar = createProgressBar();
		DesiredCapabilities capabilities = new DesiredCapabilities("", "", Platform.ANY);

		// 1. Replace <<cloud name>> with your perfecto cloud name (e.g. demo is the
		// cloudName of demo.perfectomobile.com).
		String cloudName = "trial";

		// 2. Replace <<security token>> with your perfecto security token.
		String securityToken = "eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI2ZDM2NmJiNS01NDAyLTQ4MmMtYTVhOC1kODZhODk4MDYyZjIifQ.eyJpYXQiOjE2Nzc2NzA2MDEsImp0aSI6IjBjYmFiMzU0LWY1MmUtNDFmNC04NzEwLTY5MmQ0Zjc0MmZhMyIsImlzcyI6Imh0dHBzOi8vYXV0aDMucGVyZmVjdG9tb2JpbGUuY29tL2F1dGgvcmVhbG1zL3RyaWFsLXBlcmZlY3RvbW9iaWxlLWNvbSIsImF1ZCI6Imh0dHBzOi8vYXV0aDMucGVyZmVjdG9tb2JpbGUuY29tL2F1dGgvcmVhbG1zL3RyaWFsLXBlcmZlY3RvbW9iaWxlLWNvbSIsInN1YiI6ImYwZTFiMDQ3LTg5YmYtNDEwZi1hNjM4LWU0ODNlMmIyNzk5NyIsInR5cCI6Ik9mZmxpbmUiLCJhenAiOiJvZmZsaW5lLXRva2VuLWdlbmVyYXRvciIsIm5vbmNlIjoiYzMzY2JhNDMtNjQwNS00ZDRiLWFjYmQtNjg2OGExYThlNzE2Iiwic2Vzc2lvbl9zdGF0ZSI6IjMzZTU3OTY4LTA0NGItNDliNi05ZDA0LWE3ZGE4ZDA1ZDQ3NSIsInNjb3BlIjoib3BlbmlkIG9mZmxpbmVfYWNjZXNzIHByb2ZpbGUgZW1haWwifQ.nHegoi70DwqisbynRQFDVojONxL7ePkgcgLv8CiLakk";
		capabilities.setCapability("securityToken", securityToken);

		// 3. Set web capabilities.
		capabilities.setCapability("platformName", "Windows");
		capabilities.setCapability("platformVersion", "11");
		capabilities.setCapability("browserName", "Chrome");
		capabilities.setCapability("browserVersion", "110");
		capabilities.setCapability("location", "US East");
		capabilities.setCapability("resolution", "1024x768");

		// Set other capabilities.
		capabilities.setCapability("takesScreenshot", true);
		capabilities.setCapability("screenshotOnError", true);

		// Initialize the  driver
		progressBar.setProgress(1);
		Logger.getLogger("org.openqa.selenium.remote").setLevel(Level.OFF);
		RemoteWebDriver driver = new RemoteWebDriver(
				new URL("https://" + cloudName.replace(".perfectomobile.com", "")
				+ ".perfectomobile.com/nexperience/perfectomobile/wd/hub"),
				capabilities);

		// Set page load timeout
		driver.manage().timeouts().pageLoadTimeout(60 , TimeUnit.SECONDS);

		PerfectoExecutionContext perfectoExecutionContext;
		if (System.getenv("jobName") != null) {
			perfectoExecutionContext = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
					.withProject(new Project("My Project", "1.0"))
					.withJob(new Job(System.getenv("jobName"),
							Integer.parseInt(System.getenv("jobNumber"))))
					.withWebDriver(driver).build();
		} else {
			perfectoExecutionContext = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
					.withProject(new Project("My Project", "1.0"))
					.withWebDriver(driver).build();
		}
		ReportiumClient reportiumClient = new ReportiumClientFactory()
				.createPerfectoReportiumClient(perfectoExecutionContext);

		reportiumClient.testStart("Selenium Java Web Sample", new TestContext("selenium", "web"));

		try {
			WebDriverWait wait = new WebDriverWait(driver, 30);
			reportiumClient.stepStart("Navigate to URL");
			driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
			reportiumClient.stepEnd();

			/**
			 *****************************
			 *** Your test starts here. If you test a different url, you need to modify the test steps accordingly. ***
			 *****************************
			 */


			/**
			 *****************************
			 *** Your test ends here. ***
			 *****************************
			 */

			progressBar.setProgress(8);
			reportiumClient.testStop(TestResultFactory.createSuccess());
		} catch (Exception e) {
			progressBar.setProgress(8);
			reportiumClient.testStop(TestResultFactory.createFailure(e));
			exitCode = 1;
		}

		// Obtains the Report URL
		String reportURL = reportiumClient.getReportUrl() + "&onboardingJourney=automated&onboardingDevice=desktopWeb";

		// Quits the driver
		progressBar.setProgress(9);
		driver.quit();

		// Prints the Report URL
		progressBar.setProgress(10);
		System.out.println("\n\nOpen this link to continue with the guide: " + reportURL + "\n");

		// Launch browser with the Report URL
		try {
			Desktop.getDesktop().browse(new URI(reportURL));
		} catch (Exception e) {
			System.out.println("Unable to open Reporting URL in browser: " + e.getMessage());
		}

		System.exit(exitCode);
	}

	private static ProgressBar createProgressBar() {
		int TOTAL_STEPS = 10;
		int PROGRESS_BAR_CHAR_COUNT = 50;
		ProgressBar.Builder builder = new ProgressBar.Builder();
		builder.setMax(TOTAL_STEPS)
				.setCharCount(PROGRESS_BAR_CHAR_COUNT)
				.setBaseChar(' ')
				.setProgressChar('=')
				.setStatusLocation(StatusLoc.FIRST_LINE)
				.setKeepSingleColor(true)
				.setBeginString("[")
				.setEndString("]")
				.setFgColor(Ansi.Color.WHITE)
				.setBgColor(Ansi.BgColor.BLACK)
				.claimNoOuts();
		return builder.build();
	}
}
