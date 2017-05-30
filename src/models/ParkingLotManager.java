/**   
* @Title: ParkingLotManager.java 
* @Package models 
* @Description: TODO(��һ�仰�������ļ���ʲô) 
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
		
		emptySpaces = new HeapMin<ParkingSpace,Integer>(parkingLot.allSpaces);
		time = 0;
	}
	
	protected int time;
	
	protected HeapMin<ParkingSpace,Integer> emptySpaces;
	
	public void event(Task task){	
		parkingLot.allSpaces.get(task.parkingSpaceID).eventsOn.add(task);
	}
	
	/*
	public int pullOutAck(int parkingSpaceID,int time ){
		if(time < this.time)
			throw new IllegalArgumentException("�밴");
		
		return 0;
	}*/
	
	protected ParkingSpace removeAEmptySpace(int spaceID){
		int locInHeap = parkingLot.allSpaces.get(spaceID).getElementLocation();
		emptySpaces.decreaseKey(locInHeap, -1);
		ParkingSpace removedSpace = emptySpaces.remove();
		removedSpace.updateKey(router.routing(parkingLot.in, removedSpace.location).size() 
				+ router.routing(parkingLot.out, removedSpace.location).size());
	//	removedSpace.updateKey(removedSpace.routings.get(0).size() 
	//			+ removedSpace.routings.get(1).size());
		
		return removedSpace;
	}
	
	protected void stateChangeClockwise(int time){
		if(time <= this.time)
			throw new IllegalStateException("-ʱ����ڵ�ǰʱ��ʱ�Ż����");
		//ע�⵽��ϵͳ����ʱ�̿�ʼ���¼��Ե�ʱ�Ŀ��ս����Ӱ��
		
		int nSpaces = parkingLot.allSpaces.size();
		for(int i = 0;i < nSpaces;i ++){
			List<Task> events = parkingLot.allSpaces.get(i).eventsOn;
			int j = -1;
			while(j + 1 < events.size()){
				Task event = events.get(j + 1);
				if(event.taskType == Task.PULL_IN && event.realFinishTime <= time)
					j ++;
				else if(event.taskType == Task.PULL_OUT && event.realStartTime < time)
					//���ڳ������Σ�ȷ���ڸ���ʱ�̳�λ�ѿճ�����
					j ++;				
			}
			if(j != -1){//j�������ʱ���ڣ���(this.time,time]��������ڳ�λ����Ӱ������һ���¼�����һ����ɣ�
				if(events.get(j).taskType == Task.PULL_IN && parkingLot.allSpaces.get(i).empty){
					removeAEmptySpace(i);
					parkingLot.allSpaces.get(i).empty = false;
				}else if(events.get(i).taskType == Task.PULL_OUT && !parkingLot.allSpaces.get(i).empty){
					emptySpaces.add(parkingLot.allSpaces.get(i));
					parkingLot.allSpaces.get(i).empty = true;
				}
				
				//�ͷ����һ������¼�ǰ��������¼�
				for(int k = 0;k < j;k ++ )
					events.remove(0);
			}						
		}
		//��¼��ǰϵͳʱ��
		this.time = time;
	}
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param args 
	*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
