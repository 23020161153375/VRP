/**   
* @Title: ParkingLotManager.java 
* @Package models 
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2017-05-20
* @version V1.0   
*/
package models;

import java.util.List;


import dataStructure.other.HeapMin;
import parkingScheme.SpaceDispatcher;
import routing.Routing;

/**
* <p> ParkingLotManager</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2017-05-20
*/
public abstract class ParkingLotManager implements SpaceDispatcher{
	public Routing router;

	public Map parkingLot;
		
	protected ParkingLotManager(Map parkingLot, Routing router){

		this.parkingLot = parkingLot;
		this.router = router;
		
		//emptySpaces = new HeapMin<ParkingSpace,Integer>(parkingLot.allSpaces);
		time = 0;
		globalEventsOn = new java.util.ArrayList<Integer>();
	}
	
	protected int time;
	
	//protected HeapMin<ParkingSpace,Integer> emptySpaces;
	
	/** 
	* @Fields globalEventsOn : TODO(记录调度的顺序，但不一定是按时间序的,整数代表事件发生的位置，也就是停车位的编号) 
	*/ 
	protected List<Integer> globalEventsOn;
	
	public void event(Task task){	
		parkingLot.allSpaces.get(task.parkingSpaceID).eventsOn.add(task);
		globalEventsOn.add(task.parkingSpaceID);
	}
	/*	
	protected ParkingSpace removeAEmptySpace(int spaceID){
		int locInHeap = parkingLot.allSpaces.get(spaceID).getElementLocation();
		emptySpaces.decreaseKey(locInHeap, -1);
		ParkingSpace removedSpace = emptySpaces.remove();
		removedSpace.updateKey(router.routing(parkingLot.in, removedSpace.location).size() 
				+ router.routing(parkingLot.out, removedSpace.location).size());
	//	removedSpace.updateKey(removedSpace.routings.get(0).size() 
	//			+ removedSpace.routings.get(1).size());
		
		return removedSpace;
	}*/
	
	protected void stateChangeClockwise(int time){
		if(time <= this.time)
			throw new IllegalStateException("-时间大于当前时间时才会调整");
		//注意到在系统所处时刻开始的事件对当时的快照结果无影响
		
		int nSpaces = parkingLot.allSpaces.size();
		for(int i = 0;i < nSpaces;i ++){
			List<Task> events = parkingLot.allSpaces.get(i).eventsOn;
			int j = parkingLot.allSpaces.get(i).lastEventIndex;
			while(j + 1 < events.size()){
				Task event = events.get(j + 1);
				if(event.taskType == Task.PULL_IN && event.realFinishTime <= time)
					j ++;
				else if(event.taskType == Task.PULL_OUT && event.realStartTime < time)
					//对于出库情形，确保在给定时刻车位已空出来了
					j ++;
				else//否则j+1事件未完成
					break;
			}
			if(j != parkingLot.allSpaces.get(i).lastEventIndex){//j代表这段时间内（即(this.time,time]）会对所在车位发生影响的最后一项事件（不一定完成）
				if(events.get(j).taskType == Task.PULL_IN && parkingLot.allSpaces.get(i).empty){
					//removeAEmptySpace(i);
					parkingLot.allSpaces.get(i).empty = false;
				}else if(events.get(j).taskType == Task.PULL_OUT && !parkingLot.allSpaces.get(i).empty){
					//emptySpaces.add(parkingLot.allSpaces.get(i));
					parkingLot.allSpaces.get(i).empty = true;
				}
				
				parkingLot.allSpaces.get(i).lastEventIndex = j;
				
			}						
		}
		//记录当前系统时间
		this.time = time;
		
		//调试用
		//printCurrentStateOfParkingLot();
	}
	
	/** 
	* <p>打印停车场状态 </p> 
	* <p>调试用</p>  
	*/
	protected void printCurrentStateOfParkingLot(){
		System.out.println("当前时间 " + time+",全局事件 "+globalEventsOn.size()+"件，分配前停车位状态（Empty/Busy）");
		for(int i = 0;i < parkingLot.allSpaces.size();i ++){
			System.out.printf("%3d",i);			
		}
		System.out.println();
		for(int i = 0;i < parkingLot.allSpaces.size();i ++){
			System.out.printf("%3c",parkingLot.allSpaces.get(i).empty ? 'E':'B');			
		}
		System.out.println();
	}
	
	/** (non-Javadoc)
	 * @see parkingScheme.SpaceDispatcher#restore(int)
	 */
	@Override
	public void restore(int restoreTime,int eventsNumber) {
		// TODO Auto-generated method stub
		if(restoreTime == -1 && eventsNumber == -1){
			//特殊指令，恢复到初始状态
			restoreTime = 0;
			eventsNumber = globalEventsOn.size();
		}
		
		undoEvents(eventsNumber);
		if(restoreTime != time){//时间相等不用调整
			stateChangeAnticlockwise(restoreTime);
		}
		
		//调试用
		//printCurrentStateOfParkingLot();
	}
	
	protected void stateChangeAnticlockwise(int time){
		if(time >= this.time)
			throw new IllegalStateException(time +" >= " + this.time+"(系统时间)，只能选择当初的某个时间点");
		
		int nSpaces = parkingLot.allSpaces.size();

		for(int i = 0;i < nSpaces;i ++){
			List<Task> events = parkingLot.allSpaces.get(i).eventsOn;
			int lastEventIndex= parkingLot.allSpaces.get(i).lastEventIndex;
			int j = lastEventIndex;
			
			//有的事件可能在回退时被移除了
			while(j >= events.size()) j--;
			
			while(j >= 0){
				Task event = events.get(j);
				if(event.taskType == Task.PULL_IN && event.realFinishTime > time)
					//入库事件j完成
					j --;
				else if(event.taskType == Task.PULL_OUT && event.realStartTime >= time)
					//车辆未离开车位
					j --;
				else
					break;
			}
			if(j != -1){//j代表在(0,time]时间内会对所在车位发生影响的最后一项事件（不一定完成）
				if(events.get(j).taskType == Task.PULL_IN){
					//removeAEmptySpace(i);
					parkingLot.allSpaces.get(i).empty = false;
				}else if(events.get(j).taskType == Task.PULL_OUT ){
					//emptySpaces.add(parkingLot.allSpaces.get(i));
					parkingLot.allSpaces.get(i).empty = true;
				}
			}else
				//在这个位置上什么事也没发生
				parkingLot.allSpaces.get(i).empty = true;
			
			parkingLot.allSpaces.get(i).lastEventIndex = j;
			
			//释放最后一个完成事件后面的所有事件
			//while(events.size() > j+1)
				//events.remove(events.size() - 1);
		}
		//记录当前系统时间
		this.time = time;	
	}
	
	protected void undoEvents(int number){
		while(number-- > 0){
			int spaceID = globalEventsOn.
					remove(globalEventsOn.size() - 1);
			parkingLot.allSpaces.get(spaceID).eventsOn
				.remove(parkingLot.allSpaces.get(spaceID)
								.eventsOn.size() - 1);
		}		
	}
	
}
