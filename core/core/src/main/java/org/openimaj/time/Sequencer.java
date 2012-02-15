/**
 * 
 */
package org.openimaj.time;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

/**
 *	An object that will fire events at specific times thereby sequencing
 *	objects into a stream.  Can be used to trigger anything, such as animations
 *	on a slideshow or sequencing audio samples.
 *	<p>
 *	The class is parameterised on a {@link Timecode} object which will be used
 *	to determine whether actions should be triggered. The accuracy of the
 *	triggers can be determined by changing the internal timer tick of the
 *	sequencer. For example, if you know that actions will never occur more
 *	than once a second, you can set the tick to something near a second.
 *	<p>
 *	This class will automatically start the {@link TimeKeeper} running when
 *	it is run.  The time of firing of an event cannot be guaranteed and an event
 *	will always be fired if 
 *
 *	@author David Dupplaw <dpd@ecs.soton.ac.uk>
 *	@version $Author$, $Revision$, $Date$
 *	@created 27 Nov 2011
 */
public class Sequencer implements Runnable
{
	/**
	 *	A class that can be sequenced by the sequencer can implement this
	 *	interface. The only method, {@link #performAction()}, should 	
	 *	execute the desired action.
	 *
	 *	@author David Dupplaw <dpd@ecs.soton.ac.uk>
	 *	@version $Author$, $Revision$, $Date$
	 *	@created 29 Nov 2011
	 */
	static public interface SequencedAction
	{
		/**
		 * 	Perform the sequenced action.
		 *	@return TRUE if the action trigger succeeded; FALSE otherwise.
		 */
		public boolean performAction();
	}
	
	/**
	 * 	An event in the sequencer, this represents an action occurring at a
	 * 	specific time.
	 *
	 *	@author David Dupplaw <dpd@ecs.soton.ac.uk>
	 *	@version $Author$, $Revision$, $Date$
	 *	@created 29 Nov 2011
	 */
	static public class SequencerEvent implements Comparable<SequencerEvent>
	{
		/** The sequenced action */
		public SequencedAction action = null;
		
		/** The time at which the sequenced action should happen */
		public long timestamp = 0;
		
		/** Whether the event has already been fired. */
		public boolean fired = false;
		
		/** Whether the event failed to fire */
		public boolean failed = false;
		
		/**
		 *  Create a new sequencer event that occurs at a specific time.
		 *	@param timestamp The time the sequencer event should occur.
		 *	@param action The action that should happen at the given time.
		 */
		public SequencerEvent( long timestamp, SequencedAction action )
		{
			this.timestamp = timestamp;
			this.action = action;
		}
		
		/**
		 *  Create a new sequencer event that occurs at a specific time.
		 *	@param tc The time the sequencer event should occur.
		 *	@param action The action that should happen at the given time.
		 */
		public SequencerEvent( Timecode tc, SequencedAction action )
		{
			this.timestamp = tc.getTimecodeInMilliseconds();
			this.action = action;
		}

		/**
		 *	{@inheritDoc}
		 * 	@see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo( SequencerEvent event )
		{
			int v = (int)(timestamp - event.timestamp);
			if( v == 0 && event != this )
				return -1;
			else return v;
		}

		/**
		 *	{@inheritDoc}
		 * 	@see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			return "@"+timestamp;
		}
	}
	
	/**
	 *	A simple {@link TimerTask} that calls the private method checkForActions()
	 *	to check whether there are any actions that should be executed. 
	 *
	 *	@author David Dupplaw <dpd@ecs.soton.ac.uk>
	 *	@version $Author$, $Revision$, $Date$
	 *	@created 29 Nov 2011
	 */
	private class CheckActionTask extends TimerTask
	{
		@Override
		public void run()
		{
			checkForActions();
		}
	}
	
	/** The timekeeper that can provide the current time */
	private TimeKeeper<Timecode> timeKeeper = null;
	
	/** Check for action triggers every 1000 milliseconds by default */
	private long tickAccuracyMillis = 1000;
	
	/** The set of events - using a {@link TreeSet} so that they are ordered */
	private TreeSet<SequencerEvent> events = new TreeSet<SequencerEvent>();
	
	/** Whether to remove the events from the sequencer when they are complete. */
	private boolean removeEventsWhenComplete = true;
	
	/** Whether to retry failed events */
	private boolean retryFailedEvents = false;
	
	/** The timer to use for event scheduling */
	private Timer timer = null;

	/**
	 * 	Default constructor that instantiates a sequencer that will check
	 * 	for actions every 10 milliseconds using the given time keeper.
	 * 
	 *  @param timeKeeper The timekeeper to use
	 */
	public Sequencer( TimeKeeper<Timecode> timeKeeper )
	{
		this.timeKeeper = timeKeeper;
	}
	
	/**
	 * 	Constructor that instantiates a sequencer that will check for actions
	 * 	at the given rate using the given time keeper.
	 * 
	 *	@param timeKeeper The timekeeper to use
	 *	@param tickAccuracyMillis How often the sequencer will check for events.
	 */
	public Sequencer( TimeKeeper<Timecode> timeKeeper, long tickAccuracyMillis )
	{
		this.timeKeeper = timeKeeper;
		this.tickAccuracyMillis = tickAccuracyMillis;
	}

	/**
	 * 	Add an event to the sequencer.
	 *	@param event The event to add.
	 */
	public void addEvent( SequencerEvent event )
	{
		events.add( event );
	}
	
	/**
	 *	{@inheritDoc}
	 * 	@see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		new Thread( timeKeeper ).start();
		timer = new Timer();
		timer.scheduleAtFixedRate( new CheckActionTask(), 0, tickAccuracyMillis );		
	}	

	/**
	 * 	Returns the set of events in this sequencer.
	 *	@return The set of events in this sequencer.
	 */
	public TreeSet<SequencerEvent> getEvents()
	{
		return events;
	}
	
	/**
	 * 	Check whether any actions should be run.
	 */
	private void checkForActions()
	{
		// Time how long it takes to do the processing, so we can
		// subtract this time from the next timer
		long startProcessingTime = System.currentTimeMillis();
		
		// Get the current time
		Timecode tc = timeKeeper.getTime();
		long t = tc.getTimecodeInMilliseconds();

		Iterator<SequencerEvent> eventIterator = events.iterator();
		while( eventIterator.hasNext() )
		{
			// Get the next event.
			SequencerEvent event = eventIterator.next();
			
			// If the even was supposed to be fired in the past or now,
			// then we better get on and fire it.
			if( !event.fired && event.timestamp <= t )
			{
				// Perform the action
				boolean success = event.action.performAction();
				
				// Remove the event if that's what we're to do...
				if( (success || !retryFailedEvents) && removeEventsWhenComplete )
					eventIterator.remove();
				else
				{
					// Set the event information
					if( retryFailedEvents )
							event.fired = success;
					else	event.fired = true;
					
					event.failed = !success;
				}
			}
		}
		
		// Set a new timer
		long processingTime = System.currentTimeMillis() - startProcessingTime;
		long nextTime = tickAccuracyMillis - processingTime;
		while( nextTime < 0 )
			nextTime += tickAccuracyMillis;
		
	}
	
	/**
	 * 	Sets whether failed events will be retried at the next processing
	 * 	time.
	 * 
	 *	@param rfe TRUE to retry failed events.
	 */
	public void setRetryFailedEvents( boolean rfe )
	{
		this.retryFailedEvents = rfe;
	}
	
	/**
	 * 	Sets whether to remove events from the event list when they have
	 * 	been completed. Doing so will speed up the processing
	 * 	at each event check but events will be lost. A way around this is to
	 * 	set up all your events then use {@link #getEvents()} to retrieve the
	 * 	list of events and clone it.  Failed events will be removed from
	 * 	the list only if retryFailedEvents is set to false.
	 * 
	 *	@param rewc TRUE to remove successfully completed events.
	 */
	public void setRemoveEventsWhenComplete( boolean rewc )
	{
		this.removeEventsWhenComplete = rewc;
	}
}