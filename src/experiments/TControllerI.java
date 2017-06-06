/**   
* @Title: TControllerI.java 
* @Package experiments 
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2017-06-04
* @version V1.0   
*/
package experiments;

import models.Map;

import models.Router;
import parkingScheme.DispatcherII;
import parkingScheme.SpaceDispatcher;
import schedulingScheme.RobotsControllerI;
import schedulingScheme.Scheduling;
import utils.Calculator;
import utils.Initiation;

/**
* <p> TControllerI</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2017-06-04
*/
public class TControllerI {

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

		Scheduling controller = new RobotsControllerI(map,router,parkingLotDispatcher,data.fEnergy,data.fPanishment,data.fRobots,data.fWaiting,data.applications);
		
		int nRobots = 3;
		
		System.out.println("使用机器人数目为： " + nRobots);
		
		//调度
		int jointCost = controller.scheduling(nRobots);
		
		System.out.println("***结果***");
		System.out.println("经计算，最后的等待代价与拒载代价之和为：" +jointCost );
		System.out.println("机器人代价加上等待代价嘉盛拒载代价之和为："+ (jointCost +data.fRobots * nRobots));
		System.out.println("所有代价之和为：" +  (jointCost +data.fRobots * nRobots + Calculator.calcW(data.applications, data.fEnergy, map, router)));
	}

}
