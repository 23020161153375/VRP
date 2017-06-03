/**   
* @Title: ParkingSpace.java 
* @Package models 
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2017-05-20
* @version V1.0   
*/
package models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


import dataStructure.other.HeapElement;

/**
* <p> ParkingSpace</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2017-05-20
*/
public class ParkingSpace implements Comparable<ParkingSpace>,HeapElement<Integer> {

	public int id;
	public Point location;
	public Point inlet;
	
	public ParkingSpace(int id, Point location,Point inlet){
		this.id = id;
		this.location = location;
		this.inlet = inlet;

		empty = true;
		
		//所有发生过的事件（改成数组形式）
		eventsOn = new ArrayList<Task>();
		
		lastEventIndex = -1;
	}
	

	public boolean empty;

	//最后一次发生的事件
	public int lastEventIndex;
	
	//所有的事件
	public List<Task> eventsOn;
		 
	public Task lastEvent(){
		if(lastEventIndex == -1)
			return null;
		else
			return eventsOn.get(lastEventIndex);
	}
	
	public Task firstPlanningEvent(){
		if(lastEventIndex + 1 >= eventsOn.size())
			return null;
		else
			return eventsOn.get(lastEventIndex + 1);		
	}
	
	public int key;
	/** (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ParkingSpace o) {
		// TODO Auto-generated method stub
		return key - o.key;
	}
	
	private int loc;
	/** (non-Javadoc)
	 * @see dataStructure.other.HeapElement#setElementLocation(int)
	 */
	@Override
	public void setElementLocation(int location) {
		// TODO Auto-generated method stub
		loc = location;
	}


	/** (non-Javadoc)
	 * @see dataStructure.other.HeapElement#getElementLocation()
	 */
	@Override
	public int getElementLocation() {
		// TODO Auto-generated method stub
		return loc;
	}


	/** (non-Javadoc)
	 * @see dataStructure.other.HeapElement#updateKey(java.lang.Object)
	 */
	@Override
	public void updateKey(Integer key) {
		// TODO Auto-generated method stub
		this.key = key;
	}
	
}
