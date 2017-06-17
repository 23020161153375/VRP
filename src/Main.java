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
* @Description: TODO(��һ�仰�������ļ���ʲô) 
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
		
		System.out.println("***����������***");
		data.init(new Scanner(new File("E:/HKVison/20170524/1.txt")));
		
		System.out.println("***���Կ�ʼ***");
		//������ͼ
		Map map = new Map(data.map, data.routingIn, data.routingOut);
		
		//��ӡ��ͼ
		map.print();
		
		//���õ�ͼ����һ��·����
		Router router = new Router(map, data.routingIn, data.routingOut);

		//ʹ��2��ͣ��λ���ȷ���������������֣�
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
		//����
		
		long startTime = System.currentTimeMillis();
		lastJointCost = controller.scheduling(nRobots );
		double totalTime = (System.currentTimeMillis() - startTime) / 1000;
		Calculator calculator = new Calculator(data);
		
		IOHelper.out2file(nRobots, lastJointCost, Calculator.calcW(data.applications, data.fEnergy, map, router), data.applications, router, map, "E:/HKVison/20170524/check/100result.txt");
		System.out.println("***���***");
		System.out.println("��ͼ��С��" + map.map.length + " * " + map.map[0].length);
		System.out.println("���������Ŀ��" + data.applications.length);
		System.out.println("���ѵ�ʱ��(s)��" + totalTime);
		System.out.println("ʹ�û�������ĿΪ�� " + (nRobots ));
		System.out.println("�����˴��ۼ��ϵȴ����ۼ��Ͼ��ش���֮��Ϊ��"+ (lastJointCost ));
		int sumCost = lastJointCost + Calculator.calcW(data.applications, data.fEnergy, map, router);
		System.out.println("���д���֮��Ϊ��" +  sumCost);
		System.out.println("���أ�" +calculator.nRefused()+".����ռ�ܴ��۱��أ�"+(1.0 * data.fPanishment * calculator.nRefused() / sumCost ));
		System.out.println("����ܵȴ�ʱ�䣺" + calculator.watingTimeIn() + ".����ռ�ܴ��۱��أ�"+(1.0 * data.fWaiting * calculator.watingTimeIn() / sumCost ));
		System.out.println("�����ܵȴ�ʱ�䣺" + calculator.watingTimeOut() + ".����ռ�ܴ��۱��أ�"+(1.0 * data.fWaiting * calculator.watingTimeOut() / sumCost) );	
	}

}
