package net.krazyweb.cataclysm.mapeditor.map.undo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;

public class UndoBuffer {

	private static Logger log = LogManager.getLogger(UndoBuffer.class);

	private LinkedList<UndoEvent> eventLinkedList = new LinkedList<>();
	private int currentIndex = -1; //Since the first event added will increment this to the index 0, it needs to be -1.

	/**
	 * WARNING: Removes the end of the queue -- the current index is the new last index
	 * @param undoEvent The state to add to the queue
	 */
	public void addEvent(final UndoEvent undoEvent) {
		removeEnd();
		currentIndex++;
		log.trace("Added event to undo buffer: " + undoEvent.getName());
		eventLinkedList.add(undoEvent);
	}

	public boolean hasNextEvent() {
		return currentIndex < (eventLinkedList.size() - 1);
	}

	public boolean hasPreviousEvent() {
		return currentIndex >= 0;
	}

	public UndoEvent peekAtNextEvent() {
		return eventLinkedList.get(currentIndex + 1);
	}

	public UndoEvent getCurrentEvent() {
		return eventLinkedList.get(currentIndex);
	}

	public void undoLastEvent() {
		eventLinkedList.get(currentIndex--).undo();
	}

	public void redoNextEvent() {
		eventLinkedList.get(++currentIndex).redo();
	}

	public UndoEvent removeLastEvent() {
		currentIndex--;
		return eventLinkedList.removeLast();
	}

	private void removeEnd() {
		if (hasNextEvent()) {
			log.info("Removing end of undo buffer.");
			while (eventLinkedList.size() > currentIndex + 1) {
				log.trace("Removing last event from undo buffer: " + peekAtNextEvent());
				eventLinkedList.removeLast();
			}
		}
	}

}
