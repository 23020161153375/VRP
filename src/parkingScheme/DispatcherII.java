/**   
* @Title: ShortestPathFirst.java 
* @Package parkingScheme 
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2017-05-18
* @version V1.0   
*/
package parkingScheme;

import models.DispatchState;
import models.Map;
import models.ParkingLotManager;
import models.ParkingSpace;
import models.Task;
import models.VRP;
import routing.Routing;

/**
* <p> 最短路径优先策略</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2017-05-18
*/
public class DispatcherII extends ParkingLotManager {

	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param parkingLot
	* @param router 
	*/
	int b=5,k=1;
	int inf=1000000000;
	int p=inf;
	public DispatcherII(Map parkingLot, Routing router,int fWating,int fPanishment, int fEnergy) {
		super(parkingLot, router);
		// TODO Auto-generated constructor stub
		b = fWating;
		p = fPanishment;
		k = fEnergy;
	}


	/** (non-Javadoc)
	 * @see parkingScheme.SpaceDispatcher#parkingSpaceDispatch(models.VRP[], int, int)
	 */
	@Override
	public DispatchState parkingSpaceDispatch(VRP[] vrps, int id, int readyTime) {
		if(readyTime < this.time)
			throw new IllegalStateException("先申请入库的先处理。");
		else if(readyTime > this.time)//时钟调整
			stateChangeClockwise(readyTime);
		//else
			//this.printCurrentStateOfParkingLot();
		
		//将当前距离最短的一个可行的车位调度出来
		//int fessibleSpace = -1;
		
		//用于筛选
		int cost=0,mini=inf;
		int delay=0;
		
		//结果
		int choose=0;
		int chooseDelay = -1;
		for(int i = 0;i < parkingLot.allSpaces.size() ;i ++){
			ParkingSpace space = parkingLot.allSpaces.get(i);
			if(space.empty && space.firstPlanningEvent() == null) {
				//车位为空，且还没来得及安排车辆
				delay = 0;
			}else if(!space.empty){
				//车位有车但马上就要出库了
				Task pevent=null;
				if(space.firstPlanningEvent() != null) {//出库事件什么时候添加的？（在这次申请之前）
					pevent = space.firstPlanningEvent();
					if (pevent.realStartTime < readyTime + router.hops(parkingLot.in, space.location))
						//无需等待
						delay = 0;
						//否则再看看有其他的现成的空车位没有
					else {
						//要等一会儿
						delay = 1 + space.firstPlanningEvent().realStartTime - readyTime - router.hops(parkingLot.in, space.location);
					}
				}
				else{//没有添加出库事件
					delay=inf;
				}
			}
			else{
				//有安排的车位，暂不考虑
				delay=inf;
			}

			int dis=router.hops(parkingLot.in, space.location)+router.hops(parkingLot.out, space.location);
			
			int m = vrps[id].carMass;
			cost=b*delay+k*m*dis;
			if(cost<mini){
				mini=cost;
				choose=i;
				
				//保存时延
				chooseDelay = delay;
			}
		}
		//并不是划得来才入库，是有空闲必须入库
		/*if(mini<inf){
			fessibleSpace=choose;
		}*/
		/*System.out.println("分配车位"+fessibleSpace);
		System.out.println("车位编号"+fessibleSpace);
		System.out.println("车位坐标"+parkingLot.allSpaces.get(fessibleSpace).location.x+" "+parkingLot.allSpaces.get(fessibleSpace).location.y);
		System.out.println("距离"+parkingLot.allSpaces.get(fessibleSpace).key);*/
		
		if(mini < inf){
			return new DispatchState(true,choose,chooseDelay);
		}
		
		//无车位可供调度（需要先出库才行）
		return new DispatchState(false,-1,-1);
	}
}
