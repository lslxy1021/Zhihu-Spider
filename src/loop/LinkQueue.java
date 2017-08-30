package loop;

import java.util.HashSet;

public class LinkQueue {
	private static  HashSet<Object> visited = new HashSet<>();
	private static Queue<Object> unVisited = new Queue<>();
	
	public static Queue<Object> getUnVisited() {
		return unVisited;
	}
	/**
	 * 向已访问集合中添加元素
	 * @param obj
	 */
	public static void addVisited(Object obj) {
		visited.add(obj);
	}
	/**
	 * 从已访问集合visited中移除已访问obj
	 * @param obj
	 */
	public static void removeVisited(Object obj) {
		visited.remove(obj);
	}
	/**
	 * 保证每个obj只被访问一次
	 * @param obj
	 */
	public static void addUnVisited(Object obj) {
		if (obj != null && !visited.contains(obj) && !unVisited.contains(obj)) {
			unVisited.offer(obj);
		}
	}
	/**
	 * 出队
	 * @return
	 */
	public static Object removeUnVisited() {
		return unVisited.remove();
	}
	/**
	 * 返回已访问网址的数量
	 * @return visited集合长度
	 */
	public static int getVisitedSize() {
		return visited.size();
	}
	/**
	 * 判断未访问集合是否为空
	 * @return 为空返回true
	 */
	public static boolean isUnVisitedEmpty() {
		return unVisited.isEmpty();
	}
}
