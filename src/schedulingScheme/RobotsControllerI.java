/**   
* @Title: RobotsControllerI.java 
* @Package schedulingScheme 
* @Description: TODO(用一句话描述该文件做什么) 
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
		
		//初始化机器人队列
		this.createRobotsSet(nRobots);
		
		//初始化任务列表
		this.createTaskList();
				
		//调试用
		int taskSelectionCount = 0;
		while(!pullInTasks.isEmpty() || !pullOutTasks.isEmpty()){
			//只要还有入库任务或者出库任务没有处理
			taskSelectionCount ++;
			System.out.println("***第" + taskSelectionCount + "次任务选择***");
			//派出机器人选择一些任务完成
			List<Tuple> select = taskSelection();
			
			System.out.println("***第" + taskSelectionCount+"选择完成。开始调度***");
			

			dispatcher.restore(lastRequestTime,undoEvents);
			
			//调试用
			int taskCount = 0;
			for(Tuple tuple : select){
				taskCount++;
				System.out.println("\n0. 第" +taskCount+"个任务");
				if(tuple.r == null){
					System.out.println("\n1.1. 原计划拒载，检查是否有可调动的机器人");
					Robot robot = robots.peek();
					//决定是否入库
					if(robot.completementTime <= tuple.t.startTime + applications[tuple.t.carID].longestWatingTime){
						DispatchState ds = dispatcher.parkingSpaceDispatch(applications, tuple.t.carID, tuple.t.startTime); 
						
						//就绪时间
						int readyTime = Math.max(tuple.t.startTime + ds.delay, robot.completementTime);
						
						//等前面完成入库
						if(readyTime < lastRealStartTimeIn)
							readyTime = lastRealStartTimeIn;
						
						if(ds.success && readyTime <= 
								tuple.t.startTime + applications[tuple.t.carID].longestWatingTime){
							//调度出该机器人
							tuple.r = robots.remove();

							//System.out.println("\n2.1"+robot.robotID+"号机器人可以完成该任务，车辆将进入" + ds.parkingSpaceID +"号停车位");
							pullIn(tuple, ds);
							System.out.println("\n2.1. 车辆" + tuple.t.carID+"在"+tuple.t.realFinishTime+"时刻由机器人"+tuple.t.exeRobotID+"送入" +ds.parkingSpaceID+"号车位。");
							continue;
						}
					}
					
					System.out.println("\n 2.2 没有机器人可以完成该任务，车辆" + tuple.t.carID+"被拒载。");
					//否则拒载
					applications[tuple.t.carID].refused = true;
				}else if(tuple.t.taskType == Task.PULL_IN){
					System.out.println("\n1.2. 原计划入库，检查是否还有停车位");
					//机器人响应时间

					
					DispatchState ds = dispatcher.parkingSpaceDispatch(applications, tuple.t.carID, tuple.t.startTime); 
					int readyTime = Math.max(tuple.t.startTime + ds.delay, tuple.r.completementTime);
					if(readyTime < lastRealStartTimeIn)
						readyTime = lastRealStartTimeIn;
					if(!ds.success|| readyTime  > tuple.t.startTime + applications[tuple.t.carID].longestWatingTime){
						//没车位或赶不上，则放弃（以后可以改进）
						
						if(!ds.success)
							System.out.println("\n2.1 没有车位，车辆" + tuple.t.carID+"放弃入库");
						else
							System.out.println("\n2.2 等待时间" + (readyTime   - tuple.t.startTime)+"过长，超过" 
									+ applications[tuple.t.carID].longestWatingTime+"限制。车辆" + tuple.t.carID+"放弃停车。");
						
						applications[tuple.t.carID].refused = true;
						
						//机器人入队
						robots.add(tuple.r);
						continue;
					}
					//否则入库
					pullIn(tuple, ds);
					System.out.println("\n2.3. 车辆" + tuple.t.carID+"在"+tuple.t.realFinishTime+"时刻由机器人"+tuple.t.exeRobotID+"送入" +ds.parkingSpaceID+"号车位。");
				}else{//出库
					pullOut(tuple);
					System.out.println("\n1.3 车辆"+ tuple.t.carID + "在"+tuple.t.realStartTime+"时刻由机器人"+tuple.t.exeRobotID +"送出"+ tuple.t.parkingSpaceID+"号车位。");
				}
			}
			
			undoEvents = 0;
		}
		
		//恢复到最开始状态
		dispatcher.restore(-1, -1);
		robotsQ.clear();
		taskQ1.clear();
		taskQ2.clear();
		undoEvents = 0;
		lastRealStartTimeIn = 0;
		lastRequestTime = 0;

		return  utils.Calculator.calcT(applications, fPanishment, fWating);
	}

	private void pullOut(Tuple tuple){
		//事件标记
		//实际开始时间应该是机器人实际从起点出发的时间加上从起点到车位所花的时间
		tuple.t.realStartTime = Math.max(tuple.t.startTimeFromEntrance(), tuple.r.completementTime) 
					+ router.hops(map.in, map.allSpaces.get(tuple.t.parkingSpaceID).location);
		tuple.t.realFinishTime = tuple.t.realStartTime 
				+ router.hops(map.out, map.allSpaces.get(tuple.t.parkingSpaceID).location);
		tuple.t.exeRobotID = tuple.r.robotID;
		
		//机器人执行任务并返回起点
		tuple.r.completementTime = tuple.t.realFinishTime
				+ router.hops(map.out, map.in);
		tuple.r.location = map.in;
		
		//机器人归队
		robots.add(tuple.r);
		
		//通知停车场方面
		dispatcher.event(tuple.t);
		
		//维护全局变量
		//lastRST = tuple.t.realStartTime;		
	}
	
	private void pullIn(Tuple tuple,DispatchState ds){
		//机器人响应时间
		//注意给响应时间设置了下界
		int readyTime = Math.max(tuple.t.startTime + ds.delay, tuple.r.completementTime);
		if(readyTime < lastRealStartTimeIn)
			readyTime = lastRealStartTimeIn;
		
		//记录入库事件
		applications[tuple.t.carID].refused = false;
		applications[tuple.t.carID].task1 = tuple.t;
		
		//标记入库事件
		tuple.t.realStartTime  = readyTime; 
		tuple.t.realFinishTime = tuple.t.realStartTime + router.hops(map.in, map.allSpaces.get(ds.parkingSpaceID).location);
		tuple.t.parkingSpaceID = ds.parkingSpaceID;
		tuple.t.exeRobotID = tuple.r.robotID;
		
		//创建对应的出库任务
		int startTime = 
				applications[tuple.t.carID].pullOutTime 
				- router.hops(map.out, map.allSpaces.get(tuple.t.parkingSpaceID).location);
		Task out = new Task(tuple.t.carID, startTime, Task.PULL_OUT, tuple.t.parkingSpaceID);
		
		//更新从起点出发的时间（排序）并添加
		out.updateKey(startTime 
				- router.hops(map.in, map.allSpaces.get(tuple.t.parkingSpaceID).location) );
		pullOutTasks.add(out);
		
		//记录出库事件
		applications[tuple.t.carID].task2 = out;
							
		//机器人执行该任务并返回起点
		tuple.r.completementTime = tuple.t.realStartTime 
				+ 2 * router.hops(map.in, map.allSpaces.get(tuple.t.parkingSpaceID).location);					
		tuple.r.location = map.in;
		
		//机器人归队
		robots.add(tuple.r);
		
		//通知停车场方面
		dispatcher.event(tuple.t);
		
		//维护全局变量
		lastRealStartTimeIn =  tuple.t.realStartTime;
		lastRequestTime = tuple.t.startTime;
		//lastRST =  tuple.t.realStartTime;		
	}
	
	private List<Robot> robotsQ = new LinkedList<Robot>();
	private List<Task> taskQ1 = new ArrayList<Task>();
	private List<Task> taskQ2 = new LinkedList<Task>();
	
	/** 
	* <p>返回一个选择的任务列表</p> 
	* <p>每个任务（入库/出库）会指定一个机器人，有的入库任务没有指定机器人，这表明拒载。 </p> 
	* <p>与任务有关的信息已经保存在相应位置，最后的调度结果也通知了停车场方面，所以在调用结束后还需要更新机器人信息。</p>
	* @return 选择完成的任务列表
	*/
	protected List<Tuple> taskSelection(){
		if(pullInTasks.isEmpty()){
			//如果没有入库任务，执行出库任务
			
			List<Tuple> result = new ArrayList<Tuple>();
			Tuple tuple = new Tuple(pullOutTasks.remove(),robots.remove());
			result.add(tuple);
			
			
			//System.out.println("1.1 没有入库任务，执行出库");
			//System.out.println("申请车辆"+tuple.t.carID +"离开车位"+tuple.t.parkingSpaceID
			//		+"，执行机器人: "+tuple.r.robotID+"\n");
			return result;
		}
		
		//先查看接下来的第一个入库任务
		Task taskIn = this.pullInTasks.get(0);
		
		//记录当前实际可以执行的所有的出库任务，放到taskQ2中
		//以后考虑加上一个小的超前量，以减少出库等待的时间		
		while(!pullOutTasks.isEmpty() && pullOutTasks.peek().startTimeFromEntrance() <= taskIn.startTime + 50)
			taskQ2.add(pullOutTasks.remove());
		
		//查看第一个空闲机器人（注意还未调出）
		Robot robot = robots.peek();
		DispatchState ds ;
		
		//System.out.println("\n1.2 车辆"+taskIn.carID+"申请时间为"+taskIn.startTime
		//		+"机器人"+robot.robotID+"最快响应"+",响应时间" + robot.completementTime);
		
		//System.out.println("\n2. 尝试申请车位");
		if(!(ds = dispatcher.parkingSpaceDispatch(applications, 
				taskIn.carID,taskIn.startTime)).success){
			//如果没有车位可供分配
					
			//调出第一个机器人
			robot = robots.remove();
			
			//执行一个出库任务
			Task taskOut;			
			if(!taskQ2.isEmpty()){
				//如果当前有可以执行的出库任务，选最早开始的去执行
				taskOut = taskQ2.remove(0);
			}else{	//否则，等待并执行一个出库任务
				taskOut = pullOutTasks.remove();
			}
			
			/*
			System.out.println("2.1. 车位分配不成功，执行一个出库任务...");
			System.out.println("申请车辆"+taskOut.carID +"离开车位"+taskOut.parkingSpaceID
					+"时间为"+taskOut.realStartTime+"，执行机器人: "+robot.robotID+"\n");
			 */
			Tuple pullOut = new Tuple(taskOut,robot);
			List<Tuple> result = new ArrayList<Tuple>();
			result.add(pullOut);
			return result;
			
		}else if(robot.completementTime >
			taskIn.startTime + this.applications[taskIn.carID].longestWatingTime){
			//	如果分配了车位，但机器人赶不上，拒载
			/*
			System.out.println("2.2. 机器人实际上赶不上该入库任务，机器人完成时间"
					+robot.completementTime +"车辆申请入库时间"+taskIn.startTime
					+"，最长等待啥时间" +  this.applications[taskIn.carID].longestWatingTime);
			*/
			List<Tuple> result = new ArrayList<Tuple>();
			result.add(new Tuple(taskIn,null));
			return result;
		}else{
			//System.out.println("2.3. 机器人有希望完成该入库任务");
			//	如果分配了车位，并且机器人可以赶上
			int readyTime = Math.max(taskIn.startTime + ds.delay, robot.completementTime);
			if(readyTime < lastRealStartTimeIn)
				readyTime = lastRealStartTimeIn;
			

			//定义一个时间周期[takeIn,startTime,timeBound]
			//timeBound是第一个机器人在完成入库任务后返回
			//入口的时刻
			int timeBound = router.hops(map.allSpaces.get(ds.parkingSpaceID).location, 
					map.in) * 2 + readyTime;
			
			//将时间周期内的入库任务放到taskQ1
			//一次执行的入库任务数目不会超过总的机器人数目
			while(!pullInTasks.isEmpty() && taskIn.startTime < timeBound 
					&&taskQ1.size() < robots.size()){
				//接任务（前面属于试探阶段）
				taskIn = pullInTasks.remove(0);
				taskQ1.add(taskIn);
			}
			
			//System.out.println("\n3. 计算机器人活动周期：["+readyTime+" , " +timeBound+"]");
			//System.out.print("在这段周期内入库任务数目："+ taskQ1.size());
			
			List<Tuple> result = new ArrayList<Tuple>();
			
			//一次性指派的机器人数目的上界
			int robotsBound = Math.min(
					taskQ2.size() + taskQ1.size(),robots.size());
			
			//当前入库任务指针
			int taskQ1Index = 0;
			
			//拒载的入库任务数目
			int refusalCount = 0;
			
			//该入库任务是否拒载
			boolean refuse;
			while(taskQ1Index < taskQ1.size() ){
				refuse = true;
				if(taskQ1Index ==  taskQ1.size() - 1){
					//记录有多少个机器人赶得上入库队列中的
					//最后一个任务
					if(robots.peek().completementTime > 
						taskQ1.get(taskQ1Index).startTime +
							applications[taskQ1.get(taskQ1Index).carID].longestWatingTime)
						//拒载		
						result.add(new Tuple(taskQ1.get(taskQ1Index),null));
					
					while(robotsQ.size() < robotsBound//robotsQ最多只能有robotsBound个
							&&robots.peek().completementTime <= 
								taskQ1.get(taskQ1Index).startTime +
									applications[taskQ1.get(taskQ1Index).carID].longestWatingTime){
						//在暂时不考虑车位的情况下尽可能添加满足条件的机器人
						Robot exeRobot = robots.remove();
						
						//机器人加入缓冲队列
						robotsQ.add(exeRobot);
						if(refuse)
							//暂定让（也只让）第一个满足的机器人执行该入库任务
							result.add(new Tuple(taskQ1.get(taskQ1Index),exeRobot));
						refuse = false;
					}					
				}else{	//没到输入任务队列中的最后一个
					if(robots.peek().completementTime > 
							taskQ1.get(taskQ1Index).startTime +
							applications[taskQ1.get(taskQ1Index).carID].longestWatingTime)
						//赶不上，拒载
						result.add(new Tuple(taskQ1.get(taskQ1Index),null));
					else if(!robots.isEmpty()){
						Robot exeRobot = robots.remove();
						robotsQ.add(exeRobot);	
						refuse = false;
						
						//暂定可以入库
						result.add(new Tuple(taskQ1.get(taskQ1Index),exeRobot));
					}
				}
				
				if(refuse)
					//拒载数目
					refusalCount++;
				
				//下一个入库任务
				taskQ1Index++;
			}

			/*富余的机器人数目
			 *问题是有没有可能出现robotsOffset < 0，
			 *注意前面限定了选择的入库任务不会比机器人数目多
			 *另一方面，每一个没有拒载的入库任务都意味着有一个机器人可以执行
			 */
			//System.out.println("\n4. 调动的机器人数目为 " + robotsQ.size()+" ，"+"目前拒载的任务数目"+refusalCount);
			int robotsOffset = robotsQ.size() - taskQ1.size() + refusalCount;
			if(robotsOffset == 0){
				//如果每个机器人都有入库任务
				//System.out.println("\n5.1 调动出来的机器人都用于入库任务了。");
				
				/*清理工作*/
				
				//入库任务都已经安排好了（包括拒载）
				taskQ1.clear();
				
				//System.out.println("\n6. 把剩余的未完成的"+ taskQ2.size()+"个在缓冲队列的出库任务重新入总队。");
				
				//没有选上的出库任务需重新排队
				while(!taskQ2.isEmpty())
					pullOutTasks.add(taskQ2.remove(0));
				
				//解散队列
				robotsQ.clear();
				return result;
			}
			
			
			//有的机器人还没有任务
			List<Task> solution = new LinkedList<Task>();
			for(Task t :taskQ1)
				solution.add(t);
			
			for(int i = robotsOffset;i > 0  && !taskQ2.isEmpty();i --){
				//分配出库任务
				solution.add(taskQ2.remove(0));
			}
			/*
			System.out.println("\n5.2 为富余的"+robotsOffset+
					"个机器人分配了" + (solution.size() -taskQ1.size())+"个任务");
			
			System.out.println("\n6. ***禁忌搜索***");
			*/
			//禁忌搜索
			TabuSearch ts = new TabuSearch(solution,robotsQ);			
			List<Tuple> taskSelecting = ts.solve();
			
			//记录完成所有任务需要的机器人数目
			int workingRobotsNumber = 0;
			for(Tuple tu : taskSelecting)
				if(tu.r != null)
					workingRobotsNumber ++;
			/*
			System.out.println("\n7. 在调出的"+robotsQ.size()+"个机器人中，"
					+workingRobotsNumber+"个有任务。（如果有剩余的机器人将重新入队）");
					*/	
			//将队列后面没有分配任务的机器人重新放回堆中（不用改变）
			while(robotsQ.size() > workingRobotsNumber)
				robots.add(robotsQ.remove(robotsQ.size() - 1));
			
			//已经安排任务的机器人由上层调用者更新
			//在这里可以解散队列了
			robotsQ.clear();
			
			//sSystem.out.println("\n8. 把剩余的未完成的"+ taskQ2.size()+"个在缓冲队列的出库任务重新入总队。");
			
			//入库任务都已经安排好了（包括拒载）
			taskQ1.clear();
			
			//没有选上的出库任务需重新排队
			while(!taskQ2.isEmpty())
				pullOutTasks.add(taskQ2.remove(0));
			
			return taskSelecting;
		}
			
	}
	
	private int lastRequestTime;
	
	/** 
	* @Fields lastRealStartTimeIn : TODO(上次执行入库任务时从入口出发的时间，控制先申请先处理) 
	*/ 
	private int lastRealStartTimeIn = 0;
		
	/** 
	* @Fields undoEvents : TODO(回退到几个事件之前) 
	*/ 
	private int undoEvents = 0;
	
	public class TabuSearch{
		private List<Task> solution;
		private List<Robot> robots;
		
		/** 
		* @Fields tabuTable : TODO(tabuTable[i][j] 表示将i位置上的元素取出来插入到j位置上，注意列的数目要比行数多1个) 
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
			//localLastRequestTime = lastRequestTime;
			localLastRealStartTimeIn = lastRealStartTimeIn;
		}
		
		public List<Tuple> solve(){
			iterationCount = 0;
			bestCost = evaluate(solution);
			bestSolution = new ArrayList<Task>(solution);
			
			System.out.println("初始解为");
			this.printSolution(bestSolution, bestCost);
			//调试中
				
			while(!stop()){//没有到终止条件


				Point neighborLoc = bestNeighbor();
				List<Task> solutionCopy = new LinkedList<Task>(solution);
				int bestUntabuCost  = 
						evaluate(neighbor(solutionCopy,neighborLoc.x,neighborLoc.y));
								
				Point finalNeighborLoc= aspiration(bestUntabuCost,neighborLoc);
							
				solutionCopy = new LinkedList<Task>(solution);
				int finalCost = 
						evaluate(neighbor(solutionCopy,finalNeighborLoc.x,finalNeighborLoc.y));
									
				solution = neighbor(solution,finalNeighborLoc.x,finalNeighborLoc.y);
				
				if(finalCost < bestCost ){
					bestCost = finalCost;
					bestSolution = new ArrayList<Task>(solution);
					bestSolutionUnchangedIterations = 0;
				}else
					bestSolutionUnchangedIterations++;
				
				for(int i = 0;i < tabuTable.length;i ++)
					for(int j = 0;j < tabuTable[0].length;j ++)
						if(tabuTable[i][j] > 0)
							tabuTable[i][j] --;
				
				tabuTable[finalNeighborLoc.x][finalNeighborLoc.y] = 5;
				iterationCount++;

				System.out.println("第" + iterationCount+"次，得到的解为：");
				this.printSolution(solution, finalCost);			
			}
			//System.out.println("***搜索结束***");		
			return robotsMatching(bestSolution);
		}
		
		public int evaluate(List<Task> solution){
			int cost = 0;
			int taskIndex = 0,robotIndex = 0; 
			
			dispatcher.restore(lastRequestTime,undoEvents);
			localLastRealStartTimeIn = lastRealStartTimeIn;
			undoEvents = 0;
			
			while(taskIndex < solution.size()){
				//按顺序处理每个任务
				Task task = solution.get(taskIndex);
				Robot robot = robots.get(robotIndex);
				if(task.taskType == Task.PULL_IN){//入库任务
					if(robot.completementTime > task.startTime 
						+ applications[task.carID].longestWatingTime){
						//机器人赶不上
						cost += fPanishment;
					}else{
	
						DispatchState ds = dispatcher.parkingSpaceDispatch(applications, task.carID, task.startTime);
						int readyTime = Math.max(task.startTime + ds.delay, robot.completementTime);
						if(readyTime < localLastRealStartTimeIn)
							readyTime = localLastRealStartTimeIn;						
						if(!ds.success || readyTime > task.startTime + applications[task.carID].longestWatingTime)
							//因为等待车位而延迟，导致机器人赶不上任务
							cost += fPanishment;
						else{//机器人赶得上
														
							//记录
							task.realStartTime = readyTime;
							task.realFinishTime = task.realStartTime + router.hops(map.in, map.allSpaces.get(ds.parkingSpaceID).location);
							task.parkingSpaceID = ds.parkingSpaceID;
							task.exeRobotID = robot.robotID;							
							
							if(task.realStartTime > task.startTime)
								cost += fWating * (task.realStartTime - task.startTime);
							robotIndex++;
							
							//通知
							dispatcher.event(task);
							
							//记录
							localLastRealStartTimeIn = task.realStartTime;
							undoEvents++;
						}
					}			
				}else if(task.taskType == Task.PULL_OUT){//出库任务
					int realStartTimeFromEntrance = Math.max(robot.completementTime,task.startTimeFromEntrance());
					
					task.realStartTime = realStartTimeFromEntrance + router.hops(map.in,map.allSpaces.get(task.parkingSpaceID).location);
					task.realFinishTime = task.realStartTime + router.hops( map.allSpaces.get(task.parkingSpaceID).location,map.out);
					task.exeRobotID = robot.robotID;
					
					if(realStartTimeFromEntrance >  task.startTimeFromEntrance())
						cost += fWating * (realStartTimeFromEntrance - task.startTimeFromEntrance() );
					robotIndex++;
					
					//通知
					dispatcher.event(task);
					
					//记录
					undoEvents++;					
				}else
					throw new IllegalStateException("只考虑入库和出库任务!");
				taskIndex++;
			}
			
			return cost;
		}
		
		public List<Tuple> robotsMatching(List<Task> solution){
			List<Tuple> robotsMatching = new ArrayList<Tuple>();
			
			//int cost = 0;
			int taskIndex = 0,robotIndex = 0; 
			
			//回到搜索前状态
			dispatcher.restore(lastRequestTime,undoEvents);
			
			//开始入库时间下界
			localLastRealStartTimeIn = lastRealStartTimeIn;
			
			//重置
			undoEvents = 0;
			
			while(taskIndex < solution.size()){
				//按顺序处理每个任务
				Task task = solution.get(taskIndex);
				
				Robot robot = robots.get(robotIndex);
				if(task.taskType == Task.PULL_IN){//入库任务
					if(robot.completementTime > task.startTime 
						+ applications[task.carID].longestWatingTime){
						//机器人赶不上
						robotsMatching.add(new Tuple(task,null));
					}else{
						//一定是在申请之后开始的

						DispatchState ds = dispatcher.parkingSpaceDispatch(applications, task.carID, task.startTime);
						int readyTime = Math.max(task.startTime + ds.delay, robot.completementTime);
						if(readyTime < localLastRealStartTimeIn)
							readyTime = localLastRealStartTimeIn;
						
						if(!ds.success || readyTime> task.startTime + applications[task.carID].longestWatingTime){
							//因为没有车位，或者等待车位而发生延迟，导致机器人赶不上任务
							robotsMatching.add(new Tuple(task,null));						
						}else{//机器人赶得上
														
							task.realStartTime = readyTime;
							task.realFinishTime =
									task.realStartTime + router.hops(map.in, map.allSpaces.get(ds.parkingSpaceID).location);
							task.parkingSpaceID = ds.parkingSpaceID;
							//task.exeRobotID = robot.robotID;
						
							//通知
							dispatcher.event(task);
							
							robotsMatching.add(new Tuple(task,robot));
							robotIndex++;
							
							//记录
							localLastRealStartTimeIn = task.realStartTime;
							undoEvents ++;
						}
					}			
				}else if(task.taskType == Task.PULL_OUT){//出库任务
					int realStartTime = Math.max(	//停车场编号已知
							robot.completementTime,task.startTimeFromEntrance()) 
							+ router.hops(map.in, map.allSpaces.get(task.parkingSpaceID).location);
					
					task.realStartTime = realStartTime;
					task.realFinishTime = 
							realStartTime + router.hops(map.allSpaces.get(task.parkingSpaceID).location, map.out);
									
					//task.exeRobotID = robot.robotID;
				
					robotsMatching.add(new Tuple(task,robot));
					robotIndex++;
					
					//通知
					dispatcher.event(task);
					
					//记录
					undoEvents ++;
				}else
					throw new IllegalStateException("只考虑入库和出库任务!");
				taskIndex++;
			}
			
			return robotsMatching;
		}
		
		private int bestSolutionUnchangedIterations = 0;
		public boolean stop(){
			//return bestSolutionUnchangedIterations >= 1;
			return iterationCount >= 5;
		}
		
		/** 
		* <p>找到代价最小的邻解， </p> 
		* <p>禁忌的邻解不在查找范围之内。 </p> 
		* @param neighborLoc 存放位置信息
		* @return 最小的邻解位置
		* @throws IllegalStateException 如果不存在邻解
		*/
		public Point bestNeighbor(){
			//复制
			List<Task> neighbor = new LinkedList<Task>(solution);	

			Point  neighborLoc = new Point(-1,-1);
			int bestNeighborCost = Integer.MAX_VALUE;
			
			int i = getNextPullOutTask( 0,solution);
			if(i < 0)
				throw new IllegalStateException("没有邻居");
			
			int currentCost;
			while(i >= 0){//还有出库任务
				for(int j = 0;j < tabuTable[0].length;j ++)
					//针对所有可能的插入位置
					
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
				i = getNextPullOutTask( i + 1,solution);
			}
			return neighborLoc;
		}
		
		/** 
		* <p>吸引准则 </p> 
		* <p>如果当前某个禁忌邻解要优于到目前为止的最好的解（包括先前找到的最优非禁忌邻解），该禁忌解将被采纳，返回该解代价。否则，仍返回刚才找到的最优非禁忌邻解代价。 </p> 
		* @param bestNeighborCost 当前最优的非禁忌邻解的代价
		* @param bestUntabuNLoc 当前最优的非禁忌邻解的位置
		* @param finalBestNeighbor 输入一个位置引用，以存放最终获取的最优解的位置
		* @return 最终最优邻解代价
		* @throws IllegalStateException 没有邻解
		*/
		public Point aspiration(int bestNeighborCost,Point bestUntabuNLoc){
			Point finalBestNeighbor = new Point(bestUntabuNLoc.x,bestUntabuNLoc.y);
			List<Task> neighbor = new LinkedList<Task>();	
			for(Task t : solution)
				neighbor.add(t);
			
			int i = getNextPullOutTask( 0,solution);
			if(i < 0)
				throw new IllegalStateException("没有邻居");
			int bestCost = Math.min(bestNeighborCost, this.bestCost);	
			int currentCost;
			while(i >= 0){
				for(int j = 0;j < tabuTable[0].length;j ++)
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
				i = getNextPullOutTask( i + 1,solution);
			}
			
			return finalBestNeighbor;
		}
		
		/** 
		* <p>从给定位置（包括）往后查找，返回第一个出库任务的下标。如果没有或给定下标越界返回-1 </p> 
		* <p>Description: </p> 
		* @param start 给定起点
		* @param solution 要查找的解
		* @return 
		*/
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
		* <p>在原解得基础上，将 i 位置上的元素插入到 j 位置上，得到一个新解</p> 
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
			else//此时i位置上对应的元素已经后移了一位
				solution.remove(i+1);
		
			return solution;
		}
		public void printSolution(List<Task> solution,int cost){
			System.out.print("cost:" + cost);
			for(Task t: solution)
				System.out.print("<car"+t.carID+" "+(t.taskType==Task.PULL_IN?"in":"out")+"> ");
			System.out.println();
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
