/**   
* @Title: Heap.java 
* @Package week18th 
* @Description: TODO(��һ�仰�������ļ���ʲô) 
* @author FlyingFish
* @date 2017-01-16
* @version V1.0   
*/
package dataStructure.other;

import java.util.List;

/**
* <p>ʵ��С����</p>
* <p>Description: </p>
* @author FlyingFish
* @date 2017-01-16
*/
public class HeapMin<E extends HeapElement<K>,K> {
	private java.util.List<E> list = new java.util.ArrayList<E>(); 
	
	/*����һ���Ƚ���������ѡ��ʹ����Ȼ�򣨴�ʱcomparator = null����Ƚ�������Ԫ������
	 * ��һ���棬ͨ���Ƚ������Խ�һЩ��Ȼ���Ѿ�ȷ�������ͣ���Integer����С���ѱ�ɴ󶥶�*/
	//private final java.util.Comparator<? super E> comparator; 
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p>  
	*/
	public HeapMin() {
		// TODO Auto-generated constructor stub
		list.add(null);
	}

	/** 
	* <p>Title: </p> 
	* <p>0��λ�ò��� </p> 
	* @param elements 
	*/
	public HeapMin(E[] elements){
		list.add(null);
		for(int i = 0;i < elements.length;i ++)
			add(elements[i]);
	}

	public HeapMin(List<E> elements){
		list.add(null);
		for(int i = 0;i < elements.size();i ++)
			add(elements.get(i));
	}
	
	
	public static void main(String[] args){
		//�ö�������
		Integer[] elems = {49,38,65,97,76,13,27,49};
		
		HeapEAdapter[] elemsAdp = HeapEAdapter.getAdapters(elems);
		HeapMin heap = new HeapMin(elemsAdp);
		System.out.println("��ʼ�ѣ�Ԫ�ظ�ʽ������/λ�ã�");
		System.out.println(heap);		
		System.out.println("������4��λ��49��Ϊ9");
		//elemsAdp[3].value = 9;
		heap.decreaseKey(4, 9);
		System.out.println(heap);		
		
		System.out.println("������");
		int times = elems.length;
		while(times -- > 0){
			HeapElement he = heap.remove();
			System.out.println(heap + " " + he);
		}
		
	}
	
	/** 
	* <p>���һ��Ԫ�ص��� </p> 
	* <p>Description: </p> 
	* @param e 
	*/
	public void add(E e){
		list.add(e);
		shiftUp(size());
	}

	/** 
	* <p>����ͷԪ�أ������Ӷ����Ƴ�</p> 
	* <p>Description: </p> 
	* @return 
	*/
	public E peek(){
		if(size() == 0)
			return null;
		else
			return list.get(1);
	}
	
	/** 
	* <p>�Ƴ������ضѶ�Ԫ�أ������Ϊ�գ�����null</p> 
	* <p>Description: </p> 
	* @return 
	*/
	public E remove(){
		if(size() == 0)
			return null;
		if(size() == 1)
			return list.remove(1);
			
		//ע���1��λ�ÿ�ʼ��Ԫ��
		E removedElem = list.get(1);
			
		//�����һ��λ���ϵ�Ԫ�ظ�����һ��λ���ϵ�Ԫ��
		list.set(1, list.get(size()));
		
		//ȥ�����һ��λ���ϵ�Ԫ��
		list.remove(size());
		
		//�Ե�һ��λ���ϵ�Ԫ�ؽ���ɸѡ
		shiftDown(1);
		return removedElem;
	}
	
	/** 
	* <p>��Сĳ���ڵ��ֵ</p> 
	* <p>Description: </p> 
	* @param elemLoc
	* @param key ע��keyֵӦ���ǡ���С���ģ���ν��С�Ǿ���Ȼ��Ĭ�ϣ����߱Ƚ������� 
	*/
	public void decreaseKey(int elemLoc,K key ){	
		list.get(elemLoc).updateKey(key);
		shiftUp(elemLoc);
	}
		
	/** 
	* <p>����ɸѡ </p> 
	* <p>��С������ƵĲ������Է�Χ��[1,elementLoc]��Ԫ�ؽ����ڲ�������ʹ����elementLocλ���ϵ�Ԫ������ѵĶ��壬ǰ�����ڷ�Χ[1,elementLoc - 1]�ϵ�Ԫ������ѵĶ��塣</p> 
	* @param elementLoc 
	*/
	protected void shiftUp(int elementLoc){
		int newLoc = elementLoc;
		
		//Ԥ�ȱ���elementLocλ���ϵ�Ԫ��
		E elem = list.get(elementLoc);
		
		//ǿ��ת����ǰ����Eʵ����Comparable�ӿ�
		//֮����ʹ��Comparable<? super E>���ǿ��ǵ�ʵ��Comparable�ӿڵĿ�����E��E�ĸ���
		@SuppressWarnings("unchecked")
		Comparable<? super E> key =( Comparable<? super E>)list.get(elementLoc);

		for(list.set(0, list.get(elementLoc))//�����ڱ�
			;key.compareTo(list.get(newLoc/ 2))< 0;newLoc /= 2){
			
			//�����ñ�ǣ��ٽ����ڵ�����
			list.get(newLoc / 2).setElementLocation(newLoc);
			list.set(newLoc, list.get(newLoc / 2));		
		}
		
		//��Ŀ��ڵ��Ƶ�����λ��
		elem.setElementLocation(newLoc);
		list.set(newLoc, elem);
		
		//�ڱ����
		list.set(0, null);
	}
	
	/** 
	* <p>����ɸѡ</p> 
	* <p>��С������ƵĲ���������elementLoλ��Ϊ���Ķ�(��Ա��Χ������[elementLoc,size()])�����ڲ�������ʹ��Ѷ���С��ǰ���Ǹö�����Ԫ�ط���С���Ѷ��塣</p> 
	* @param elementLoc �������Ѷ�
	*/
	protected void shiftDown(int elementLoc){
		int newLoc  = elementLoc,child;
		E elem = list.get(elementLoc);
		
		for(; newLoc * 2 <= size() ;newLoc = child){
			//����С���ӽڵ�
			child = newLoc * 2;
					
			Comparable<? super E> keyChild =( Comparable<? super E>)list.get(child);			
			if(child < size() && keyChild.compareTo(list.get(child + 1) )  > 0)
				//����ҽڵ�����ұ���ڵ�С��ѡ�ҽڵ�
				keyChild = ( Comparable<? super E>)list.get(++ child);	
			
			if(keyChild.compareTo(elem) < 0){//���ӽڵ��С
				list.get(child).setElementLocation(newLoc);
				list.set(newLoc, list.get(child));
			}else
				break;//ֱ���˳�����ʱ��������childȥ����newLoc��newLoc = child��				
		}
		
		//��Ŀ��ڵ��Ƶ�����λ��
		elem.setElementLocation(newLoc);
		list.set(newLoc, elem);		
	}
	
	/** 
	* <p>���ضѵĴ�С��ͬʱҲ�Ƕ������һ���ڵ�ı��</p> 
	* <p>Description: </p> 
	* @return 
	*/
	public int size(){
		return list.size() - 1;
	}
	
	public boolean isEmpty(){
		return size() <= 0;
	}
	
	public String toString(){		
		return list.toString();
	}
}
