import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import models.Map;
import models.Router;
import parkingScheme.DispatcherII;
import parkingScheme.SpaceDispatcher;
import schedulingScheme.RobotsControllerI;
import schedulingScheme.Scheduling;
import utils.Calculator;
import utils.IOHelper;
import utils.Initiation;

/**   
* @Title: Main.java 
* @Package  
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2017-06-07
* @version V1.0   
*/

/**
* <p> Main</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2017-06-07
*/
public class Main {

	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param args 
	 * @throws IOException 
	*/
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Initiation data = new Initiation();
		
		System.out.println("***请输入数据***");
		data.init(new Scanner(new File("E:/HKVison/20170524/1.txt")));
		
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
		
		int nRobots = 2,jointCost = Integer.MAX_VALUE,lastJointCost;
		//lastJointCost = controller.scheduling(nRobots);
		
	/*	do{
			lastJointCost = jointCost ;
			nRobots += 10;
			jointCost = controller..cheduling(nRobots) +data.fRobots * nRobots ;
		}while(jointCost <= lastJointCost);
		*/
		//调度
		
		long startTime = System.currentTimeMillis();
		lastJointCost = controller.scheduling(nRobots );
		double totalTime = (System.currentTimeMillis() - startTime) / 1000;
		Calculator calculator = new Calculator(data);
		
		IOHelper.out2file(nRobots, lastJointCost, Calculator.calcW(data.applications, data.fEnergy, map, router), data.applications, router, map, "E:/HKVison/20170524/check/100result.txt");
		System.out.println("***结果***");
		System.out.println("地图大小：" + map.map.length + " * " + map.map[0].length);
		System.out.println("入库申请数目：" + data.applications.length);
		System.out.println("花费的时间(s)：" + totalTime);
		System.out.println("使用机器人数目为： " + (nRobots ));
		System.out.println("机器人代价加上等待代价加上拒载代价之和为："+ (lastJointCost ));
		int sumCost = lastJointCost + Calculator.calcW(data.applications, data.fEnergy, map, router);
		System.out.println("所有代价之和为：" +  sumCost);
		System.out.println("拒载：" +calculator.nRefused()+".代价占总代价比重："+(1.0 * data.fPanishment * calculator.nRefused() / sumCost ));
		System.out.println("入库总等待时间：" + calculator.watingTimeIn() + ".代价占总代价比重："+(1.0 * data.fWaiting * calculator.watingTimeIn() / sumCost ));
		System.out.println("出库总等待时间：" + calculator.watingTimeOut() + ".代价占总代价比重："+(1.0 * data.fWaiting * calculator.watingTimeOut() / sumCost) );	
	}

}
