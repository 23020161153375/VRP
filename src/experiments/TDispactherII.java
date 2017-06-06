/**   
* @Title: TDispactherII.java 
* @Package experiments 
* @Description: TODO(��һ�仰�������ļ���ʲô) 
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
		
		System.out.println("***ִ������***");		
		Task firstPullIn = null,pullIn = null,firstPullOut;
		for(int i = 0;i < data.applications.length ;i ++){
			//���һ������������˳���
			//��װÿ����������л����˰�ʱ����
						
			int readyTime = data.applications[i].requestTime;
			DispatchState ds = parkingLotDispatcher.parkingSpaceDispatch(data.applications, i, readyTime);
			
			System.out.println("���복��"+i+"������ʱ��Ϊ"+readyTime+"��������" );
			if(ds.success){
				System.out.println("����ɹ�����λ��" + ds.parkingSpaceID +"��ʱΪ" + ds.delay );
				pullIn = new Task(i, data.applications[i].requestTime, Task.PULL_IN, ds.parkingSpaceID);
				pullIn.realStartTime = readyTime + ds.delay;
				pullIn.realFinishTime =  pullIn.realStartTime + router.hops(map.in, map.allSpaces.get(ds.parkingSpaceID).location);
				parkingLotDispatcher.event(pullIn);
			}else
				System.out.println("����ʧ��");
			System.out.println();
			
			
			
			//��һ�����������ڳ�λ����ͼ������Ĳ��Բ�������
			if(i == 0)
				firstPullIn = pullIn;
			else if(i == data.applications.length - 2){
				//��ִ�����һ���������ǰ������һ�����ĳ�����
				int startTime = data.applications[i].requestTime;
				firstPullOut = new Task(firstPullIn.carID,startTime,Task.PULL_OUT,firstPullIn.parkingSpaceID);
				firstPullOut.realStartTime = startTime;
				firstPullOut.realFinishTime 
					= firstPullOut.realStartTime 
						+ router.hops(map.allSpaces.get(firstPullOut.parkingSpaceID).location, map.out);
				parkingLotDispatcher.event(firstPullOut);
				
				System.out.println("���복��"+firstPullOut.carID +"�뿪��λ"+firstPullOut.parkingSpaceID+"ʱ��Ϊ"+firstPullOut.realStartTime+"\n");
			}				
		}					
		
		System.out.println("***���Իָ�����***");
		System.out.println("�ص��ڶ���������������ǰ��״̬��Ҳ������ǰ����ʾ�ĵڶ���ͣ����״̬��ͬ��");
		parkingLotDispatcher.restore(data.applications[1].requestTime);

		
		System.out.println("�ص���ʼ״̬(���еĳ�λΪ��)");
		parkingLotDispatcher.restore(0);

	}

}
