package resolve;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

/**
 * 主要用来通过用户名和密码登陆知乎获取用户cookie，否则无法获取完整信息;
 * 还可以通过visit方法获取指定页面的源码
 * @author lslxy1021
 *
 */
public class Seed {
	private RequestConfig globalConfig;
	private static CloseableHttpClient httpClient;
	private PoolingHttpClientConnectionManager cm;
	private String xsrfValue;
	private String captURL = "https://www.zhihu.com/captcha.gif?r=" + System.currentTimeMillis() + "&type=login";
	private String userName = "";
	private String passWord = "";

	public String getUserName() {
		return userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public CloseableHttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(CloseableHttpClient httpClient) {
		Seed.httpClient = httpClient;
	}

	/**
	 * 
	 * @param userName 用户名
	 * @param passWord 密    码
	 */
	public Seed(String userName, String passWord) {
		/**
		 * setConnectTimeout：设置连接超时时间，单位毫秒。
		 * setConnectionRequestTimeout：设置从connect Manager获取Connection超时时间，单位毫秒。
		 * setSocketTimeout：请求获取数据的超时时间，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
		 */
		globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).setConnectTimeout(5000)
				.setConnectionRequestTimeout(1000).setSocketTimeout(5000).build();
		cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(20);// 将最大连接数增加到20
		cm.setDefaultMaxPerRoute(20); // 将每个路由基础的连接增加到20
		cm.closeExpiredConnections();
		httpClient = HttpClients.custom().setDefaultRequestConfig(globalConfig).setConnectionManager(cm).build();
		this.userName = userName;
		this.passWord = passWord;
	}

	/**
	 *@param url 登录页面网址
	 *@return 返回用户cookie
	 */
	public String getCookie(String url) {
		HttpGet getMethod = new HttpGet(url);
		getMethod.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		getMethod.setHeader("Accept-Encoding", "gzip, deflate, sdch");
		getMethod.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
		getMethod.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
		CloseableHttpResponse status = null;
		String cookie = null;
		String responseHtml = null;
		HttpResponse loginstatus = null;
		URI u = null;
		try {
			status = httpClient.execute(getMethod);
			cookie = getCookie(status);
			responseHtml = EntityUtils.toString(status.getEntity());
			xsrfValue = responseHtml.split("<input type=\"hidden\" name=\"_xsrf\" value=\"")[1].split("\"/>")[0];
			String captcha = getCaptcha(httpClient, cookie);
			u = new URIBuilder(url).addParameter("_xsrf", xsrfValue).addParameter("phone_num", userName)
					.addParameter("password", passWord).addParameter("captcha", captcha).build();
			HttpPost postMethod = new HttpPost(u);
			postMethod.setHeader("Cookie", cookie);
			loginstatus = httpClient.execute(postMethod);
			if (loginstatus.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY) {
				Header head = loginstatus.getFirstHeader("Location");
				String strHead = head.getValue();
				HttpPost rePostMethod = new HttpPost(strHead);
				loginstatus = httpClient.execute(rePostMethod);
			}
		} catch (Exception e) {}

		String loginCookie = getCookie(loginstatus);
		return loginCookie;
	}

	/**
	 * 
	 * @param httpResponse 服务器回应信息
	 * @return 返回用户cookie
	 */
	public String getCookie(HttpResponse httpResponse) {
		Map<String, String> cookieMap = new HashMap<String, String>(64);
		Header headers[] = httpResponse.getHeaders("Set-Cookie");
		if (headers == null || headers.length == 0) {
			System.out.println("----there are no cookies");
			return null;
		}
		String cookie = "";
		for (int i = 0; i < headers.length; i++) {
			cookie += headers[i].getValue();
			if (i != headers.length - 1) {
				cookie += ";";
			}
		}
		String cookies[] = cookie.split(";");
		for (String c : cookies) {
			c = c.trim();
			cookieMap.put(c.split("=")[0],
					c.split("=").length == 1 ? "" : (c.split("=").length == 2 ? c.split("=")[1] : c.split("=", 2)[1]));// 最多分割2-1次
		}
		String cookiesTmp = "";
		for (String key : cookieMap.keySet()) {
			cookiesTmp += key + "=" + cookieMap.get(key) + ";";
		}
		return cookiesTmp.substring(0, cookiesTmp.length() - 2);
	}
	
	/**
	 * 
	 * @param httpClient 自己设置的HttpClient
	 * @param cookie 用户登录cookie
	 * @return 返回验证码
	 */
	public String getCaptcha(CloseableHttpClient httpClient, String cookie) {
		HttpGet getCaptcha = new HttpGet(captURL);
		getCaptcha.setHeader("Cookie", cookie);
		CloseableHttpResponse imageResponse = null;
		try {
			imageResponse = httpClient.execute(getCaptcha);
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}
		FileOutputStream out;
		try {
			out = new FileOutputStream("e:/Captcha.jpg");
			int imgCont;
			while ((imgCont = imageResponse.getEntity().getContent().read()) != -1) {
				out.write(imgCont);
			}
			out.close();
		} catch (Exception e) {
		}

		System.out.print("请输入验证码:");
		Scanner sc = new Scanner(System.in);
		String captcha = sc.next();
		sc.close();
		return captcha;
	}

	/**
	 * 
	 * @param url 用户关注页面的网址
	 * @return 用户关注页面的网页源代码
	 */
	public static String visit(String url,String cookie) {

		HttpGet get = new HttpGet(url);
		get.setHeader("Cookie",cookie);
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(get);
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		String responseHtml = null;

		if (response.getStatusLine().getStatusCode() == 200) {
			try {
				responseHtml = EntityUtils.toString(response.getEntity(), "utf-8");
			} catch (UnsupportedOperationException |IOException e) {}
		}
		return responseHtml;
	}

}
