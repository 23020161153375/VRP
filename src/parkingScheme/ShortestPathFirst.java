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
public class ShortestPathFirst extends ParkingLotManager {

	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param parkingLot
	* @param router 
	*/
	public ShortestPathFirst(Map parkingLot, Routing router) {
		super(parkingLot, router);
		// TODO Auto-generated constructor stub
	}


	/** (non-Javadoc)
	 * @see parkingScheme.SpaceDispatcher#restore(int)
	 */
	@Override
	public boolean restore(int restoreTime) {
		// TODO Auto-generated method stub
		return false;
	}


	/** (non-Javadoc)
	 * @see parkingScheme.SpaceDispatcher#parkingSpaceDispatch(models.VRP[], int)
	 */
	@Override
	public DispatchState parkingSpaceDispatch(VRP[] vrps, int id, int readyTime) {
		// TODO Auto-generated method stub
		if(readyTime < this.time)
			//�޸ĳ���ʱ��������������Ż���
			throw new IllegalStateException("�����������ȴ���");
		else if(readyTime > this.time)//ʱ�ӵ���
			stateChangeClockwise(readyTime);
				
		//����ǰ������̵�һ�����еĳ�λ���ȳ���
		int fessibleSpace = -1;
		for(int i = 0;i < parkingLot.allSpaces.size() && fessibleSpace == -1;i ++){
			ParkingSpace space = parkingLot.allSpaces.get(i);
			if(space.empty && space.firstPlanningEvent() == null)
				//��λΪ�գ��һ�û���ü����ų���
				fessibleSpace = i;
			else if(!space.empty && space.firstPlanningEvent() != null){
				//��λ�г������Ͼ�Ҫ������
				Task pevent = space.firstPlanningEvent();
				if(pevent.realStartTime < readyTime + router.hops(parkingLot.in, space.location))
					//����ȴ�
					fessibleSpace = i;
				//�����ٿ������������ֳɵĿճ�λû��
			}
		}
		
		if(fessibleSpace != -1)//�ҵ��˺��ʵĳ�λ
			return new DispatchState(true,fessibleSpace,0);
		
		//�����һ��ʱ�����Ƿ��г�λ��ճ���ѡ��������̵�
		int delay = 0;
		for(int i = 0;i < parkingLot.allSpaces.size() && fessibleSpace == -1;i ++){
			ParkingSpace space = parkingLot.allSpaces.get(i);
			if(!space.empty && space.firstPlanningEvent() != null){
				//��λ�г������Ͼ�Ҫ������
				fessibleSpace = i;
				
				//Ҫ��һ���
				delay = 1  +   space.firstPlanningEvent().realStartTime- readyTime - router.hops(parkingLot.in, space.location) ;
			}			
		}
		
		if(fessibleSpace != -1){
			return new DispatchState(true,fessibleSpace,delay);
		}
		
		//�޳�λ�ɹ����ȣ���Ҫ�ȳ�����У�
		return new DispatchState(false,-1,-1);
	}
}
