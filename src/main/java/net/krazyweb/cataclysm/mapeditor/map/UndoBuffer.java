package net.krazyweb.cataclysm.mapeditor.map;

import java.util.LinkedList;

public class UndoBuffer {

	private LinkedList<CataclysmMap.State> eventLinkedList = new LinkedList<>();
	private int currentIndex = -1; //Since the first event added will increment this to the index 0, it needs to be -1.

	/**
	 * WARNING: Removes the end of the queue -- the current index is the new last index
	 * @param state The state to add to the queue
	 */
	public void addState(final CataclysmMap.State state) {
		removeEnd();
		currentIndex++;
		eventLinkedList.add(state);
	}

	public boolean hasNextEvent() {
		return currentIndex < (eventLinkedList.size() - 1);
	}

	public boolean hasPreviousEvent() {
		return currentIndex > 0;
	}

	public CataclysmMap.State getNextEvent() {
		return eventLinkedList.get(++currentIndex);
	}

	public CataclysmMap.State getCurrentEvent() {
		return eventLinkedList.get(currentIndex);
	}

	public CataclysmMap.State undoLastEvent() {
		return eventLinkedList.get(--currentIndex);
	}

	public CataclysmMap.State removeLastEvent() {
		currentIndex--;
		return eventLinkedList.removeLast();
	}

	private void removeEnd() {
		if (hasNextEvent()) {
			while (eventLinkedList.size() > currentIndex + 1) {
				eventLinkedList.removeLast();
			}
		}
	}

}
