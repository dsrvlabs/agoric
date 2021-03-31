package com.dsrvlabs.agoric.telegrambot;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.dsrvlabs.common.util.TelegramMsgSender;

/**
 * Wait 관련 설명 : https://www.browserstack.com/guide/wait-commands-in-selenium-webdriver
 * fineElement : http://bit.ly/2tZTLVY
 * @author jongkwang
 *
 */
public class SeleniumWorker extends Thread {
	
	// WebDriver
	private WebDriver driver;
	private WebElement webElement;

	// Properties
	private static boolean HEADLESS_MODE = true;
	
	// 잠자기 시간을 짧게 할 것인가?
	private final boolean SLEEP_MODE_PRODUCT = false;
	
	
	public static final String WEB_DRIVER_ID = "webdriver.chrome.driver";
	public static String WEB_DRIVER_PATH = "/var/www/bin/chromedriver/chromedriver";
	private final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.106 Safari/537.36";
	
	private final boolean PRINT_STEP = true;
	private static final int MAX_PAGE_ROOP = 5;
	

	// Agoric Explorer
	//private String URL = "https://testnet.explorer.agoric.net";
	
	// 내부 변수
	private String telegramId = null;
	private String cmd = null;
	
	// Log4j
	static Logger logger = Logger.getLogger(SeleniumWorker.class.getName());

	public static void main(String[] args) {
		
		String telegramId = "166492353";
		String cmd = "/agoricvaloper16c65m9jn8rm85vzl2ned8xjrwk9jn6t3v04u8g";
		
		HEADLESS_MODE = false;
		
		SeleniumWorker crawling_Main = new SeleniumWorker(telegramId, cmd);
		crawling_Main.run();
	}

	public SeleniumWorker( String telegramId, String cmd ) {
		super();
		
		this.telegramId = telegramId;
		this.cmd = cmd;
		
		// System Property SetUp
		System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
		
		// Driver SetUp
		ChromeOptions options = new ChromeOptions();
		options.setCapability("ignoreProtectedModeSettings", true);
		options.addArguments("user-agent=" + USER_AGENT);
		if( HEADLESS_MODE ) {
			 options.addArguments("headless"); // Headless mode
		}
		options.addArguments("--disable-gpu", "--window-size=1280,700", "--ignore-certificate-errors", "--no-sandbox", "--disable-dev-shm-usage", "--blink-settings=imagesEnabled=false", "--disable-extensions");
		driver = new ChromeDriver(options);
		driver.manage().deleteAllCookies();	// delete all cookies
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);	// Implicit Wait
		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
	}
	
	/**
	 * 
	 */
	public void run() {
		
		if ( cmd.equals("/BlockchainData") ) {
			getBlockchainData();
			
		} else if ( cmd.equals("/ValidatorList") ) {
			getValidatorList();
			
		} else if ( cmd.startsWith("/agoricv") ) {
			getValidatorData();
			
		} else {
		}

	}

	private void getValidatorData() {
		try {
			WebDriverWait wait = new WebDriverWait(driver, 10);
			
			// #### URL Start
			driver.get("https://testnet.explorer.agoric.net/validator" + cmd);
			
			String msg = "";
			
			// #### waiting
			webElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#main > div:nth-child(3) > div:nth-child(2) > div > div > div.col-md-4 > div.text-center.card.card-body > div.moniker.text-primary > a")));
			String validatorName = webElement.getText();
			
			webElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#main > div:nth-child(3) > div:nth-child(2) > div > div > div.col-md-8 > div:nth-child(1) > div.card-body > div > div:nth-child(7)")));
			String commissionRate = webElement.getText().split("%")[0] + "%";
			
			webElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#main > div:nth-child(3) > div:nth-child(2) > div > div > div.col-md-8 > div:nth-child(1) > div.card-body > div > div:nth-child(9)")));
			String commissionMaxRate = webElement.getText();
			
			webElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#main > div:nth-child(3) > div:nth-child(2) > div > div > div.col-md-4 > div:nth-child(2) > div.card-body > div > div.value.text-right.col-4")));
			String uptime = webElement.getText();
			
			System.out.println("##### commissionRate : " + commissionRate);
			System.out.println("##### commissionMaxRate : " + commissionMaxRate);

			msg += "*"+validatorName+"*\n";
			msg += "- Commission Rate : " + commissionRate + "\n";
			msg += "- Commission Max Rate : " + commissionMaxRate + "\n";
			msg += "- Uptime(Last 10000 blocks) : " + uptime + "\n";
			
			TelegramMsgSender.sendMsgToChannel(telegramId, msg);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} finally {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			driver.close();
		}
	}

	private void getValidatorList() {
		try {
			WebDriverWait wait = new WebDriverWait(driver, 10);
			
			// #### URL Start
			driver.get("https://testnet.explorer.agoric.net/validators");
			
			// #### waiting
			webElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#validator-list > div.validator-list.row div.card")));
			
			Thread.sleep(2000);
			
			List<WebElement> list = driver.findElements(By.cssSelector("#validator-list > div.validator-list.row div.card"));
			String msg = "";
			String validatorName = null;
			String validatorAddress = null;
			
			
			for( int i=2; i < list.size() - 1; i++ ) {
				System.out.println( "### i = " + i);
				logger.debug("### i = " + i);
				webElement = driver.findElement(By.cssSelector("#validator-list > div.validator-list.row div.card:nth-child("+i+") span.moniker"));
				validatorName = webElement.getText();
				
				webElement = driver.findElement(By.cssSelector("#validator-list > div.validator-list.row div.card:nth-child("+i+") a"));
				validatorAddress = webElement.getAttribute("href").split("validator")[1];
				msg += validatorName + " : " +  validatorAddress + "\n";
				
				System.out.println( "### " + i + " : " + msg);
				
				if( i % 30 == 0 ) {
					System.out.println( "$$$$$$ " + i + " : " + msg);
					TelegramMsgSender.sendMsgToChannel(telegramId, msg);
					msg = "";
				}
			}
			
			System.out.println( "### End");
			TelegramMsgSender.sendMsgToChannel(telegramId, msg);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} finally {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			driver.close();
		}
	}

	private void getBlockchainData() {
		try {
			WebDriverWait wait = new WebDriverWait(driver, 10);
			
			// #### URL Start
			driver.get("https://testnet.explorer.agoric.net");
			
			// #### waiting
			webElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#home > div.row:nth-child(3) > div.col-md-6:nth-child(1) span.value")));
			
			// #### [View] 탭 찾기
			webElement = driver.findElement(By.cssSelector("#home > div.row:nth-child(3) > div.col-md-6:nth-child(1) span.value"));
			String latestBlockHeight = webElement.getText();
			
			webElement = driver.findElement(By.cssSelector("#home > div.row:nth-child(3) > div.col-md-6:nth-child(3) span.value"));
			String activeValidators = webElement.getText();
			
			webElement = driver.findElement(By.cssSelector("#home > div.row:nth-child(3) > div.col-md-6:nth-child(4) span.value"));
			String votingPower = webElement.getText();
			
			String msg = "";
			msg += "- Latest Block Height : " + latestBlockHeight;
			msg += "\n- Active Validators : " + activeValidators;
			msg += "\n- Voting Power : " + votingPower;
			msg += "\n- Agoric Price : (N/A)";
			
			TelegramMsgSender.sendMsgToChannel(telegramId, msg);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} finally {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			driver.close();
		}
	}
}