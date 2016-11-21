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
public class Classifier {
	static ArrayList<Integer> trainLabels = new ArrayList<Integer>();/** Stores the results of training data's bitmaps*/
	static ArrayList<Integer> testLabels = new ArrayList<Integer>();/** Stores the results of testing data's bitmaps*/ 
	static ArrayList<Integer> predictedLabels = new ArrayList<Integer>();/** Stores the predicted results*/
	static int [][]trainFaces = new int[451][4200];/** Stores the pixel information of each image in the training data as a 4200 size array (70X60) bitmap */
	static int [][]testFaces = new int[150][4200];/** Stores the pixel information of each image in the testing data as a 4200 size array (70X60) bitmap */
	static int[] positive = new int[4200];
	static int[] negative = new int[4200];
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
		populateVariables();
		/* Reading the testing data*/
		try{
			inputLabelHandle("facedatatestlabels",testLabels);
		}catch(Exception e){
		}

		try{
			inputFaceHandle("facedatatrain",testFaces);
		}catch(Exception e){
		}
		
		predictResults();
		calculateAccuracy();
	}

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

	public static void predictResults(){

		for(int i=0;i<testFaces.length;i++)
		{
			double human = 0.0;
			double no_human = 0.0;
			for(int j=0;j<testFaces[i].length;j++)
			{
				if(testFaces[i][j]==1 && testLabels.get(i)==1)
					human += Math.log(positive[j]/muggle);
				else if(testFaces[i][j]==0 && testLabels.get(i)==1)
					human += Math.log((muggle-positive[j])/muggle);
				else if(testFaces[i][j]==0 && testLabels.get(i)==0)
					no_human += Math.log((wizard-negative[j])/wizard);
				else if(testFaces[i][j]==1 && testLabels.get(i)==0)
					no_human += Math.log(negative[j]/wizard);
			}
			if(human*muggle>no_human*wizard)
				predictedLabels.add(1);
			else
				predictedLabels.add(0);
		}
	}

	public static void populateVariables(){
		for(int i=0;i<trainLabels.size();i++)
		{
			if(trainLabels.get(i)==1)
			{
				muggle++;
				for(int j = 0;j<4200;j++)
					positive[j] += trainFaces[i][j];
			}
			else
			{
				wizard++;
				for(int j = 0;j<4200;j++)
					negative[j] += trainFaces[i][j];

			}
		}
	}

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
	public static void inputFaceHandle(String filename,int[][] faces)throws IOException {
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
