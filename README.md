# Zhihu-Spider
一个获取知乎用户关注信息的demo

**环境**

 - Eclipse
 - Jsoup 1.10.2 负责解析HTML文档
 - Apache HttpClient 4.5.3 提供了功能丰富的、支持HTTP协议的客户端编程工具包。相比Java自带的.net包，它具有更灵活方便、功能更丰富的特点。
 - fastJSON 1.1.34：Java语言编写的高性能功能完善的JSON库
 
> **Note:** 采用了广度遍历获取用户关注信息。因为知乎对未登录用户做了限制，为了信息的完整性，需要在爬取信息之前进行模拟登陆，并将第一次登陆成功的cookie保存下来以备后序使用
