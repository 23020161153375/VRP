/**   
* @Title: RobotsControlCenter.java 
* @Package models 
* @Description: TODO(��һ�仰�������ļ���ʲô) 
* @author FlyingFish
* @date 2017-05-24
* @version V1.0   
*/
package models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import dataStructure.other.HeapMin;
import parkingScheme.SpaceDispatcher;
import routing.Routing;
import schedulingScheme.Scheduling;

/**
* <p> RobotsControlCenter</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2017-05-24
*/
public abstract class RobotsControlCenter implements Scheduling {

	public Map map;
	public Routing router;
	public SpaceDispatcher dispatcher;
	
	public int fRobots;
	public int fWating;
	public int fPanishment;
	public int fEnergy;	
		
	public VRP[] applications;
	
	protected RobotsControlCenter(
			Map map,Routing router,SpaceDispatcher dispatcher,
			int k,int p,int a,int b,VRP[] appls){
		this.map = map;
		this.router  = router;
		this.dispatcher = dispatcher;
		fEnergy = k;
		fPanishment = p;
		fRobots = a;
		fWating = b;
		applications = appls;
	}
	
	/** 
	* @Fields pullInTasks : TODO(���������У�һ��ʼ�;�������) 
	*/ 
	protected List<Task> pullInTasks;
	
	/** 
	* @Fields pullOutTasks : TODO(����������У���̬����) 
	*/ 
	protected HeapMin<Task,Integer> pullOutTasks;
	
	protected HeapMin<Robot,Integer> robots;
	
	/** 
	* <p>��ʼ��������</p> 
	* <p>Description: </p> 
	* @param n ��������Ŀ
	*/
	protected void createRobotsSet(int n){
		List<Robot> robots = new ArrayList<Robot>();
		for(int  i = 0; i < n;i ++)
			robots.add(new Robot(i,map.in));	
		this.robots = new HeapMin<Robot,Integer>(robots);
	}
	
	/** 
	* <p>��ʼ�������б�</p> 
	* <p>�������������񵽴����б� </p>  
	*/
	protected void createTaskList(){
		pullInTasks = new LinkedList<Task>();
		pullOutTasks = new HeapMin<Task,Integer>();
		for(int i = 0;i < applications.length;i ++){
			Task pullIn = new Task(applications[i].carID,applications[i].requestTime,Task.PULL_IN,-1) ;
			pullInTasks.add(pullIn);
		}
	}
}
