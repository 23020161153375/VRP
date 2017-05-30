/**   
* @Title: RobotsControllerI.java 
* @Package schedulingScheme 
* @Description: TODO(��һ�仰�������ļ���ʲô) 
* @author FlyingFish
* @date 2017-05-24
* @version V1.0   
*/
package schedulingScheme;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import models.DispatchState;
import models.Map;
import models.Point;
import models.Robot;
import models.RobotsControlCenter;
import models.Task;
import models.VRP;
import parkingScheme.SpaceDispatcher;
import routing.Routing;

/**
* <p> RobotsControllerI</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2017-05-24
*/
public class RobotsControllerI extends RobotsControlCenter {

	
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param map
	* @param router
	* @param dispatcher
	* @param k
	* @param p
	* @param a
	* @param b
	* @param appls 
	*/
	public RobotsControllerI(Map map, Routing router, SpaceDispatcher dispatcher, int k, int p, int a, int b,
			VRP[] appls) {
		super(map, router, dispatcher, k, p, a, b, appls);
		// TODO Auto-generated constructor stub
	}

	

	
	/** (non-Javadoc)
	 * @see schedulingScheme.Scheduling#scheduling(int)
	 */
	@Override
	public int scheduling(int nRobots) {
		// TODO Auto-generated method stub
		this.createRobotsSet(nRobots);
		this.createTaskList();
		
		while(!pullInTasks.isEmpty() && !pullOutTasks.isEmpty()){
			
			/*
			if(!pullInTasks.isEmpty()){
				taskQ.add(pullInTasks.remove(0));
			}*/
			
			
		}
		
		return 0;
	}

	private List<Robot> robotsQ = new LinkedList<Robot>();
	private List<Task> taskQ1 = new ArrayList<Task>();
	private List<Task> taskQ2 = new LinkedList<Task>();
	
	protected List<Tuple> taskSelection(){
		
		//�Ȳ鿴�������ĵ�һ���������
		Task taskIn = this.pullInTasks.get(0);
		
		//�鿴��һ�����л�����
		Robot robot = robots.peek();
		DispatchState ds ;
		
		//��¼��ǰʵ�ʿ���ִ�е����еĳ������񣬷ŵ�taskQ2��
		int realStartTime = Math.max(taskIn.startTime, robot.completementTime);
		while(!pullOutTasks.isEmpty() && pullOutTasks.peek().startTime <= realStartTime)
			taskQ2.add(pullOutTasks.remove());
		

		if(!(ds = dispatcher.parkingSpaceDispatch(applications, 
				taskIn.carID,realStartTime)).success){
			//���û�г�λ�ɹ�����
			
			if(!taskQ2.isEmpty()){
				//�����ǰ�п���ִ�еĳ�������ѡ���翪ʼ��ȥִ��
				Tuple pullOut = new Tuple(taskQ2.remove(0),robots.remove());
				List<Tuple> result = new ArrayList<Tuple>();
				result.add(pullOut);
				return result;
			}else{
				//���򣬵ȴ���ִ��һ����������
				Tuple pullOut = new Tuple(pullOutTasks.remove(),robots.remove());	
				List<Tuple> result = new ArrayList<Tuple>();
				result.add(pullOut);
				return result;
			}				
		}else if(robot.completementTime >
			taskIn.startTime + this.applications[taskIn.carID].longestWatingTime){
		//	��������˳�λ���������˸ϲ��ϣ�����
			
			List<Tuple> result = new ArrayList<Tuple>();
			result.add(new Tuple(taskIn,null));
			return result;
		}else{
			//	��������˳�λ�����һ����˿��Ը���
			
			//����һ��ʱ������[takeIn,startTime,timeBound]
			//timeBound�ǵ�һ��������������������󷵻�
			//��ڵ�ʱ��
			int timeBound = router.hops(map.allSpaces.get(ds.parkingSpaceID).location, 
					map.in) * 2 + ds.delay + realStartTime  ;
			//int nTaskOut = taskQ2.size();
			
			//��ʱ�������ڵ��������ŵ�taskQ1
			//һ��ִ�е����������Ŀ���ᳬ���ܵĻ�������Ŀ
			while(!pullInTasks.isEmpty() && taskIn.startTime < timeBound 
					&&taskQ1.size() < robots.size()){
				taskQ1.add(taskIn);
				taskIn = pullInTasks.remove(0);
			}
			
			List<Tuple> result = new ArrayList<Tuple>();
			
			//һ����ָ�ɵĻ�������Ŀ���Ͻ�
			int robotsBound = Math.min(
					taskQ2.size() + taskQ1.size(),robots.size());
			int taskQ1Index = 0;
			//boolean interruption = false;
			int refusalCount = 0;
			boolean refuse ;
			while(taskQ1Index < taskQ1.size() ){
				refuse = true;
				if(taskQ1Index ==  taskQ1.size() - 1){
					//��¼�ж��ٸ������˸ϵ����������е�
					//���һ������
					if(robots.peek().completementTime > 
						taskQ1.get(taskQ1Index).startTime +
							applications[taskQ1.get(taskQ1Index).carID].longestWatingTime)
								result.add(new Tuple(taskQ1.get(taskQ1Index),null));
					
					while( robotsQ.size() <= robotsBound
							&&robots.peek().completementTime <= 
								taskQ1.get(taskQ1Index).startTime +
									applications[taskQ1.get(taskQ1Index).carID].longestWatingTime){
						Robot exeRobot = robots.remove();
						robotsQ.add(exeRobot);
						if(refuse)
							//ֻ�ڵ�һ�ν���ʱ���
							result.add(new Tuple(taskQ1.get(taskQ1Index),exeRobot));
						refuse = false;

					}					
				}else{	//û��������������е����һ��
					if(robots.peek().completementTime > 
							taskQ1.get(taskQ1Index).startTime +
							applications[taskQ1.get(taskQ1Index).carID].longestWatingTime)
						result.add(new Tuple(taskQ1.get(taskQ1Index),null));
					else if(!robots.isEmpty()){
						Robot exeRobot = robots.remove();
						robotsQ.add(exeRobot);	
						refuse = false;
						result.add(new Tuple(taskQ1.get(taskQ1Index),exeRobot));
					}
				}
				
				if(refuse)
					refusalCount++;
				taskQ1Index++;
			}

			
			int robotsOffset = robotsQ.size() - taskQ1.size() + refusalCount;
			if(robotsOffset == 0){
				//���ÿ�������˶����������
				return result;
			}
			
			
			//�еĻ����˻�û������
			List<Task> solution = taskQ1;
			for(int i = robotsOffset;i > 0  && !taskQ2.isEmpty();i --){
				//�����������
				solution.add(taskQ2.remove(0));
			}
			
			//��������
			
		}
			
		return null;
	}
	
	private int lastRealStartTimeIn = 0;
	private int lastRST = 0;
	
	public class TabuSearch{
		private List<Task> solution;
		private List<Robot> robots;
		
		private int[][] tabuTable;
		
		private int bestCost ;
		private List<Task> bestSolution;
		private int iterationCount = 0;
				
		public TabuSearch(List<Task> initSolution,List<Robot> robots){
			solution = initSolution;
			this.robots = robots;
			tabuTable = new int[initSolution.size()][initSolution.size()];
		}
		
		public List<Task> solve(){
			iterationCount = 0;
			bestCost = evaluate(solution);
			bestSolution = solution;
			
			while(!stop()){
				
			}
			
			
			return bestSolution;
		}
		
		
		public int evaluate(List<Task> solution){
			int cost = 0;
			int taskIndex = 0,robotIndex = 0; 
			
			dispatcher.restore(lastRST);
			
			while(taskIndex < solution.size()){
				//��˳����ÿ������
				Task task = solution.get(taskIndex);
				Robot robot = robots.get(robotIndex);
				if(task.taskType == Task.PULL_IN){//�������
					if(robot.completementTime > task.startTime 
						+ applications[task.carID].longestWatingTime){
						//�����˸ϲ���
						cost += fPanishment;
					}else{
						int realStartTime =
								Math.max(robot.completementTime,task.startTime);
						DispatchState ds = dispatcher.parkingSpaceDispatch(applications, task.carID, realStartTime);
						if(!ds.success || realStartTime+ds.delay > task.startTime + applications[task.carID].longestWatingTime)
							//��Ϊ�ȴ���λ���ӳ٣����»����˸ϲ�������
							cost += fPanishment;
						else{//�����˸ϵ���
							
							//ע���������ȴ���
							realStartTime = Math.max(realStartTime+ds.delay, lastRealStartTimeIn);
							cost += fWating * (realStartTime - task.startTime);
							robotIndex++;
						}
					}			
				}else if(task.taskType == Task.PULL_OUT){//��������
					int realStartTime = Math.max(robot.completementTime,task.startTime);
					cost += fWating * (realStartTime - task.startTime );
					robotIndex++;																			
				}else
					throw new IllegalStateException("ֻ�������ͳ�������!");
				taskIndex++;
			}
			
			return cost;
		}
		
		public boolean stop(){
			return iterationCount == 1000;
		}
		
		public int bestNeighbor(Point neighborLoc){
			
			
			int i = getNextPullOutTask( 0,solution);
			while(i > 0)
				for(int j = 0;j < tabuTable.length;j ++)
					if(tabuTable[i][j] == 0){
						
					}
						
			
			return 0;
		}
		
		public int aspiration(int bestNeighborCost,Point finalBestNeighbor){
			return 0;
		}
		
		private int getNextPullOutTask(int start,List<Task> solution){
			java.util.ListIterator<Task> iter = solution.listIterator(start);
			int pullOutIndex = -1;
			while(iter.hasNext() && pullOutIndex == -1){
				
				if(iter.next().taskType == Task.PULL_OUT)
					pullOutIndex = iter.previousIndex();
			}
			return pullOutIndex;
		}
		
		private List<Task> neighbor(List<Task> solution,int i,int j){
			Task temp = solution.get(i);
			solution.set(i, solution.get(j));
			solution.set(j, temp);
			
			return solution;
		}
	}
	
	protected static class Tuple{
		public Task t;
		public Robot r ;
		public Tuple(Task t,Robot r){
			this.t = t;
			this.r = r;
		}
	}
}
