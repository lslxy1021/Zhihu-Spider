package loop;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import resolve.Seed;

public class MyCrawler {
	private String bURL = "https://www.zhihu.com/people/";
	private String eURL = "/following";
	private Seed seed;
	private Follow followList;
	private Map<Object, List<Object>> personMap;
	private Iterator<Object> personKey;
	private int count = 0;
	private int size = 0;

	public MyCrawler(String userName, String pwd) {
		this.seed = new Seed(userName, pwd);
		this.followList = new Follow(seed);
	}
	/**
	 * @param initialUrl 初始网址，注意网址格式要一致
	 */
	public void crawling(String initialUrl) {
		LinkQueue.addUnVisited(initialUrl);
		while (!LinkQueue.isUnVisitedEmpty() && LinkQueue.getVisitedSize() < 200) {
			String url = (String)LinkQueue.removeUnVisited();
			if (url == null) 
				continue;
			followList.parseURL(url);
			personMap = followList.getPerson();
			Iterator<List<Object>> personValue = personMap.values().iterator();
			if (count == 0) {
				personKey = personMap.keySet().iterator();
				size = personMap.size();
				count++;
			}
			System.out.println(personMap.keySet());
			if (count < size) {
				System.out.print(personKey.next() + ":");
				count++;
			} 
			if (count == size) {
				System.out.print(personKey.next() + ":");
				count = 0;
			}
			personMap.size();
			LinkQueue.addVisited(url);
			String nextURL;
			while (personValue.hasNext()) {
				nextURL = bURL + personValue.next().get(1) + eURL;
				LinkQueue.addUnVisited(nextURL);
			}
		}
	}
	public static void main(String[] args) {
		MyCrawler crawl = new MyCrawler("请输入用户名", "请输入密码");
		//格式:https://www.zhihu.com/people/url_token/following
		crawl.crawling("请输入初始访问网址");
	}
}
