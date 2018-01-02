package factorypattern;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class FlightsHomePage{
	
	private String START_URL = "http://www.volotea.com/en";
	public int adultPassengersCount = 0;
	public int childPassengersCount = 0;
	public int timeInSeconds = 30;	
	public WebDriver driver;	

	@BeforeClass(description = "Start browser")
	public void startBrowser() {
		driver=DriverFactory.getBrowserInstance("Chrome");
		System.out.println("BROWSER****************************************");
		driver.get(START_URL);
		driver.manage().window().maximize();
		waitForPageToLoad();
		waitForElementToPresent(By.xpath("//div[@class='search-flights-wrapper']"));
	}

	@Test
	public void verifyLandingPage() throws Exception {
		String textName = driver.findElement(By.xpath("//a[@class='button brand md block']")).getText();
		driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);
		assertEquals(textName, "FIND FLIGHTS");
		String tagName = driver.findElement(By.xpath("//li[@class='flat_tabs__tab flat_tabs__tab--flights']//a"))
				.getAttribute("class");
		if (tagName.contains("is-active")) {
			assertTrue(true);
		}
	}

	@Test
	@Parameters({ "originCountry", "originState", "destinationCountry", "destinationState" })
	public void selectOriginAndDestination(String originCountry, String originState, String destinationCountry,
			String destinationState) throws Exception {
		driver.findElement(By.xpath("//fieldset[@class='origin']")).click();
		assertTrue(true);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.findElement(By.xpath("//h2[@class='title'][contains(text(),'" + originCountry
				+ "')]//following::a[contains(text(),'" + originState + "')]")).click();
		Thread.sleep(5000);
		driver.findElement(By.xpath("//fieldset[@class='destination']")).click();
		assertTrue(true);
		driver.findElement(By.xpath("//h2[@class='title'][contains(text(),'" + destinationCountry
				+ "')]//following::a[contains(text(),'" + destinationState + "')]")).click();
		driver.findElement(By.xpath("//fieldset[@datepicker-type='origin']")).getText();
		boolean nextbutton = driver.findElement(By.xpath("//a[@data-handler='next']")).isEnabled();
		assertEquals(nextbutton, true);
	}

	@Test
	@Parameters({ "travelStartDate", "travelReturnDate" })
	public void selectDate(String travelStartDate, String travelReturnDate) throws Exception {
		driver.manage().timeouts().implicitlyWait(80, TimeUnit.SECONDS);
		driver.findElement(By
				.xpath("//div[@class='ui-datepicker-group ui-datepicker-group-first']//following::a[@class='ui-state-default'][contains(text(),'"
						+ travelStartDate + "')]"))
				.click();

		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		driver.findElement(By
				.xpath("//div[@class='ui-datepicker-group ui-datepicker-group-first']//following::a[@class='ui-state-default'][contains(text(),'"
						+ travelReturnDate + "')]"))
				.click();
		driver.manage().timeouts().implicitlyWait(80, TimeUnit.SECONDS);
	}

	@Test
	@Parameters({ "originState", "destinationState" })
	public void getSelectedOriginStateAndDestinationStateText(String originState, String destinationState)
			throws Exception {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		String origin = (String) js.executeScript("return document.getElementsByName('origin')[0].value");
		assertEquals(origin, "Munich � MUC");
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		String destination = (String) js.executeScript("return document.getElementsByName('destination')[0].value");
		assertEquals(destination, "Mykonos � JMK");
	}

	@Test
	public void selectNumberOfAdultPassengers() throws Exception {
		driver.findElement(By.xpath("//select[@name='adults']/optgroup/option")).click();
		Select selectChild = new Select(driver.findElement(By.xpath("//select[@name='adults']")));
		selectChild.selectByValue("number:2");
	}

	@Test
	public void selectNumberOfChildPassengers() throws Exception {
		driver.findElement(By.xpath("//select[@name='children']/optgroup/option")).click();
		Select selectChild = new Select(driver.findElement(By.xpath("//select[@name='children']")));
		selectChild.selectByValue("number:2");
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
	}

	@Test
	public void clickFindFlightsButton() throws Exception {
		Actions action = new Actions(driver);
		WebElement element = driver.findElement(
				By.xpath("//form[@name='vm.searchSubmit']/a[@role='button'][contains(text(),'FIND FLIGHTS')]"));
		action.moveToElement(element).click(element).build().perform();
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
	}

	@Test
	public void verifySummaryPageIsOpened() throws Exception {
		String summaryPage = driver.findElement(By.xpath("//section[@class='booking-sumary']/h1")).getText();
		assertEquals(summaryPage, "SUMMARY");
	}

	@Test
	public void getDetailsFromSummaryPage() throws Exception {
		String noOfPassengers = driver.findElement(By.xpath("//div[@class='resume-wrapper']/p")).getText();
		assertTrue(noOfPassengers.contains("4 passengers"));

		String depatureDate = driver.findElement(By.xpath("//div[@class='departure']/p/strong[@class='date']"))
				.getText();
		assertEquals(depatureDate, "Mon 28 May 2018");
		String depatureCity = driver.findElement(By.xpath("//div[@class='departure']/p/strong[2]")).getText();
		assertEquals(depatureCity, "Munich � Mykonos");

		String returnDate = driver.findElement(By.xpath("//div[@class='return']/p/strong[@class='date']")).getText();
		assertEquals(returnDate, "Fri 29 Jun 2018");
		String returnCity = driver.findElement(By.xpath("//div[@class='return']/p/strong[2]")).getText();
		assertEquals(returnCity, "Mykonos � Munich");

		driver.findElement(By.xpath("//div[@id='formBookingStep01']/a[contains(text(),'NEXT STEP')]")).click();
		waitForPageToLoad();
		driver.manage().timeouts().implicitlyWait(80, TimeUnit.SECONDS);
	}

	@Test
	public void verifyPassengerInformationPageIsOpened() throws Exception {
		String passengerDetailsPage = driver.findElement(By.xpath("//h2[@class='title main icon passenger']"))
				.getText();
		assertEquals(passengerDetailsPage, "PASSENGERS");
		adultPassengersCount = driver
				.findElements(By
						.xpath("//div[@data-type='Adult']//div[@class='form-column']//input[contains(@name,'FirstName')]"))
				.size();
		childPassengersCount = driver
				.findElements(By
						.xpath("//div[@data-type='Child']//div[@class='form-column']//input[contains(@name,'FirstName')]"))
				.size();
	}

	@AfterMethod
	public void screenCapture() {
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(scrFile, new File(".\\ScreenCapture\\screenshot_" + appendTimeStampToFile() + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String appendTimeStampToFile() {
		DateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
		String timeStamp = format.format(new Date());
		return timeStamp;
	}

	@Test
	@Parameters({ "adultPassengerFirstName", "adultPassengerSecondName" })
	public void enterAdultFirstNameAndLastName(String adultPassengerFirstName, String adultPassengerSecondName) {
		adultPassengersCount = driver
				.findElements(By
						.xpath("//div[@data-type='Adult']//div[@class='form-column']//input[contains(@name,'FirstName')]"))
				.size();
		for (int adultPassengerCnt = 0; adultPassengerCnt < adultPassengersCount; adultPassengerCnt++) {
			driver.findElement(By
					.xpath("//div[@data-type='Adult']//div[@class='form-column']//input[contains(@name,'FirstName')][contains(@id,'name_"
							+ adultPassengerCnt + "')]"))
					.sendKeys(adultPassengerFirstName);
			driver.findElement(By
					.xpath("//div[@data-type='Adult']//div[@class='form-column']//input[contains(@name,'Surname')][contains(@id,'surname1_"
							+ adultPassengerCnt + "')]"))
					.sendKeys(adultPassengerSecondName);
		}
	}

	@Test(dependsOnMethods = { "enterAdultFirstNameAndLastName" })
	@Parameters({ "childPassengerFirstName", "childPassengerSecondName" })
	public void enterChildFirstNameAndLastName(String childPassengerFirstName, String childPassengerSecondName) {
		childPassengersCount = driver
				.findElements(By
						.xpath("//div[@data-type='Child']//div[@class='form-column']//input[contains(@name,'FirstName')]"))
				.size();
		for (int childPassengerCnt = adultPassengersCount; childPassengerCnt < adultPassengersCount
				+ childPassengersCount; childPassengerCnt++) {
			driver.findElement(By
					.xpath("//div[@data-type='Child']//div[@class='form-column']//input[contains(@name,'FirstName')][contains(@id,'name_"
							+ childPassengerCnt + "')]"))
					.sendKeys(childPassengerFirstName);
			driver.findElement(By
					.xpath("//div[@data-type='Child']//div[@class='form-column']//input[contains(@name,'Surname')][contains(@id,'surname1_"
							+ childPassengerCnt + "')]"))
					.sendKeys(childPassengerSecondName);
		}
	}

	public void waitForPageToLoad() {
		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString()
						.equals("complete");
			}
		};
		try {
			WebDriverWait wait = new WebDriverWait(driver, timeInSeconds);
			wait.until(expectation);
		} catch (Throwable error) {
			Assert.fail("Timeout waiting for Page Load Request to complete.");
		}

	}

	public void waitForElementToPresent(By by) {
		try {
			new WebDriverWait(driver, timeInSeconds).until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isElementDisplayed(String XPath) {
		if (driver.findElements(By.xpath(XPath)).size() > 0) {
			return true;
		}
		return false;
	}

	@AfterClass
	public void afterClass() {
		driver.close();
		driver.quit();
	}
}