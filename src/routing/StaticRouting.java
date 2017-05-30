/**   
* @Title: StaticRouting.java 
* @Package routing 
* @Description: TODO(��һ�仰�������ļ���ʲô) 
* @author FlyingFish
* @date 2017-05-17
* @version V1.0   
*/
package routing;

import java.util.ArrayList;
import java.util.PriorityQueue;

import models.Point;

import models.Map;

/**
* <p> ��̬·��</p>
* <p>���õϽ�˹������Դ���·���㷨</p>
* @author FlyingFish
* @date 2017-05-17
*/
public class StaticRouting {
//�Ծ���Ϊ��������һ�龲̬·��
	public static final int MAX_MAP_SIZE = 100;	
	
    private static int[][] maze = new int[MAX_MAP_SIZE][MAX_MAP_SIZE];
    private static int[][] direction = new int[MAX_MAP_SIZE][MAX_MAP_SIZE];

    private static PriorityQueue<ElemType> q = new PriorityQueue<ElemType>();   
     
    private static int n,m;
     
    /**
    * <p>Title: </p>
    * <p>Description: </p>
    * @param args
    */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

         
    }
    
    public static void init(int[][] parkingLot){
    	n = parkingLot.length;
    	m = parkingLot.length;
    	for(int r = 0;r <n;r ++)
    		for(int c = 0;c < m;c++){
    			if(parkingLot[r][c] == Map.X || parkingLot[r][c] == Map.E || parkingLot[r][c] == Map.I)
    				//ͨ�������ڡ���ڿ�������ͨ��
    				maze[r][c] = 0;
    			else
    				maze[r][c] = -1;
    				//maze[r][c] = parkingCase.map[r][c].ordinal();
                direction[r][c] = 0;
    		}
    	
        //��ʼ�����ȶ���
        q = new PriorityQueue<ElemType>();
    }

    public static int[][] routing(Point start){
         
        maze[0][0] = -1;
        q.add(new ElemType(start,0));
         
        while(!q.isEmpty()){
            ElemType e = q.poll();
                
            for(int i = 1;i <= 4;i ++){
                Point next = nextPosition(e.seat,i);
                if(pass(next)){
                    ElemType nexte = new ElemType(next, e.time + 1);
                    maze[next.x][next.y] = -1;   
                    direction[next.x][next.y] = i;
                    q.add(nexte);   
                }
            }   
        } 
        
        int[][] routingTable = new int[n][m];
        for(int r = 0;r < n;r ++)
        	for(int c = 0;c < m;c ++)
        		routingTable[r][c] = direction[n][m];
        
        return routingTable;
    }
    
    public static ArrayList<Point> getRoute(int x1,int y1,int x2,int y2,int[][] routingTable){   	    	
    	ArrayList<Point> rout = new ArrayList<Point>();
    	
    	int curx = x2,cury = y2;
    	rout.add(new Point(curx,cury));

    	if(x1 == x2 && y1 == y2)
    		//����Ŀ����غ�
    		return rout;
    	
    	if(routingTable[curx][cury] == 0)
    		//������޷�����Ŀ���
    		return null;
    	
    	while( routingTable[curx][cury] > 0  ){
    		Point prior =  priorPosition(curx,cury,routingTable[curx][cury]);
    		rout.add(0, prior);
    		curx = prior.getX();
    	}
    	
    	if(curx != x1 || cury != y1)
    		throw new IllegalArgumentException("���;�̬·�ɱ�ƥ��");
    	
    	
    	return rout;
    }
 
    public static int getHops(int x1,int y1,int x2,int y2,int[][] routingTable){   	    	 	
    	if(x1 == x2 && y1 == y2)
    		//����Ŀ����غ�
    		return 0;
    	
    	int curx = x2,cury = y2;
    	int hops = 0;
     	
    	if(routingTable[curx][cury] == 0)
    		//������޷�����Ŀ���
    		return -1;
    	
    	while( routingTable[curx][cury] > 0  ){
            switch(routingTable[curx][cury] ){
            //ע������ϵ��X�ᴹֱ���£�Y��ˮƽ���ҡ�
                case 1: cury --;
                            break;
                case 2: curx --;
                            break;
                case 3: cury ++;
                            break;
                case 4: curx ++;
                            break;             
            }
            hops++;
    	}
    	
    	if(curx != x1 || cury != y1)
    		throw new IllegalArgumentException("���;�̬·�ɱ�ƥ��");
    		
    	return hops;
    }
    
    /**���ݻ���ͷ����ȡ���ڽӵ�
     * @param p ���㣬���øú��������޸ĸò���ֵ
     * @param direction �ڽӵ�����ڻ���ķ���*/
    private static Point nextPosition(Point p,int direction){
        int locX = p.x;
        int locY = p.y;
        switch(direction){
        //ע������ϵ��X�ᴹֱ���£�Y��ˮƽ���ҡ�
            case 1: locY ++;
                        break;
            case 2: locX ++;
                        break;
            case 3: locY --;
                        break;
            case 4: locX --;
                        break;
        }       
        return new Point(locX,locY);
    }
 
    /**���ݻ���ͷ���׷�����ڽӵ�
     * @param p ���㣬���øú��������޸ĸò���ֵ
     * @param direction ����������ڽӵ�ķ���*/
    private static Point priorPosition(int x,int y,int direction){
        int locX = x;
        int locY = y;
        switch(direction){
        //ע������ϵ��X�ᴹֱ���£�Y��ˮƽ���ҡ�
            case 1: locY --;
                        break;
            case 2: locX --;
                        break;
            case 3: locY ++;
                        break;
            case 4: locX ++;
                        break;
        }       
        return new Point(locX,locY);
    }
    
    public static boolean pass(Point p){   
    	
        if(p.x < 0 || p.y < 0 || p.x >= n || p.y >= m) 
        	//�Թ�Խ��
            return false;
        else if(maze[p.x][p.y] < 0)
        	//�Ѿ��߹��˻�������ͣ��λ��·�����Բ�����
            return false;
        else
            return true;

    }
    
  
    static class  ElemType implements Comparable<ElemType>{
        //ͨ������maze�е�����λ��
        public Point seat;
         
        public int time;
         
        public ElemType(Point seat,int t){
            this.seat = new Point(seat.x,seat.y);
            this.time = t;
        }
 
        /** (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(ElemType o) {
            // TODO Auto-generated method stub
            return time - o.time;
        }
       
    }
}
