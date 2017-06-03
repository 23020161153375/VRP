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
			List<Tuple> select = taskSelection();
			
			for(Tuple tuple : select){
				if(tuple.r == null){
					applications[tuple.t.carID].refused = true;
				}else if(tuple.t.taskType == Task.PULL_IN){
					applications[tuple.t.carID].refused = false;
					applications[tuple.t.carID].task1 = tuple.t;
					
					//������Ӧ�ĳ�������
					int startTime = 
							applications[tuple.t.carID].pullOutTime 
							- router.hops(map.out, map.allSpaces.get(tuple.t.parkingSpaceID).location);
					Task out = new Task(tuple.t.carID, startTime, Task.PULL_OUT, tuple.t.parkingSpaceID);
					
					//���´���������ʱ��
					out.updateKey(startTime 
							- router.hops(map.in, map.allSpaces.get(tuple.t.parkingSpaceID).location) );
					pullOutTasks.add(out);
					
					//��¼
					applications[tuple.t.carID].task2 = out;
										
					//������ִ�и����񲢷������
					tuple.r.completementTime = tuple.t.realStartTime 
							+ 2 * router.hops(map.in, map.allSpaces.get(tuple.t.parkingSpaceID).location);
					
					tuple.r.location = map.in;
					
					//�����˹��
					robots.add(tuple.r);
					
					//֪ͨͣ��������
					//dispatcher.event(tuple.t);
					
					//ά��ȫ�ֱ���
					lastRealStartTimeIn =  tuple.t.realStartTime;
					lastRST =  tuple.t.realStartTime;
				}else{//����
					tuple.r.completementTime = tuple.t.realStartTime 
							+ router.hops(map.out, map.allSpaces.get(tuple.t.parkingSpaceID).location)
							+ router.hops(map.out, map.in);
					tuple.r.location = map.in;
					
					//�����˹��
					robots.add(tuple.r);
					
					//֪ͨͣ��������
					//dispatcher.event(tuple.t);
					
					//ά��ȫ�ֱ���
					lastRST = tuple.t.realStartTime;
				}
			}				
		}		
		return fRobots * nRobots + utils.Calculator.calcT(applications, fPanishment, fWating);
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
		while(!pullOutTasks.isEmpty() && pullOutTasks.peek().startTimeFromEntrance() <= realStartTime)
			taskQ2.add(pullOutTasks.remove());
		

		if(!(ds = dispatcher.parkingSpaceDispatch(applications, 
				taskIn.carID,realStartTime)).success){
			//���û�г�λ�ɹ�����
			
			//������һ��������
			robot = robots.remove();
			
			//ִ��һ����������
			Task taskOut;			
			if(!taskQ2.isEmpty()){
				//�����ǰ�п���ִ�еĳ�������ѡ���翪ʼ��ȥִ��
				taskOut = taskQ2.remove(0);
			}else{	//���򣬵ȴ���ִ��һ����������
				taskOut = pullOutTasks.remove();
			}
			
			//��¼��ʵ�Ŀ�ʼʱ������ʱ��
			taskOut.realStartTime 
				= Math.max(robot.completementTime + router.hops(map.in,
						map.allSpaces.get(taskOut.parkingSpaceID).location), taskOut.startTime);
			taskOut.realFinishTime = taskOut.realStartTime  + 
					router.hops(map.allSpaces.get(taskOut.parkingSpaceID).location, map.out);
			taskOut.exeRobotID = robot.robotID;
			
			Tuple pullOut = new Tuple(taskOut,robot);
			List<Tuple> result = new ArrayList<Tuple>();
			result.add(pullOut);
			return result;
			
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
			List<Task> solution = new LinkedList<Task>();
			for(Task t :taskQ1)
				solution.add(t);
			
			for(int i = robotsOffset;i > 0  && !taskQ2.isEmpty();i --){
				//�����������
				solution.add(taskQ2.remove(0));
			}
			
			//��������
			TabuSearch ts = new TabuSearch(solution,robotsQ);
			List<Tuple> taskSelecting = ts.solve();
			
			//��¼�������������Ҫ�Ļ�������Ŀ
			int workingRobotsNumber = 0;
			for(Tuple tu : taskSelecting)
				if(tu.r != null)
					workingRobotsNumber ++;
			
			//�����к���û�з�������Ļ��������·Żض���
			while(robotsQ.size() > workingRobotsNumber)
				robots.add(robotsQ.remove(robotsQ.size() - 1));
			
			//��������Ѿ���ɣ��������أ�
			taskQ1.clear();
			
			//û����ɵĳ��������������Ŷ�
			while(!taskQ2.isEmpty())
				pullOutTasks.add(taskQ2.remove(0));
			return taskSelecting;
		}
			
	}
	
	/** 
	* @Fields lastRealStartTimeIn : TODO(�ϴ�ִ���������ʱ����ڳ�����ʱ�䣬�����������ȴ���) 
	*/ 
	private int lastRealStartTimeIn = 0;
	
	/** 
	* @Fields lastRST : TODO(�ϴ�ִ������������⣩��ʱ��㣬�ڶϵ�ִ��ʱ���ڻָ��ֳ�) 
	*/ 
	private int lastRST = 0;
	
	public class TabuSearch{
		private List<Task> solution;
		private List<Robot> robots;
		
		/** 
		* @Fields tabuTable : TODO(tabuTable[i][j] ��ʾ��iλ���ϵ�Ԫ��ȡ�������뵽jλ���ϣ�ע���е���ĿҪ��������1��) 
		*/ 
		private int[][] tabuTable;
		
		private int bestCost ;
		private List<Task> bestSolution;
		private int iterationCount = 0;
			
		private int localLastRealStartTimeIn;
		
		public TabuSearch(List<Task> initSolution,List<Robot> robots){
			solution = initSolution;
			this.robots = robots;
			
			tabuTable = new int[initSolution.size()][initSolution.size() + 1];
			localLastRealStartTimeIn = lastRealStartTimeIn;
		}
		
		public List<Tuple> solve(){
			iterationCount = 0;
			bestCost = evaluate(solution);
			bestSolution = new ArrayList<Task>(solution);
			
			while(!stop()){
				Point neighborLoc = null ;
				int bestNCost = bestNeighbor(neighborLoc);
				Point finalNeighborLoc = null;
				int cost = aspiration(bestNCost,neighborLoc,finalNeighborLoc);
				
				solution = neighbor(solution,finalNeighborLoc.x,finalNeighborLoc.y);
				
				if(cost < bestCost ){
					bestCost = cost;
					bestSolution = new ArrayList<Task>(solution);
				}
				for(int i = 0;i < tabuTable.length;i ++)
					for(int j = 0;j < tabuTable[0].length;j ++)
						if(tabuTable[i][j] > 0)
							tabuTable[i][j] --;
				
				tabuTable[finalNeighborLoc.x][finalNeighborLoc.y] = 5;
				iterationCount++;
			}
					
			return robotsMatching(bestSolution);
		}
		
		public int evaluate(List<Task> solution){
			int cost = 0;
			int taskIndex = 0,robotIndex = 0; 
			
			dispatcher.restore(lastRST);
			localLastRealStartTimeIn = lastRealStartTimeIn;
			
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
							realStartTime = Math.max(realStartTime+ds.delay, localLastRealStartTimeIn);
							
							//��¼
							task.realStartTime = realStartTime;
							
							if(realStartTime > task.startTime)
								cost += fWating * (realStartTime - task.startTime);
							robotIndex++;
							
							//֪ͨ
							dispatcher.event(task);
							
							localLastRealStartTimeIn = realStartTime;
						}
					}			
				}else if(task.taskType == Task.PULL_OUT){//��������
					int realStartTimeFromEntrance = Math.max(robot.completementTime,task.startTimeFromEntrance());
					
					task.realStartTime = realStartTimeFromEntrance + router.hops(map.in,map.allSpaces.get(task.parkingSpaceID).location);
					
					if(realStartTimeFromEntrance >  task.startTimeFromEntrance())
						cost += fWating * (realStartTimeFromEntrance - task.startTimeFromEntrance() );
					robotIndex++;
					
					//֪ͨ
					dispatcher.event(task);
				}else
					throw new IllegalStateException("ֻ�������ͳ�������!");
				taskIndex++;
			}
			
			return cost;
		}
		
		public List<Tuple> robotsMatching(List<Task> solution){
			List<Tuple> robotsMatching = new ArrayList<Tuple>();
			
			//int cost = 0;
			int taskIndex = 0,robotIndex = 0; 
			
			dispatcher.restore(lastRST);
			localLastRealStartTimeIn = lastRealStartTimeIn;
			
			while(taskIndex < solution.size()){
				//��˳����ÿ������
				Task task = solution.get(taskIndex);
				Robot robot = robots.get(robotIndex);
				if(task.taskType == Task.PULL_IN){//�������
					if(robot.completementTime > task.startTime 
						+ applications[task.carID].longestWatingTime){
						//�����˸ϲ���
						//cost += fPanishment;
						robotsMatching.add(new Tuple(task,null));
					}else{
						//һ����������֮��ʼ��
						int realStartTime =
								Math.max(robot.completementTime,task.startTime);
						DispatchState ds = dispatcher.parkingSpaceDispatch(applications, task.carID, realStartTime);
						if(!ds.success || realStartTime+ds.delay > task.startTime + applications[task.carID].longestWatingTime){
							//��Ϊû�г�λ�����ߵȴ���λ�������ӳ٣����»����˸ϲ�������
							//cost += fPanishment;
							robotsMatching.add(new Tuple(task,null));						
						}else{//�����˸ϵ���
							
							//ע���������ȴ���
							realStartTime = Math.max(realStartTime+ds.delay, localLastRealStartTimeIn);
							
							task.realStartTime = realStartTime;
							task.realFinishTime =
									realStartTime + router.hops(map.in, map.allSpaces.get(ds.parkingSpaceID).location);
							task.parkingSpaceID = ds.parkingSpaceID;
							task.exeRobotID = robot.robotID;
							dispatcher.event(task);
							
							robotsMatching.add(new Tuple(task,robot));
							robotIndex++;
							localLastRealStartTimeIn = realStartTime;
						}
					}			
				}else if(task.taskType == Task.PULL_OUT){//��������
					int realStartTime = Math.max(	//ͣ���������֪
							robot.completementTime,task.startTimeFromEntrance()) 
							+ router.hops(map.in, map.allSpaces.get(task.parkingSpaceID).location);
					
					task.realStartTime = realStartTime;
					task.realFinishTime = 
							realStartTime + router.hops(map.allSpaces.get(task.parkingSpaceID).location, map.out);
					
					//ͣ���������֪
					
					task.exeRobotID = robot.robotID;
					//֪ͨ
					dispatcher.event(task);
					
					robotsMatching.add(new Tuple(task,robot));
					robotIndex++;																			
				}else
					throw new IllegalStateException("ֻ�������ͳ�������!");
				taskIndex++;
			}
			
			return robotsMatching;
		}
		
		public boolean stop(){
			return iterationCount == 1000;
		}
		
		public int bestNeighbor(Point neighborLoc){
			List<Task> neighbor = new LinkedList<Task>();	
			for(Task t : solution)
				neighbor.add(t);
			neighborLoc = new Point(-1,-1);
			int bestNeighborCost = Integer.MAX_VALUE;
			
			int i = getNextPullOutTask( 0,solution);
			if(i < 0)
				throw new IllegalStateException("û���ھ�");
			
			int currentCost;
			while(i > 0){
				for(int j = 0;j < tabuTable.length;j ++)
					if(tabuTable[i][j] == 0 && i != j){
						neighbor = neighbor(neighbor,i,j);
						if((currentCost = evaluate(neighbor)) < bestNeighborCost){
							bestNeighborCost = currentCost;
							neighborLoc.x = i;
							neighborLoc.y = j;
						}
						
						if(i < j)
							neighbor = neighbor(neighbor,j-1,i);
						else
							neighbor = neighbor(neighbor,j,i+1);
					}
				i = getNextPullOutTask( 0,solution);
			}
			return bestNeighborCost;
		}
		
		public int aspiration(int bestNeighborCost,Point bestUntabuNLoc, Point finalBestNeighbor){
			finalBestNeighbor = new Point(bestUntabuNLoc.x,bestUntabuNLoc.y);
			List<Task> neighbor = new LinkedList<Task>();	
			for(Task t : solution)
				neighbor.add(t);
			
			int i = getNextPullOutTask( 0,solution);
			if(i < 0)
				throw new IllegalStateException("û���ھ�");
			int bestCost = Math.min(bestNeighborCost, this.bestCost);	
			int currentCost;
			while(i > 0){
				for(int j = 0;j < tabuTable.length;j ++)
					if(tabuTable[i][j] > 0 && i != j){
						neighbor = neighbor(neighbor,i,j);
						if((currentCost = evaluate(neighbor)) < bestCost){
							bestCost = currentCost;
							finalBestNeighbor.x = i;
							finalBestNeighbor.y = j;
						}
						
						if(i < j)
							neighbor = neighbor(neighbor,j-1,i);
						else
							neighbor = neighbor(neighbor,j,i+1);
					}
				i = getNextPullOutTask( 0,solution);
			}
			
			if(bestUntabuNLoc.equals(finalBestNeighbor))
				return bestNeighborCost;
			else
				return bestCost;
		}
		
		private int getNextPullOutTask(int start,List<Task> solution){
			if(start < 0 || start >= solution.size())
				return -1;
			java.util.ListIterator<Task> iter = solution.listIterator(start);
			int pullOutIndex = -1;
			while(iter.hasNext() && pullOutIndex == -1){
				
				if(iter.next().taskType == Task.PULL_OUT)
					pullOutIndex = iter.previousIndex();
			}
			return pullOutIndex;
		}
		
		/** 
		* <p>��ԭ��û����ϣ��� i λ���ϵ�Ԫ�ز��뵽 j λ���ϣ��õ�һ���½�</p> 
		* <p>Description: </p> 
		* @param solution
		* @param i
		* @param j
		* @return 
		*/
		private List<Task> neighbor(List<Task> solution,int i,int j){
			Task temp = solution.get(i);
			solution.add(j,temp);
			if(i < j)
				 solution.remove(i);
			else//��ʱiλ���϶�Ӧ��Ԫ���Ѿ�������һλ
				solution.remove(i+1);
		
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
