package resolve;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ParseHtml {
    
	/**
	 * 通过分析网页源代码获取用户的相关信息
	 * @param html 网页源代码
	 * @return 返回url_token，关注人数，关注者人数
	 */
	public static List<String> basicInfo(String html) {
		
		List<String> numberList = new ArrayList<>();
		Document doc = Jsoup.parse(html);
		Elements elements = doc.getElementsByClass("NumberBoard-value");
		String name = doc.getElementsByClass("Tabs-link").first().attr("href");
		Pattern pattern = Pattern.compile("/");
		Matcher match = pattern.matcher(name);
		int number = 0;
		//找到第二个"/"的位置
		while (match.find()) {
			number++;
			if (number == 2) 
				break;
		}
		numberList.add(name.substring(match.start(),name.indexOf("/activities")));
		for(Element e : elements) {
			numberList.add(e.text());
		}
        return numberList;
        
	}

}
