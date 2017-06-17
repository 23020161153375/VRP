/**   
* @Title: StaticRouting.java 
* @Package routing 
* @Description: TODO(用一句话描述该文件做什么) 
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
* <p> 静态路由</p>
* <p>采用迪杰斯特拉单源最短路径算法</p>
* @author FlyingFish
* @date 2017-05-17
*/
public class StaticRouting {
//以距离为度量创建一组静态路由
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
    	m = parkingLot[0].length;
    	for(int r = 0;r <n;r ++)
    		for(int c = 0;c < m;c++){
    			if(parkingLot[r][c] == Map.X || parkingLot[r][c] == Map.E || parkingLot[r][c] == Map.I)
    				//通道、出口、入口可以自由通过
    				maze[r][c] = 0;
    			else
    				maze[r][c] = -1;
    				//maze[r][c] = parkingCase.map[r][c].ordinal();
                direction[r][c] = 0;
    		}
    	
        //初始化优先队列
        q = new PriorityQueue<ElemType>();
    }

    public static int[][] routing(Point start){
         
        maze[start.x][start.y] = -1;
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
        		routingTable[r][c] = direction[r][c];
        
        return routingTable;
    }
    
    public static ArrayList<Point> getRoute(int x1,int y1,int x2,int y2,int[][] routingTable){   	    	
    	ArrayList<Point> rout = new ArrayList<Point>();
    	
    	int curx = x2,cury = y2;
    	rout.add(new Point(curx,cury));

    	if(x1 == x2 && y1 == y2)
    		//起点和目标点重合
    		return rout;
    	
    	if(routingTable[curx][cury] == 0)
    		//从起点无法到达目标点
    		return null;
    	
    	while( routingTable[curx][cury] > 0  ){
    		Point prior =  priorPosition(curx,cury,routingTable[curx][cury]);
    		rout.add(0, prior);
    		curx = prior.getX();
    		cury = prior.getY();
    	}
    	
    	if(curx != x1 || cury != y1)
    		throw new IllegalArgumentException("起点和静态路由表不匹配");
    	
    	
    	return rout;
    }
 
    public static int getHops(int x1,int y1,int x2,int y2,int[][] routingTable){   	    	 	
    	if(x1 == x2 && y1 == y2)
    		//起点和目标点重合
    		return 0;
    	
    	int curx = x2,cury = y2;
    	int hops = 0;
     	
    	if(routingTable[curx][cury] == 0)
    		//从起点无法到达目标点
    		return -1;
    	
    	while( routingTable[curx][cury] > 0  ){
            switch(routingTable[curx][cury] ){
            //注意坐标系：X轴垂直向下，Y轴水平向右。
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
    		throw new IllegalArgumentException("起点和静态路由表不匹配");
    		
    	return hops;
    }
    
    /**根据基点和方向获取其邻接点
     * @param p 基点，调用该函数不会修改该参数值
     * @param direction 邻接点相对于基点的方向*/
    private static Point nextPosition(Point p,int direction){
        int locX = p.x;
        int locY = p.y;
        switch(direction){
        //注意坐标系：X轴垂直向下，Y轴水平向右。
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
 
    /**根据基点和方向追溯其邻接点
     * @param p 基点，调用该函数不会修改该参数值
     * @param direction 基点相对于邻接点的方向*/
    private static Point priorPosition(int x,int y,int direction){
        int locX = x;
        int locY = y;
        switch(direction){
        //注意坐标系：X轴垂直向下，Y轴水平向右。
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
        	//迷宫越界
            return false;
        else if(maze[p.x][p.y] < 0)
        	//已经走过了或者属于停车位、路障所以不能走
            return false;
        else
            return true;

    }
    
  
    static class  ElemType implements Comparable<ElemType>{
        //通道块在maze中的坐标位置
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
