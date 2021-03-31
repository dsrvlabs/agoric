package com.dsrvlabs.agoric.telegrambot;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.dsrvlabs.common.db.CommonDao;
import com.dsrvlabs.common.util.MyDate;
import com.dsrvlabs.common.util.MyNumber;
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
	private String URL = "https://testnet.explorer.agoric.net";
	
	// 내부 변수
	private String telegramId = null;
	private String cmd = null;

	public static void main(String[] args) {
		
		String telegramId = "166492352";
		String cmd = "";
		
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

		try {
			WebDriverWait wait = new WebDriverWait(driver, 10);
			
			// #### 네이버 첫페이지 열기
			driver.get(URL);
			
			// #### 검색어 입력
			webElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#home > div.row:nth-child(3) > div.col-md-6:nth-child(1) span.value")));
			
			// #### [View] 탭 찾기
			WebElement webElement = driver.findElement(By.cssSelector("#home > div.row:nth-child(3) > div.col-md-6:nth-child(1) span.value"));
			
			System.out.println("##### Thread(PC) End - " + MyDate.getCurrnetDate("yyyy-MM-dd HH:mm:ss"));
			System.out.println("### webElement : " + webElement.getText());
			
			
			TelegramMsgSender.sendMsgToChannel(telegramId, webElement.getText());
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			driver.close();
		}
	}
	
	private void updateRanking(int ranking, String title) {
		HashMap p = new HashMap();
		p.put("pcRanking", ranking);
		p.put("title", title);
		
		CommonDao dao = new CommonDao();
		int updateCount = dao.commonUpdate("Mapper.updateRankingPc", p);
		
	}
	
	private void sleep(int base, int percent) {
		try {
			if( SLEEP_MODE_PRODUCT ) {
				Thread.sleep(MyNumber.getRandomInt(base, percent));
			} else {
				Thread.sleep(MyNumber.getRandomInt(100, 1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}