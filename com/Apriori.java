/*
 * Author : Rohan Panicker
 * Midterm Project : Apriori Algorithm
 * CS 634 - Data Mining
*/
package com;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;

public class Apriori {
	
	static ArrayList<String> containsItemList = new ArrayList<String>();
	static ArrayList<String> containsItem = new ArrayList<String>();
	static ArrayList<Integer> containsCount = new ArrayList<Integer>();
	static ArrayList<String> backUpItems = new ArrayList<String>();
	static ArrayList<Integer> backUpCounts = new ArrayList<Integer>();
	static ArrayList<String> rejectList = new ArrayList<String>();
	static ArrayList<String> associationRules = new ArrayList<String>();
	static Apriori ap = new Apriori();
	static double minimumSuport ;
	static double minimumSupportValue ;
	static double minimumConfidence ;
	static int iterations;
	static int iterationCount = 0;

	public Apriori() {
		// TODO Auto-generated constructor stub
	}
	
	public void backUp() {
		
		if(!backUpItems.isEmpty()) {
			backUpItems.clear();
			backUpCounts.clear();
		}
		
		for( int i=0; i<containsItem.size(); i++ ) {
			backUpItems.add(containsItem.get(i));
			backUpCounts.add(containsCount.get(i));
		}
		
	}	
		
	//This is used to generate the node
	public static String generateNode(String itemList,String item) {
		
		String node="";
		
		String arguement1[]=itemList.split(",");
		String arguement2[]=item.split(",");
		
		for(int i=0; i<arguement1.length; i++) {
			
			if( i==0 ) {
				node=arguement1[i];
			}
			else {
				node=node+","+arguement1[i];
			}
		}
		
		for(int i=0;i<arguement2.length; i++) {
			
			if(!node.contains(arguement2[i])) {
				node=node+","+arguement2[i];
			}
			
		}
		
		return node;
		
	}//generateNode() ends
	
	//This checks the node for constraints
	public static boolean addCheck(String item) {
		
		boolean decision = true;
		
		for(int i=0; i<rejectList.size(); i++) {
			
			String rejects[] = rejectList.get(i).split(",");
			int counter = 0;
			
			for(int j=0; j<rejects.length; j++) {
				
				if(item.contains(rejects[j])) {
					counter=counter+1;
				}
				
			}
			
			if( counter == rejects.length ) {
				
				decision = false;
				return decision;
				
			}
			
			
			
		}
		
		//here we check for duplication
		if( containsItem.isEmpty() ) {
			
			return decision;
			
		}
		else {
			
			String itemList[] = item.split(",");
			
			
			for(int i=0; i<containsItem.size(); i++) {
				
				String value=containsItem.get(i);
				int counter=0;
				
				for(int j=0; j<itemList.length; j++) {
					
					if(value.contains(itemList[j])) {
						
						counter=counter+1;
						
					}
					
				}
				
				if( counter==itemList.length ) {
					
					decision = false;
					return decision;
					
				}
				
			}
			
			return decision;
			
		}
		
	}// addCheck() ends
	
	public static String[] fetchRecords() {
		
		ResultSet rs = null ;
		ResultSet rs1 = null ; 
		String itemList[] = null;
		try {
			int itemCount=0;
			Class.forName("com.mysql.jdbc.Driver");
			String db="implement";
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+db,"root","root");
			Statement stmt=con.createStatement();  
			rs=stmt.executeQuery("select ITEMS from TRANSACTIONS");
			while(rs.next()) {
				itemCount=itemCount+1;
			}
			itemList = new String[itemCount];
			rs1=stmt.executeQuery("select ITEMS from TRANSACTIONS");
			
			int pos=0;
			
			System.out.println("The transactions for the " + db.toUpperCase() + " DB areas follows : ");
			
			while(rs1.next()) {
				itemList[pos]=rs1.getString(1);
				pos=pos+1;
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return itemList;
		
	}//fetchRecords() ends
	
	public static String[] itemExtraction(String itemList[]) throws SQLException {
		
		String items[] = null ;
		String item="";
		
		for(int j=0; j<itemList.length; j++) {
			
			String inter[] = itemList[j].split(",");
			
			for(int i=0; i<inter.length; i++) {
				
				if(!item.contains(inter[i])) {
					item= item+inter[i]+",";
				}	
			}
		}
		
		item=item.substring(0, item.length()-1);
		items = item.split(",");
		return items;
		
	}
	
	public static int[] itemCount(String itemList[], String items[]) throws SQLException {
		
		int[] itemCount = new int[items.length];
		
		for(int i=0; i<items.length; i++) {
			
			for(int j=0; j<itemList.length; j++){
				
				if(itemList[j].contains(items[i])) {
					itemCount[i]=itemCount[i]+1;
				}
				
			}
			
		}
		
		return itemCount;
	}
	
	public static void countUpdation() {
		
		for(int i=0; i<containsItem.size(); i++ ) {
			
			String items[] = containsItem.get(i).split(",");
			int counter=0;
			
			for(int j = 0; j < containsItemList.size() ; j++) {
				
				String list = containsItemList.get(j);
				int k;
			
				for(k=0; k<items.length; k++ ) {
					
					if(list.contains(items[k])) {
						//do nothing
					}
					else {
						break;
					}
				}
				
				if(k == items.length) {
					
					counter=counter+1;
					
				}
				
			}
			
			containsCount.add(i,counter);
			
		}
		
	}
	
	
	//Set Updation for List with single items
	public static void setUpdation() {
		
		String itemArray[] = new String[containsItem.size()];
		
		for( int i=0; i<itemArray.length; i++) {
			
			itemArray[i]=containsItem.get(i);
			
		}
		
		containsItem.clear();
		containsCount.clear();
		
		String[] check = itemArray[0].split(",");
		int size = itemArray.length;
		
		//Enters here when list has only one item in one node
		if( check.length == 1 ) {
			
			for(int i=0; i < size ; i++ ) {
				
				String itemListOld = itemArray[i];
				
				for(int j=i+1; j<size ; j++) {
					
					String itemListNew = itemListOld+","+itemArray[j];
					containsItem.add(itemListNew);
					containsCount.add(0);
					itemListNew="";
					
				}
				
			}
			
			
		}
		else if( check.length > 1 ) {
			
			for(int i=0; i<itemArray.length; i++) {
				
				String items=itemArray[i];
				String individualItems[] = itemArray[i].split(",");
				
				for(int j=0; j<individualItems.length; j++ ) {
					
					for(int k=i+1; k<itemArray.length; k++) {
						
						if( itemArray[k].contains(individualItems[j])) {
							
							// This is used to generate the node we add
							String addNode=ap.generateNode(itemArray[k],itemArray[i]);
					
							
							// This is used to add the node in the list
							boolean decision = ap.addCheck(addNode);
							
							if(decision) {
								containsItem.add(addNode);
								containsCount.add(0);
							}
							else {
								rejectList.add(addNode);
							}
						}			
					}
				}
			}
		}//else ends
		
	}//setUpdation() ends
	
	public static void minSetGeneration() {
		
		boolean breakCondition = false;
		
		while(iterationCount<iterations) {
			
			if( containsCount.size() <= 1 ) {
				break;
			}
			
			String[] check = containsItem.get(0).split(",");
			
			if(check.length==1) {
				
				ap.checkSupport();
				
				if( containsItem.size() >1 ) {
					ap.backUp();
					ap.setUpdation();
					ap.countUpdation();
				}
	
				iterationCount++;
				
			}//if ends for the first loop when list has only one item
			else if(check.length>1) {
				
				ap.checkSupport();
				
				if( containsItem.size() > 1 ) {
					
					ap.backUp();
					ap.setUpdation();
					ap.countUpdation();
					
					if(containsCount.size()==0 && containsItem.size()==0) {
						breakCondition=true;
						for(int i=0;i<backUpItems.size();i++) {
							containsItem.add(backUpItems.get(i));
							containsCount.add(backUpCounts.get(i));
						}
					}
					
					int r;
					for(r=0;r<containsCount.size();r++) {
						
						if(containsCount.get(r)<minimumSupportValue) {
							//do nothing
						}
						else {
							break;
						}
						
					}
					
					if(r==containsCount.size()) {
						breakCondition=true;
						for(int i=0;i<backUpItems.size();i++) {
							containsItem.add(backUpItems.get(i));
							containsCount.add(backUpCounts.get(i));
						}
					}
					
					
				}
				
				iterationCount++;
				
			}
			
			if( containsItem.size() == 1 ) {
				break;
			}
			
			if(breakCondition) {
				break;
			}
			
			
		}//while ends
		
	}
	
	public void generateSupportandConfidence(int transactions,int condition) throws NumberFormatException, IOException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter Minimum Support percentage : ");
		minimumSuport = Double.parseDouble(br.readLine());
		System.out.println("Enter Minimum Confidence percentage : ");
		minimumConfidence = Double.parseDouble(br.readLine());
		iterations = condition;
		minimumSupportValue=(minimumSuport/100)*transactions;
		
	}
	
	public void checkSupport() {
		
		boolean error=true;
		while(error) {
			
			error=false;
			
			for(int i=0;i<containsItem.size();i++) {
				
				if(containsCount.get(i) < minimumSupportValue ) {

					rejectList.add(containsItem.get(i));
					containsItem.remove(i);
					containsCount.remove(i);
			
				}
				
			}
			
			for(int i=0;i<containsItem.size();i++) {
				
				if(containsCount.get(i) < minimumSupportValue) {
					error=true;
					break;
				}
				
			}
				
		}
		
	}
	
	public void confidenceAndSupportGeneration() {
		
		for(int i=0; i<containsItem.size(); i++) {
			
			String minimumItems[] = containsItem.get(i).split(",");
			
			for(int j=0; j<minimumItems.length; j++ ) {
				
				String first="";
				String second="";
				first = minimumItems[j];
				
				for(int k=0; k<minimumItems.length; k++){
					
					if(!first.equals(minimumItems[k])) {
						
						second=second+minimumItems[k]+",";
						
					}
					
				}
				
				second=second.substring(0, second.length()-1);
				int length = minimumItems.length;
				int counter=0;
				String op1[];
				String op2[];
				ArrayList<String> XnZ=new ArrayList<String>();
				double firstCounter;
				double secondCounter;
				double bothCounter;
				
				while(counter<length-1) {
					
					firstCounter=0;
					secondCounter = 0;
					bothCounter = 0;
					
					if(first.length()==1) {
						first=first+",";
						op1=first.split(",");
						first=first.substring(0,first.length()-1);
						op2=second.split(",");
					}
					else {
						op1=first.split(",");
						op2=second.split(",");
					}
					
					//Loop to find the count of the items in the first string 
					for(int p=0; p<containsItemList.size(); p++) {
						
						int q;
						
						for(q=0; q<op1.length; q++) {
							
							if(containsItemList.get(p).contains(op1[q])) {
								//do nothing
							}
							else {
								break;
							}
						}
						
						if( q == op1.length ) {
							XnZ.add(containsItemList.get(p));
						}
						
					}
					
					firstCounter = XnZ.size();
					
					
					//Loop to find the count of number of items in the second string
					for(int p=0; p<containsItemList.size(); p++) {
						
						int q;
						
						for(q=0; q<op2.length; q++) {
							
							if(containsItemList.get(p).contains(op2[q])) {
								//do nothing
							}
							else {
								break;
							}
						}
						
						if( q == op2.length ) {
							secondCounter=secondCounter+1;
						}
						
					}
					
					// Loop to find the count of First & Second
					boolean matcher = true;
					while(matcher) {
						
						matcher=false;
						
						for(int p=0; p<op2.length; p++) {
							
							for(int q=0; q<XnZ.size(); q++) {

								if(XnZ.get(q).contains(op2[p])) {
									//do nothing
								}
								else {
									XnZ.remove(q);
								}
							}
							
						}
						
						
						for(int p=0; p<op2.length; p++) {
							for(int q=0; q<XnZ.size(); q++) {
								
								if(!XnZ.get(q).contains(op2[p])) {
									matcher=true;
									break;
								}
								
							}
						}
						
					}
					
					bothCounter = XnZ.size();
					XnZ.clear();
					 
					
					double confidence = (bothCounter/firstCounter)*100;
					double support = (bothCounter/containsItemList.size())*100;
						
					if(confidence>=minimumConfidence) {
						
						associationRules.add(first+"->"+second);
						
					}
					
					if(second.indexOf(",") != -1) {
						String inter = second.substring(0, second.indexOf(","));
						first = first + "," + inter;
						second = second.substring(second.indexOf(",")+1, second.length());
					}
					
					++counter;
					
				}//while Loop ends	
			}	
		}
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			String itemList[] = ap.fetchRecords();
			String items[] = ap.itemExtraction(itemList);
			int itemsCount[]=ap.itemCount(itemList,items);
			int pos=0;
			
			for(int i=0;i<itemList.length;i++) {
				pos=pos+1;
				System.out.println(pos+"."+itemList[i]);
			}
			System.out.println();
			
			ap.generateSupportandConfidence(itemList.length,items.length);
			ap.checkSupport();
			
			
			
			for(int i=0; i<itemList.length; i++) {
				
				containsItemList.add(itemList[i]);
				
			}
			
			for(int i=0; i<items.length; i++) {
				containsItem.add(items[i]);
				containsCount.add(itemsCount[i]);				
			}
			
			ap.minSetGeneration();		
			
			ap.confidenceAndSupportGeneration();
			
			System.out.println("The Association Rules generated are : ");
			for(int i=0;i<associationRules.size(); i++) {
				System.out.println(associationRules.get(i));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (OutOfMemoryError e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
