package classifierbayesnaive;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author sandeep,snehal,tanmaya,kushagra
 * @title : Implementation of Naive_Bayes_Classifier to find whether a given bitmap represents the image of a person or not
 * @version 1.0
 */
public class Classifier_1 {
	static ArrayList<Integer> trainLabels = new ArrayList<Integer>();/** Stores the results of training data's bitmaps*/
	static ArrayList<Integer> testLabels = new ArrayList<Integer>();/** Stores the results of testing data's bitmaps*/ 
	static ArrayList<Integer> predictedLabels = new ArrayList<Integer>();/** Stores the predicted results*/
	static int [][]trainFaces = new int[451][4200];/** Stores the pixel information of each image in the training data as a 4200 size array (70X60) bitmap */
	static int [][]testFaces = new int[150][4200];/** Stores the pixel information of each image in the testing data as a 4200 size array (70X60) bitmap */
	static HashMap<Integer, double[]> countTable = new HashMap<Integer,double[]>();/** For each pixel stores the probability of it being '#' and being a human & all other three cases*/
	static double muggle = 0.0;/** Probability of a bitmap being a human*/
	static double wizard = 0.0;/** Probability of a bitmap not being a human*/
	public static void main(String args[]){

		/* Reading Training Data */
		try{
			inputLabelHandle("facedatatrainlabels",trainLabels);
		}catch(Exception e){
		}

		try{
			inputFaceHandle("facedatatrain",trainFaces);
		}catch(Exception e){
		}

		/* Processing the training data and applying the classifier*/	
		populateHashTable();
		//trainFaces = null;trainLabels=null;

		/* Reading the testing data*/
		try{
			inputLabelHandle("facedatatestlabels",testLabels);
		}catch(Exception e){
		}

		try{
			inputFaceHandle("facedatatest",testFaces);
		}catch(Exception e){
		}

		/* Predicting and Evaluating the results*/
		naivePrediction();
		calculateAccuracy();
	}
	/**
	 * Calculates the accuracy of the classifier based on the predictedLabels and the testLabels
	 */
	public static void calculateAccuracy(){
		int positive = 0;
		int negative = 0;
		for(int i=0;i<predictedLabels.size();i++)
		{
			if(testLabels.get(i)==predictedLabels.get(i))
				positive++;
			else
				negative++;
		}
		double accuracy = ((double)positive / (positive+negative))*100;
		accuracy = Math.round(accuracy*100) / 100.0;
		System.out.println("Accuracy of the classifier is : " + accuracy+"%");
		System.out.println("It has correctly classified "+positive+" instances out of "+(positive+negative)+" instances" );
	}
	/**
	 * Classifies a given bitmap as being a human or not based on the Bayes' Theorem
	 */
	public static void naivePrediction(){
		for(int i=0;i<countTable.size();i++)
		{
			countTable.get(i)[0] = Math.log(countTable.get(i)[0]);
			countTable.get(i)[1] = Math.log(countTable.get(i)[1]);
			countTable.get(i)[2] = Math.log(countTable.get(i)[2]);
			countTable.get(i)[3] = Math.log(countTable.get(i)[3]);
		}
		for(int i=0;i<testFaces.length;i++)
		{
			double human = 0.0;
			double no_human = 0.0;
			for(int j=0;j<testFaces[i].length;j++)
			{
				if(testFaces[i][j]==1)
				{
					human += countTable.get(j)[3];
					no_human += countTable.get(j)[2];
				}else{
					human += countTable.get(j)[1];
					no_human += countTable.get(j)[0];
				}
			}
						
			if(human + Math.log(muggle) > Math.log(wizard)+ no_human)
				predictedLabels.add(1);
			else
				predictedLabels.add(0);
		}
	}
	/**
	 * Populates the countTable Hashmap with the probabilities of each pixel.
	 * arr[0] = pixel being 0 when the image is not that of a human
	 * arr[1] = pixel being 0 when the image is that of a human
	 * arr[2] = pixel being 1 when the image is not that of a human
	 * arr[3] = pixel being 1 when the image is that of a human
	 */
	public static void populateHashTable(){
		for(int i = 0 ;i<4200;i++)
		{
			double temp[] = new double[4];
			countTable.put(i,temp);
		}
		for(int i=0;i<trainFaces[0].length;i++)
		{
			for(int j=0;j<trainFaces.length;j++)
			{
				if(trainFaces[j][i]==0 && trainLabels.get(j)==0){
					countTable.get(i)[0]++;
					wizard++;
				}
				else if(trainFaces[j][i]==0 && trainLabels.get(j)==1){
					countTable.get(i)[1]++;
					muggle++;
				}
				else if(trainFaces[j][i]==1 && trainLabels.get(j)==0){
					countTable.get(i)[2]++;
					wizard++;
				}
				else{
					countTable.get(i)[3]++;
					muggle++;
				}
			}
		}
		for(int i=0;i<4200;i++)
		{
			double temp[] = countTable.get(i);
			countTable.get(i)[0] = temp[0]/(temp[0]+temp[2]);
			countTable.get(i)[1] = temp[1]/(temp[3]+temp[1]);
			countTable.get(i)[2] = 1 - countTable.get(i)[0]; 
			countTable.get(i)[3] = 1 - countTable.get(i)[1];
		}
		
		muggle = muggle/(muggle+wizard);
		wizard = 1 - muggle;
	}
	/**
	 * @param filename : the filename containing labels
	 * @param labels : the arrayList of Integers to be updated
	 * @throws IOException
	 */
	public static void inputLabelHandle(String filename,ArrayList<Integer> labels)throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line=null;
		while( (line=br.readLine()) != null) {
			labels.add(Integer.parseInt(line));
		} 
		br.close();
	}
	/**
	 * @param filename : the filename containing faces (bitmaps)
	 * @param faces : the array to be updated
	 * @throws IOException
	 */
	public static void inputFaceHandle(String filename, int[][] faces)throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line=null;
		int linesRead = 0;
		int row=-1;
		int column = 0;
		while((line=br.readLine()) != null) {
			if(linesRead%70==0)
			{
				row++;
				column = 0;
			}
			for(int j =0;j<line.length();j++){
				if(line.charAt(j)=='#')
					faces[row][column]=1;
				else
					faces[row][column]=0;
				column++;
			}
			linesRead++;
		}
		br.close();
	}
	
	
}