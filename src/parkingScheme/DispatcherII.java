/**   
* @Title: ShortestPathFirst.java 
* @Package parkingScheme 
* @Description: TODO(��һ�仰�������ļ���ʲô) 
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
* <p> ���·�����Ȳ���</p>
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
			throw new IllegalStateException("�����������ȴ���");
		else if(readyTime > this.time)//ʱ�ӵ���
			stateChangeClockwise(readyTime);
		//else
			//this.printCurrentStateOfParkingLot();
		
		//����ǰ������̵�һ�����еĳ�λ���ȳ���
		//int fessibleSpace = -1;
		
		//����ɸѡ
		int cost=0,mini=inf;
		int delay=0;
		
		//���
		int choose=0;
		int chooseDelay = -1;
		for(int i = 0;i < parkingLot.allSpaces.size() ;i ++){
			ParkingSpace space = parkingLot.allSpaces.get(i);
			if(space.empty && space.firstPlanningEvent() == null) {
				//��λΪ�գ��һ�û���ü����ų���
				delay = 0;
			}else if(!space.empty){
				//��λ�г������Ͼ�Ҫ������
				Task pevent=null;
				if(space.firstPlanningEvent() != null) {//�����¼�ʲôʱ����ӵģ������������֮ǰ��
					pevent = space.firstPlanningEvent();
					if (pevent.realStartTime < readyTime + router.hops(parkingLot.in, space.location))
						//����ȴ�
						delay = 0;
						//�����ٿ������������ֳɵĿճ�λû��
					else {
						//Ҫ��һ���
						delay = 1 + space.firstPlanningEvent().realStartTime - readyTime - router.hops(parkingLot.in, space.location);
					}
				}
				else{//û����ӳ����¼�
					delay=inf;
				}
			}
			else{
				//�а��ŵĳ�λ���ݲ�����
				delay=inf;
			}

			int dis=router.hops(parkingLot.in, space.location)+router.hops(parkingLot.out, space.location);
			
			int m = vrps[id].carMass;
			cost=b*delay+k*m*dis;
			if(cost<mini){
				mini=cost;
				choose=i;
				
				//����ʱ��
				chooseDelay = delay;
			}
		}
		//�����ǻ���������⣬���п��б������
		/*if(mini<inf){
			fessibleSpace=choose;
		}*/
		/*System.out.println("���䳵λ"+fessibleSpace);
		System.out.println("��λ���"+fessibleSpace);
		System.out.println("��λ����"+parkingLot.allSpaces.get(fessibleSpace).location.x+" "+parkingLot.allSpaces.get(fessibleSpace).location.y);
		System.out.println("����"+parkingLot.allSpaces.get(fessibleSpace).key);*/
		
		if(mini < inf){
			return new DispatchState(true,choose,chooseDelay);
		}
		
		//�޳�λ�ɹ����ȣ���Ҫ�ȳ�����У�
		return new DispatchState(false,-1,-1);
	}
}
