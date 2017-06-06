/**   
* @Title: TDispactherII.java 
* @Package experiments 
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2017-06-03
* @version V1.0   
*/
package experiments;

import models.DispatchState;
import models.Map;
import models.Router;
import models.Task;
import parkingScheme.DispatcherII;
import parkingScheme.SpaceDispatcher;
import utils.Initiation;

/**
* <p> TDispactherII</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2017-06-03
*/
public class TDispactherII {

	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param args 
	*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Initiation data = new Initiation();
		
		System.out.println("***请输入数据***");
		data.init();
		
		System.out.println("***测试开始***");
		//创建地图
		Map map = new Map(data.map, data.routingIn, data.routingOut);
		
		//打印地图
		map.print();
		
		//利用地图创建一个路由器
		Router router = new Router(map, data.routingIn, data.routingOut);

		//使用2号停车位调度方案（换了这个名字）
		SpaceDispatcher parkingLotDispatcher 
			= new DispatcherII(map,router, data.fWaiting, data.fPanishment, data.fEnergy);
		
		System.out.println("***执行任务***");		
		Task firstPullIn = null,pullIn = null,firstPullOut;
		for(int i = 0;i < data.applications.length ;i ++){
			//最后一个入库任务留了出来
			//假装每个入库任务都有机器人按时发送
						
			int readyTime = data.applications[i].requestTime;
			DispatchState ds = parkingLotDispatcher.parkingSpaceDispatch(data.applications, i, readyTime);
			
			System.out.println("申请车辆"+i+"的申请时间为"+readyTime+"分配结果：" );
			if(ds.success){
				System.out.println("分配成功，车位是" + ds.parkingSpaceID +"延时为" + ds.delay );
				pullIn = new Task(i, data.applications[i].requestTime, Task.PULL_IN, ds.parkingSpaceID);
				pullIn.realStartTime = readyTime + ds.delay;
				pullIn.realFinishTime =  pullIn.realStartTime + router.hops(map.in, map.allSpaces.get(ds.parkingSpaceID).location);
				parkingLotDispatcher.event(pullIn);
			}else
				System.out.println("分配失败");
			System.out.println();
			
			
			
			//找一个申请数大于车位数的图，下面的测试才有意义
			if(i == 0)
				firstPullIn = pullIn;
			else if(i == data.applications.length - 2){
				//在执行最后一辆入库任务前，将第一辆入库的车出库
				int startTime = data.applications[i].requestTime;
				firstPullOut = new Task(firstPullIn.carID,startTime,Task.PULL_OUT,firstPullIn.parkingSpaceID);
				firstPullOut.realStartTime = startTime;
				firstPullOut.realFinishTime 
					= firstPullOut.realStartTime 
						+ router.hops(map.allSpaces.get(firstPullOut.parkingSpaceID).location, map.out);
				parkingLotDispatcher.event(firstPullOut);
				
				System.out.println("申请车辆"+firstPullOut.carID +"离开车位"+firstPullOut.parkingSpaceID+"时间为"+firstPullOut.realStartTime+"\n");
			}				
		}					
		
		System.out.println("***测试恢复功能***");
		System.out.println("回到第二个入库任务刚申请前的状态（也就是与前面显示的第二条停车场状态相同）");
		parkingLotDispatcher.restore(data.applications[1].requestTime);

		
		System.out.println("回到初始状态(所有的车位为空)");
		parkingLotDispatcher.restore(0);

	}

}
