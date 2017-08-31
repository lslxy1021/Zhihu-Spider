# Zhihu-Spider
一个获取知乎用户关注信息的demo

**环境**

 - Eclipse
 - Jsoup 1.10.2 负责解析HTML文档
 - Apache HttpClient 4.5.3 提供了功能丰富的、支持HTTP协议的客户端编程工具包。相比Java自带的.net包，它具有更灵活方便、功能更丰富的特点。
 - fastJSON 1.1.34：Java语言编写的高性能功能完善的JSON库
 
> **Note:** 采用了广度遍历获取用户关注信息。因为知乎对未登录用户做了限制，为了信息的完整性，需要在爬取信息之前进行模拟登陆，并将第一次登陆成功的cookie保存下来以备后序使用

知乎关注页面的数据如果直接从网页源代码看只能得到少部分关注人信息，真正请求的地址通过浏览器自带的开发者工具可以分析出，返回的是JSON格式的数据。

**目录结构**

* loop
  * Follow.java 负责获取请求地址并解析用户数据
  * LinkQueue.java 存储已访问过地址和待访问地址
  * MyCrawler.java 主程序，广度遍历
  * Queue.java 用LinkedList实现的队列，用来存储
* resolve  
  * ParseHtml.java 解析HTML文档  
  * Seed.java 获取登陆cookie


