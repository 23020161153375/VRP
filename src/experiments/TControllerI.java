/**   
* @Title: TControllerI.java 
* @Package experiments 
* @Description: TODO(��һ�仰�������ļ���ʲô) 
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
		
		System.out.println("***����������***");
		data.init();
		
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
		
		int nRobots = 3;
		
		System.out.println("ʹ�û�������ĿΪ�� " + nRobots);
		
		//����
		int jointCost = controller.scheduling(nRobots);
		
		System.out.println("***���***");
		System.out.println("�����㣬���ĵȴ���������ش���֮��Ϊ��" +jointCost );
		System.out.println("�����˴��ۼ��ϵȴ����ۼ�ʢ���ش���֮��Ϊ��"+ (jointCost +data.fRobots * nRobots));
		System.out.println("���д���֮��Ϊ��" +  (jointCost +data.fRobots * nRobots + Calculator.calcW(data.applications, data.fEnergy, map, router)));
	}

}
