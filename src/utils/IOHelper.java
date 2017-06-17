/**   
* @Title: IOHelper.java 
* @Package util 
* @Description: TODO(用一句话描述该文件做什么) 
* @author FlyingFish
* @date 2017-05-18
* @version V1.0   
*/
package utils;

import java.io.BufferedInputStream;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import models.Map;
import models.Point;
import models.VRP;
import routing.Routing;

/**
* <p> IOHelper</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2017-05-18
*/
public class IOHelper {
	public static final PrintStream STANDARD_OUT = System.out;
	
	private static PrintWriter output;
	private static Scanner input;
	private static String fileOutPath = "D:/fileOut.txt";   //默认路径
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param args 
	*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static Initiation loadFromFile(String filePath) throws FileNotFoundException{
		 String fileInPath = filePath;
		 File f = new File(fileInPath);
		 Scanner cin = new Scanner(new FileInputStream(f));
		 Initiation init = new Initiation();
		 init.init(cin);
		 return init;
	}
	
	public static void out2file(String filePath) throws IOException{    //失败时的输出
		 File systemOut = new File(filePath);
		 systemOut.createNewFile();
		 FileOutputStream fileOutputStream = new FileOutputStream(systemOut);
		 PrintStream printStream = new PrintStream(fileOutputStream);
		 System.setOut(printStream);
		 System.out.println("NO");
		 System.setOut(IOHelper.STANDARD_OUT);
		 
	}
	
	public static void out2file(int nRobots, int waitingTime, int energyConsumption,VRP[] applications,Routing router,Map map,String filePath) throws IOException{
		 fileOutPath = filePath;
		 File systemOut = new File(fileOutPath);
		 systemOut.createNewFile();
		 FileOutputStream fileOutputStream = new FileOutputStream(systemOut);
		 PrintStream printStream = new PrintStream(fileOutputStream);
		 System.setOut(printStream);
		 System.out.println("YES");
		 System.out.print(nRobots+" ");
		 System.out.print(waitingTime+" ");
		 System.out.println(energyConsumption);
		 for(int i = 0;i < applications.length;i++){
			 System.out.print(applications[i].carID+1+" ");
			 if(applications[i].refused == true){
				 System.out.println("yes");
				 continue;
			 }else{
				 System.out.print("no "+applications[i].task1.exeRobotID+" "+applications[i].task1.realStartTime+" ");
				 Point location = map.allSpaces.get(applications[i].task1.parkingSpaceID).location;
				 ArrayList<Point> pointList = router.routing(map.in, location);
				 for(int j = 0;j < pointList.size();j++){
					 int x = pointList.get(j).getX();
					 int y = pointList.get(j).getY();
					 System.out.print("("+x+","+y+")"+" ");
				 }
				 System.out.print(applications[i].task2.exeRobotID+" "+applications[i].task2.realStartTime+" ");
				 ArrayList<Point> pointList2 = router.routing(location, map.out);
				 for(int j = 0;j< pointList2.size();j++){
					 int x = pointList2.get(j).getX();
					 int y = pointList2.get(j).getY();
					 System.out.print("("+x+","+y+")"+" ");
				 }
				 System.out.println();
			 }
		 }
		 System.setOut(IOHelper.STANDARD_OUT);		 
	}
	
	public static Initiation loadFromConsole(){		
		Initiation init = new Initiation();
		Scanner cin = new Scanner(System.in);
		init.init(cin);
		return init;
	}
	
	public static void out2console(int nRobots, int waitingTime, int energyConsumption,VRP[] applications,Map map,Routing router,String filePath){
		 System.out.println("YES");
		 System.out.print(nRobots+" ");
		 System.out.print(waitingTime+" ");
		 System.out.println(energyConsumption);
		 for(int i = 0;i < applications.length;i++){
			 System.out.print(applications[i].carID+1+" ");
			 if(applications[i].refused == true){
				 System.out.println("yes");
				 continue;
			 }else{
				 System.out.print("no "+applications[i].task1.exeRobotID+" "+applications[i].task1.realStartTime+" ");
				 Point location = map.allSpaces.get(applications[0].task1.parkingSpaceID).location;
				 ArrayList<Point> pointList = router.routing(map.in, location);
				 for(int j = 0;j < pointList.size();j++){
					 int x = pointList.get(j).getX();
					 int y = pointList.get(j).getY();
					 System.out.print("("+x+","+y+")"+" ");
				 }
				 System.out.print(applications[i].task2.exeRobotID+" "+applications[i].task2.realStartTime+" ");
				 ArrayList<Point> pointList2 = router.routing(location, map.out);
				 for(int j = 0;j< pointList2.size();j++){
					 int x = pointList2.get(j).getX();
					 int y = pointList2.get(j).getY();
					 System.out.print("("+x+","+y+")"+" ");
				 }
				 System.out.println();
			 }
		 }		
	}	
}
