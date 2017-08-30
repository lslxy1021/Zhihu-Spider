package loop;

import java.util.LinkedList;

public class Queue<T> {
	private LinkedList<T> queue = new LinkedList<>();
	
	public void offer(T obj) {
		queue.addLast(obj);
	}
	public Object remove() {
		return queue.removeFirst();
	}
	public boolean isEmpty() {
		return queue.isEmpty();
	}
	public boolean contains(T obj) {
		return queue.contains(obj);
	}
}
