

import java.util.*;
import java.io.*;

/**
 * Decision tree algorithm
 */
public class Main
{
	public static Map<Integer, String> Feature = new LinkedHashMap<Integer, String>();
	public static Map<String, Integer> FeatureMap = new LinkedHashMap<String, Integer>();
	public static Map<Integer, String> DecisionTree = new LinkedHashMap<Integer, String>();
	public static Map<Integer, Integer> MajorityClass = new LinkedHashMap<Integer, Integer>();
	public static Map<Integer, String> DecisionTreeVariance = new LinkedHashMap<Integer, String>();
	public static Map<Integer, Integer> MajorityClassVariance = new LinkedHashMap<Integer, Integer>(); 
	public static Map<Integer, String> DecisionTreePrune = new LinkedHashMap<Integer, String>();
	public static Map<Integer, String> DecisionTreeVariancePrune = new LinkedHashMap<Integer, String>();
	public static List<Integer> pruneNonLeafNodes = new ArrayList<Integer>();
	public static List<Integer> pruneNonLeafNodesForVariance = new ArrayList<Integer>();
	public static int[][] trainingData;
	public static int[][] testingData;
	public static int[][] validationData; 

	/**
	 * main function call
	 */
	public static void main(String[] args)
	{
		String trainDataInputFile = args[0];
		String validDataInputFile = args[1];
		String testDataInputFile = args[2];
		String decTreeDisplay = args[3];
		String pruneTreeDisplay = args[4];
		trainingData = readData(trainDataInputFile);
		testingData = readData(testDataInputFile);
		validationData = readData(validDataInputFile);
		int initialLocationNode = 1;
		createDecisionTree(Feature, trainingData, initialLocationNode);
		createDecisionTreeVariance(Feature, trainingData, initialLocationNode);
		System.out.println("Before Pruning Accuracy");
		System.out.println("H1 NP Training " + checkAccuracy(getDecisionTree(), trainingData));
		System.out.println("H1 NP Validation " + checkAccuracy(getDecisionTree(), validationData));
		System.out.println("H1 NP Test " + checkAccuracy(getDecisionTree(), testingData));
		System.out.println("H2 NP Training " + checkAccuracy(getDecisionTreeVariance(), trainingData));
                System.out.println("H2 NP Validation " + checkAccuracy(getDecisionTreeVariance(), validationData));
                System.out.println("H2 NP Test " + checkAccuracy(getDecisionTreeVariance(), testingData));
		Map<Integer, String> prunedDecTree = prunedDecisionTree();
		setPruneDecisionTree(prunedDecTree);
		Map<Integer, String> prunedVarTree = prunedDecisionTreeVar();
		setPruneDecisionTreeVariance(prunedVarTree);
		System.out.println("After Pruning Accuracy");
		System.out.println("H1 P Training " + checkAccuracy(getPruneDecisionTree(), trainingData));
                System.out.println("H1 P Validation " + checkAccuracy(getPruneDecisionTree(), validationData));
                System.out.println("H1 P Test " + checkAccuracy(getPruneDecisionTree(), testingData));
                System.out.println("H2 P Training " + checkAccuracy(getPruneDecisionTreeVariance(), trainingData));
                System.out.println("H2 P Validation " + checkAccuracy(getPruneDecisionTreeVariance(), validationData));
                System.out.println("H2 P Test " + checkAccuracy(getPruneDecisionTreeVariance(), testingData)); 
		String extraPosition = "";
		String moreSpaces = "";
		if(decTreeDisplay.equalsIgnoreCase("yes"))
		{
			System.out.println("Decision Tree Representation Before prunning");
			System.out.println("IG Decision Tree");
			printDecisionTree(1, extraPosition, moreSpaces);
			System.out.println("VI Decison Tree");
			printDecisionTreeVar(1, extraPosition, moreSpaces);
		}
		if(pruneTreeDisplay.equalsIgnoreCase("yes"))
		{
			System.out.println("Decision Tree Representation After prunning");
			System.out.println("IG Decision Tree");
			printPruneDecisionTree(1, extraPosition, moreSpaces);
			System.out.println("VI Decision Tree");
			printPruneDecisionTreeVar(1, extraPosition, moreSpaces);
		} 
	}
	/**
	 * print decision tree
 	 */
	public static void printDecisionTree(int locationNode, String position, String condition)
	{
		if(getDecisionTree().containsKey(locationNode)) {
			if (!getDecisionTree().containsKey(2 * locationNode) && !getDecisionTree().containsKey(2 * locationNode + 1)) {
				String str = position.substring(1) + condition + getDecisionTree().get(locationNode);
				System.out.println(str);			
			} else {
				System.out.println((position.length() > 0 ? position.substring(1) : position) + condition);			
				printDecisionTree(locationNode * 2, position + "| ", getDecisionTree().get(locationNode) + "= 0 :");
				printDecisionTree(locationNode * 2 + 1, position + "| ", getDecisionTree().get(locationNode) + "= 1 :");
			}
		}
	}
	/**
	 *print prune tree
	 */
	public static void printPruneDecisionTree(int locationNode, String position, String condition)
        {
                if(getPruneDecisionTree().containsKey(locationNode)) {
                        if (!getPruneDecisionTree().containsKey(2 * locationNode) && !getPruneDecisionTree().containsKey(2 * locationNode + 1)) {
                                String str = position.substring(1) + condition + getPruneDecisionTree().get(locationNode);
                                System.out.println(str);
                        } else {
                                System.out.println((position.length() > 0 ? position.substring(1) : position) + condition);
                                printPruneDecisionTree(locationNode * 2, position + "| ", getPruneDecisionTree().get(locationNode) + "= 0 :");
                                printPruneDecisionTree(locationNode * 2 + 1, position + "| ", getPruneDecisionTree().get(locationNode) + "= 1 :");
                        }
                }
        }
	/**
	 *print variance impurity decision tree
	 */
	public static void printDecisionTreeVar(int locationNode, String position, String condition)
        {
		 if(getDecisionTreeVariance().containsKey(locationNode)) {
                        if (!getDecisionTreeVariance().containsKey(2 * locationNode) && !getDecisionTreeVariance().containsKey(2 * locationNode + 1)) {
                                String str = position.substring(1) + condition + getDecisionTreeVariance().get(locationNode);
                                System.out.println(str);
                        } else {
                                System.out.println((position.length() > 0 ? position.substring(1) : position) + condition);
                                printDecisionTreeVar(locationNode * 2, position + "| ", getDecisionTreeVariance().get(locationNode) + "= 0 :");
                                printDecisionTreeVar(locationNode * 2 + 1, position + "| ", getDecisionTreeVariance().get(locationNode) + "= 1 :");
                        }
                }
        }
	/**
	 *print prune variance impurity decision tree
	 */
	public static void printPruneDecisionTreeVar(int locationNode, String position, String condition)
        {
                 if(getPruneDecisionTreeVariance().containsKey(locationNode)) {
                        if (!getPruneDecisionTreeVariance().containsKey(2 * locationNode) && !getPruneDecisionTreeVariance().containsKey(2 * locationNode + 1)) {
                                String str = position.substring(1) + condition + getPruneDecisionTreeVariance().get(locationNode);
                                System.out.println(str);
                        } else {
                                System.out.println((position.length() > 0 ? position.substring(1) : position) + condition);
                                printPruneDecisionTreeVar(locationNode * 2, position + "| ", getPruneDecisionTreeVariance().get(locationNode) + "= 0 :");
                                printPruneDecisionTreeVar(locationNode * 2 + 1, position + "| ", getPruneDecisionTreeVariance().get(locationNode) + "= 1 :");
                        }
                }
        }
	/**
	 * check accuracy
	 */
	public static double checkAccuracy(Map<Integer, String> decisionTree, int[][] inputData)
	{
		int positiveCor = 0, negativeCor = 0;
		for(int i = 0; i < inputData.length; i++)
		{
			int locationNode = 1;
			int inputRow[] = inputData[i];
			while(!decisionTree.get(locationNode).equalsIgnoreCase("1")
					&& !decisionTree.get(locationNode).equalsIgnoreCase("0"))
			{
				if(inputRow[FeatureMap.get(decisionTree.get(locationNode))] == 1)
				{
					locationNode = locationNode*2 + 1;
				}
				else
				{
					locationNode = locationNode*2;
				}
			}	
			if(!decisionTree.get(locationNode).equalsIgnoreCase(String.valueOf(inputRow[inputRow.length - 1])))
			{
				negativeCor++;	
			}
			else
			{
				positiveCor++;
			}
		}
		double accuracy = ((double) positiveCor/ (positiveCor + negativeCor))*100;
		return accuracy;
	}
	/**
	 * read data from the file
	 */
	public static int[][] readData(String inputFile)
	{
		int rows = getRows(inputFile);
		int columns = getColumns(inputFile);
		return getData(inputFile, rows, columns);	
	}
	/**
	 * total number of features
	 */	
	public static int getRows(String inputFile)
	{
		int rows = 0;
		BufferedReader br = null;
		try 
		{
			br = new BufferedReader(new FileReader(inputFile));
			String line1 = br.readLine();
			while(br.readLine() != null)
			{
				rows++;
			}
			br.close();	
		} catch(Exception e) {}
		return rows;
	}
	/**
	 * total nubmer of attributes
	 */
	public static int getColumns(String inputFile)
	{
		BufferedReader br = null;
		try 
		{
			br = new BufferedReader(new FileReader(inputFile));
			String line1 = br.readLine();
			String arr[] = line1.split(",");
			for(int c = 0; c < arr.length; c++)
			{
				Feature.put(c, arr[c]);
				FeatureMap.put(arr[c], c);
			}
			br.close();
		}catch(Exception e) {}
		return Feature.size();
	}
	/**
	 * store data in an array
	 */
	public static int[][] getData(String inputFile, int rows, int columns)
	{	
		String data = null;
		StringTokenizer strTokens = null; 
		String arr[] = null;
		int[][] inputData = new int[rows][columns];
		int i;
		try 
		{
			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			br.readLine();
			i = 0;
			while((data = br.readLine()) != null)
			{
				arr = data.split(",");
				for(int c = 0; c < arr.length; c++)
				{
					inputData[i][c] = Integer.parseInt(arr[c]);
				}
				i++;
			}
			br.close();
		}catch(Exception e) {}			
		return inputData;
	}
	/**
	 * tree creating decision tree variance impurity
	 */
	public static void createDecisionTreeVariance(Map<Integer, String> features, int[][] inputData, int locationNode)
	{
		if(locationNode == 1)
		{
			setDecisionTreeVariance(new LinkedHashMap<Integer, String>());
		}
		double instanceLevelVariance = getVariance(inputData);
		if(instanceLevelVariance == 0)
		{
			getDecisionTreeVariance().put(locationNode, String.valueOf(inputData[0][inputData[0].length - 1]));
		}
		else if(features.size() == 1)
                {
                        int neg = 0;
                        int pos = 0;
                        for(int i = 0; i < inputData.length; i++)
                        {
                                if(inputData[i][inputData[i].length-1] == 1)
                                {
                                        pos++;
                                }
                                else
                                {
                                        neg++;
                                }
                        }
                        if(neg > pos)
                        {
                                getDecisionTreeVariance().put(locationNode, "0");
                        }
                        else
                        {
                                getDecisionTreeVariance().put(locationNode, "1");
                        }
                }
		else
		{
			double varInfo = -3;
			int varNode = -1;
			int varLeftChild = -1;
			int varRightChild = -1;
			int varMajClass = -1;

			for(int i = 0; i < inputData[0].length - 1; i++) 
			{
				if(features.containsKey(i))
				{
					int zero_v = 0; 
					int one_v = 0;
					int zerozero_v = 0;
					int zeroone_v = 0; 
					int onezero_v = 0; 
					int oneone_v = 0;
					for(int j = 0; j < inputData.length; j++) 
					{
						if(inputData[j][i] == 0) 
						{
							zero_v++;
							if(inputData[j][inputData[j].length - 1] == 0) 
							{
								zerozero_v++;
							} 
							else 
							{
								zeroone_v++;
							}
						} 
						else 
						{
							one_v++;
							if(inputData[j][inputData[j].length - 1] == 0) 
							{
								onezero_v++;
							} 
							else 
							{
								oneone_v++;
							}
						}
					}

					double expectedVar = 0;
					double negVar = 0;
					double posVar = 0;
					double informationVar = 0;
					negVar = getSVar(zerozero_v, zeroone_v, zero_v);
					posVar = getSVar(onezero_v, oneone_v, one_v);
					expectedVar = (((double) zero_v/(zero_v + one_v))*negVar) + (((double)one_v/(zero_v + one_v))*posVar);
					informationVar = instanceLevelVariance - expectedVar;

					if(informationVar > varInfo) 
					{
						varInfo = informationVar;
						varNode = i;
						varLeftChild = zero_v;
						varRightChild = one_v;
						int posC = zeroone_v + oneone_v;
						int negC = zerozero_v + onezero_v;
						if(negC > posC)
						{
							varMajClass = 0;
						}
						else
						{
							varMajClass = 1;
						}
					}
				}
			}
			getDecisionTreeVariance().put(locationNode, features.get(varNode));
			MajorityClassVariance.put(locationNode, varMajClass);	
			int[][] leftSubTreeV = new int[varLeftChild][inputData[0].length]; 
			int[][] rightSubTreeV = new int[varRightChild][inputData[0].length];
			Map<Integer, String> leftSubTreeFeaturesV = new LinkedHashMap<Integer, String>(features);
			leftSubTreeFeaturesV.remove(varNode);
			Map<Integer,String> rightSubTreeFeaturesV = new LinkedHashMap<Integer, String>(features);
			rightSubTreeFeaturesV.remove(varNode);
			int lCount = 0, rCount = 0; 
			for(int i = 0; i < inputData.length; i++)
			{
				if(inputData[i][varNode] == 0)
				{
					for(int j = 0;j < inputData[i].length; j++)
					{
						leftSubTreeV[lCount][j] = inputData[i][j];                        
					}
					lCount++;
				}
				else
				{
					for(int j = 0;j < inputData[0].length; j++)
					{
						rightSubTreeV[rCount][j] = inputData[i][j];
					}
					rCount++;
				}
			}
			if(varLeftChild==0)
			{ 
				getDecisionTreeVariance().put(locationNode*2, String.valueOf(varMajClass));
			}            
			else
			{       
				createDecisionTreeVariance(leftSubTreeFeaturesV, leftSubTreeV, locationNode*2);
			}
			if(varRightChild == 0)
			{
				getDecisionTreeVariance().put(((locationNode*2) + 1), String.valueOf(varMajClass));
			}
			else
			{
				createDecisionTreeVariance(rightSubTreeFeaturesV, rightSubTreeV, ((locationNode*2) + 1)); 
			} 		
		}
	}
	/** 
	 * call to create decision tree
	 */
	public static void createDecisionTree(Map<Integer, String> features, int[][] inputData, int locationNode)
	{
		if(locationNode == 1)
		{
			setDecisionTree(new LinkedHashMap<Integer, String>());
		}
		double instanceLevelEntropy = getEntropy(inputData);
		if(instanceLevelEntropy == 0)
		{
			getDecisionTree().put(locationNode, String.valueOf(inputData[0][inputData[0].length - 1]));
		}
		else if(features.size() == 1) 
		{
			int neg = 0;
			int pos = 0;
			for(int i = 0; i < inputData.length; i++) 
			{
				if(inputData[i][inputData[i].length-1] == 1)
				{
					pos++;
				}
				else
				{
					neg++;
				}
			}
			if(neg > pos)
			{
				getDecisionTree().put(locationNode, "0");
			}
			else 
			{
				getDecisionTree().put(locationNode, "1");
			}
		} 
		else
		{
			double gainInfo = -3;
			int gainNode = -1;
			int gainLeftChild = -1;
			int gainRightChild = -1;
			int gainMajClass = -1;

			for(int i = 0; i < inputData[0].length - 1; i++) 
			{
				if(features.containsKey(i))
				{
					int zero_e = 0; 
					int one_e = 0;
					int zerozero_e = 0;
					int zeroone_e = 0; 
					int onezero_e = 0; 
					int oneone_e = 0;
					for(int j = 0; j < inputData.length; j++) 
					{
						if(inputData[j][i] == 0) 
						{
							zero_e++;
							if(inputData[j][inputData[j].length - 1] == 0) 
							{
								zerozero_e++;
							} 
							else 
							{
								zeroone_e++;
							}
						} 
						else 
						{
							one_e++;
							if(inputData[j][inputData[j].length - 1] == 0) 
							{
								onezero_e++;
							} 
							else 
							{
								oneone_e++;
							}
						}
					}

					double expectedEntropy = 0;
					double negEntropy = 0;
					double posEntropy = 0;
					double informationGain = 0;
					negEntropy = getSEntropy(zerozero_e, zeroone_e, zero_e);
					posEntropy = getSEntropy(onezero_e, oneone_e, one_e);
					expectedEntropy = (((double) zero_e/(zero_e + one_e))*negEntropy) + (((double)one_e/(zero_e + one_e))*posEntropy);
					informationGain = instanceLevelEntropy - expectedEntropy;

					if(informationGain > gainInfo) 
					{
						gainInfo = informationGain;
						gainNode = i;
						gainLeftChild = zero_e;
						gainRightChild = one_e;
						int posC = zeroone_e + oneone_e;
						int negC = zerozero_e + onezero_e;
						if(negC > posC)
						{
							gainMajClass = 0;
						}
						else
						{
							gainMajClass = 1;
						}
					}
				}
			}
			getDecisionTree().put(locationNode, features.get(gainNode));
			MajorityClass.put(locationNode, gainMajClass);	
			int[][] leftSubTree = new int[gainLeftChild][inputData[0].length]; 
			int[][] rightSubTree = new int[gainRightChild][inputData[0].length];
			Map<Integer, String> leftSubTreeFeatures = new LinkedHashMap<Integer, String>(features);
			leftSubTreeFeatures.remove(gainNode);
			Map<Integer,String> rightSubTreeFeatures = new LinkedHashMap<Integer, String>(features);
			rightSubTreeFeatures.remove(gainNode);
			int lCount = 0, rCount = 0; 
			for(int i = 0; i < inputData.length; i++)
			{
				if(inputData[i][gainNode] == 0)
				{
					for(int j = 0;j < inputData[i].length; j++)
					{
						leftSubTree[lCount][j] = inputData[i][j];                        
					}
					lCount++;
				}
				else
				{
					for(int j = 0;j < inputData[0].length; j++)
					{
						rightSubTree[rCount][j] = inputData[i][j];
					}
					rCount++;
				}
			}
			if(gainLeftChild==0)
			{ 
				getDecisionTree().put(locationNode*2, String.valueOf(gainMajClass));
			}            
			else
			{       
				createDecisionTree(leftSubTreeFeatures, leftSubTree, locationNode*2);
			}
			if(gainRightChild==0)
			{
				getDecisionTree().put(((locationNode*2) + 1), String.valueOf(gainMajClass));
			}
			else
			{
				createDecisionTree(rightSubTreeFeatures, rightSubTree, ((locationNode*2) + 1)); 
			} 
		}

	}
	/**
	 * get sub entropy
	 */
	public static double getSEntropy(int negC, int posC, int totalC)
	{
		if(totalC == 0)
		{
			return 0;
		}
		double entropy = 0;
		double posEntropy = 0;
		double negEntropy = 0;
		int positiveSamples = posC, negativeSamples = negC, total = totalC;
		if(positiveSamples == 0)
		{
			posEntropy = 0;
		}
		else
		{
			posEntropy = -(((double) positiveSamples/total)*Math.log10((double)positiveSamples/total)/Math.log10(2));
		}
		if(negativeSamples == 0)
		{
			negEntropy = 0;
		}
		else
		{
			negEntropy = -(((double) negativeSamples/total)*Math.log10((double) negativeSamples/total)/Math.log10(2));
		}

		return (posEntropy + negEntropy);
	}
	/**
	 * get entropy
	 */
	public static double getEntropy(int[][] inputData)
	{
		double entropy = 0;
		double posEntropy = 0;
		double negEntropy = 0;
		int positiveSamples = 0, negativeSamples = 0;
		for(int c = 0; c < inputData.length; c++)
		{
			if(inputData[c][inputData[c].length - 1] == 1)
			{
				positiveSamples++;
			}
			else
			{
				negativeSamples++;
			}
		}
		if(positiveSamples == 0)
		{
			posEntropy = 0;
		}
		else
		{
			posEntropy = -(((double) positiveSamples/inputData.length)*Math.log10((double)positiveSamples/inputData.length)/Math.log10(2));
		}
		if(negativeSamples == 0)
		{
			negEntropy = 0;
		}
		else
		{
			negEntropy = -(((double) negativeSamples/inputData.length)*Math.log10((double) negativeSamples/inputData.length)/Math.log10(2));
		}

		return (posEntropy + negEntropy);		
	}
	/**
	 * get sub variance
	 */
	public static double getSVar(int negC, int posC, int totalC)
	{
		if(totalC == 0)
		{
			return 0;
		}
		double variance = 0;
		double posVar = 0;
		double negVar = 0;
		int positiveSamples = posC, negativeSamples = negC, total = totalC;
		if(positiveSamples == 0)
		{
			posVar = 0;
		}
		else
		{
			posVar = ((double)positiveSamples/total);
		}
		if(negativeSamples == 0)
		{
			negVar = 0;
		}
		else
		{
			negVar = ((double)negativeSamples/total);
		}

		return (posVar*negVar);
	}
	/**
	 * get variance
	 */
	public static double getVariance(int[][] inputData)
	{
		double variance = 0;
		double posVar = 0;
		double negVar = 0;
		int positiveSamples = 0, negativeSamples = 0;
		int totalSamples = inputData.length;
		for(int c = 0; c < inputData.length; c++)
		{
			if(inputData[c][inputData[c].length - 1] == 1)
			{
				positiveSamples++;
			}
			else
			{
				negativeSamples++;
			}
		}
		if(positiveSamples == 0)
		{
			posVar = 0;
		}
		else
		{
			posVar = ((double)positiveSamples/totalSamples);
		}
		if(negativeSamples == 0)
		{
			negVar = 0;
		}
		else
		{
			negVar = ((double)negativeSamples/totalSamples);
		}
		return (posVar*negVar);
	}
	/**
	 * call to create pruned decision tree
	 */
	public static Map<Integer, String> prunedDecisionTree()
	{
		double originalAccuracy = checkAccuracy(getDecisionTree(), validationData); 
		Map<Integer, String> cloneDecisionTree = new LinkedHashMap<Integer, String>();
		cloneDecisionTree.putAll(getDecisionTree());
		List<Integer> nodesToPrune = getNodesToPrune(cloneDecisionTree);
		int nodesToPruneSize = nodesToPrune.size();
		int i = 0;	
		for(i = 0; i < nodesToPruneSize; i++)	
		{
			int location = nodesToPrune.get(i);
			int highClassGain = MajorityClass.get(location);
			Map<Integer, String> beforePruneTree = new LinkedHashMap<Integer, String>();
			beforePruneTree.clear();
			beforePruneTree.putAll(cloneDecisionTree);
			cloneDecisionTree.put(location, String.valueOf(highClassGain));
			nodesToPrune.remove((Integer)location);
			createPruneTree(cloneDecisionTree, nodesToPrune, location);
			nodesToPruneSize = nodesToPrune.size();
			double prunedAccuracy = checkAccuracy(cloneDecisionTree, validationData);
			Map<Integer, String> afterPruneTree = new LinkedHashMap<Integer, String>();
			afterPruneTree.clear();
			afterPruneTree.putAll(cloneDecisionTree);
			if(originalAccuracy > prunedAccuracy)
			{
				cloneDecisionTree.clear();
				cloneDecisionTree.putAll(beforePruneTree);	
			}
			else
			{
				cloneDecisionTree.clear();
				cloneDecisionTree.putAll(afterPruneTree);
			}
		}
		return cloneDecisionTree;
	}
	/**
	 * create prune tree
	 */
	public static void createPruneTree(Map<Integer, String> newDecisionTree, List<Integer> pruneList, int location)
	{
		Integer rightSubTree = 2*location + 1;
		Integer leftSubTree = 2*location;
		if(newDecisionTree.containsKey(leftSubTree))
		{
			newDecisionTree.remove(leftSubTree);
			pruneList.remove(leftSubTree);
			createPruneTree(newDecisionTree, pruneList, leftSubTree);
		}
		if(newDecisionTree.containsKey(rightSubTree))
		{
			newDecisionTree.remove(rightSubTree);
			pruneList.remove(rightSubTree);
			createPruneTree(newDecisionTree, pruneList, rightSubTree);
		}
	}
	/**
	 *get non leaf nodes
	 */
	public static List<Integer> getNodesToPrune(Map<Integer, String> cloneDecisionTree)
	{
		int initialLocationNode = 1;
		getNonLeafs(cloneDecisionTree, initialLocationNode);
		return pruneNonLeafNodes;

	}
	public static void getNonLeafs(Map<Integer, String> cloneDecisionTree, int positionNode)
	{
		if(cloneDecisionTree.get(positionNode).equalsIgnoreCase("0") || cloneDecisionTree.get(positionNode).equalsIgnoreCase("1"))
		{
			return;
		}
		else
		{
			getNonLeafs(cloneDecisionTree, positionNode*2);
			getNonLeafs(cloneDecisionTree, positionNode*2 + 1);
			pruneNonLeafNodes.add(positionNode);
		}
	}
	public static void getNonLeafsInTree(Map<Integer, String> cloneDecisionTree, int positionNode)
	{
	
		if(cloneDecisionTree.containsKey(positionNode))
		{
				if(cloneDecisionTree.containsKey(2 * positionNode) || cloneDecisionTree.containsKey(2 * positionNode + 1))
				{
					if((cloneDecisionTree.get(2 * positionNode).equalsIgnoreCase("0") || cloneDecisionTree.get(2 * positionNode).equalsIgnoreCase("1"))
				&& (cloneDecisionTree.get(2 * positionNode).equalsIgnoreCase("0") || cloneDecisionTree.get(2 * positionNode).equalsIgnoreCase("1")))
					{
						if(positionNode%2==0)
						{
							pruneNonLeafNodes.add(positionNode);
						}
						else
						{
							pruneNonLeafNodes.add(positionNode);
						}
					}
					else
					{
						getNonLeafsInTree(cloneDecisionTree, positionNode*2);
						getNonLeafsInTree(cloneDecisionTree, positionNode*2+1);
					}
				}
				else
				{
					 pruneNonLeafNodes.add(positionNode);
				}
			if(!pruneNonLeafNodes.contains(1)) {
				pruneNonLeafNodes.add(1);
			}
		} 
	}
	/**
	 * call to create pruned decision tree variance
	 */
	public static Map<Integer, String> prunedDecisionTreeVar()
	{
		double originalAccuracyV = checkAccuracy(getDecisionTreeVariance(), validationData); 
		Map<Integer, String> cloneDecisionTreeV = new LinkedHashMap<Integer, String>();
		cloneDecisionTreeV.putAll(getDecisionTreeVariance());
		List<Integer> nodesToPruneV = getNodesToPruneVar(cloneDecisionTreeV);
		int nodesToPruneSizeV = nodesToPruneV.size();
		int iV = 0;	
		for(iV = 0; iV < nodesToPruneSizeV; iV++)	
		{
			int locationV = nodesToPruneV.get(iV);
			int highClassGainV = MajorityClassVariance.get(locationV);
			Map<Integer, String> beforePruneTreeV = new LinkedHashMap<Integer, String>();
			beforePruneTreeV.clear();
			beforePruneTreeV.putAll(cloneDecisionTreeV);
			cloneDecisionTreeV.put(locationV, String.valueOf(highClassGainV));
			nodesToPruneV.remove((Integer)locationV);
			createPruneTreeVar(cloneDecisionTreeV, nodesToPruneV, locationV);
			nodesToPruneSizeV = nodesToPruneV.size();
			double prunedAccuracyV = checkAccuracy(cloneDecisionTreeV, validationData);
			Map<Integer, String> afterPruneTreeV = new LinkedHashMap<Integer, String>();
			afterPruneTreeV.clear();
			afterPruneTreeV.putAll(cloneDecisionTreeV);
			if(originalAccuracyV > prunedAccuracyV)
			{
				cloneDecisionTreeV.clear();
				cloneDecisionTreeV.putAll(beforePruneTreeV);	
			}
			else
			{
				cloneDecisionTreeV.clear();
				cloneDecisionTreeV.putAll(afterPruneTreeV);
			}
		}
		return cloneDecisionTreeV;
	}
	public static void createPruneTreeVar(Map<Integer, String> newDecisionTreeV, List<Integer> pruneListV, int locationV)
	{
		Integer rightSubTreeV = 2*locationV + 1;
		Integer leftSubTreeV = 2*locationV;
		if(newDecisionTreeV.containsKey(leftSubTreeV))
		{
			newDecisionTreeV.remove(leftSubTreeV);
			pruneListV.remove(leftSubTreeV);
			createPruneTreeVar(newDecisionTreeV, pruneListV, leftSubTreeV);
		}
		if(newDecisionTreeV.containsKey(rightSubTreeV))
		{
			newDecisionTreeV.remove(rightSubTreeV);
			pruneListV.remove(rightSubTreeV);
			createPruneTreeVar(newDecisionTreeV, pruneListV, rightSubTreeV);
		}
	}
	public static List<Integer> getNodesToPruneVar(Map<Integer, String> cloneDecisionTreeV)
	{
		int initialLocationNode = 1;
		getNonLeafsForVariance(cloneDecisionTreeV, initialLocationNode);
		return pruneNonLeafNodesForVariance;
	}
	public static void getNonLeafsForVariance(Map<Integer, String> cloneDecisionTreeV, int positionNode)
	{
		if(cloneDecisionTreeV.get(positionNode).equalsIgnoreCase("0") || cloneDecisionTreeV.get(positionNode).equalsIgnoreCase("1"))
		{
			return;
		}
		else
		{
			getNonLeafsForVariance(cloneDecisionTreeV, positionNode*2);
			getNonLeafsForVariance(cloneDecisionTreeV, positionNode*2 + 1);
			pruneNonLeafNodesForVariance.add(positionNode);
		}
	}

	/**
	 * additional getter or setter
	 */	
	public static Map<Integer, String> getDecisionTree()
	{
		return DecisionTree;
	}
	public static void setDecisionTree(Map<Integer, String> levelBasedDecisionTree)
	{
		DecisionTree = levelBasedDecisionTree;
	}
	public static Map<Integer, String> getDecisionTreeVariance()
	{
		return Main.DecisionTreeVariance;
	}
	public static void setDecisionTreeVariance(Map<Integer, String> varBasedDecisionTree)
	{
		Main.DecisionTreeVariance = varBasedDecisionTree;
	}
	public static Map<Integer, String> getPruneDecisionTree()
        {
                return DecisionTreePrune;
        }
        public static void setPruneDecisionTree(Map<Integer, String> levelBasedDecisionTree)
        {
                DecisionTreePrune = levelBasedDecisionTree;
        }
        public static Map<Integer, String> getPruneDecisionTreeVariance()
        {
                return Main.DecisionTreeVariancePrune;
        }
        public static void setPruneDecisionTreeVariance(Map<Integer, String> varBasedDecisionTree)
        {
                Main.DecisionTreeVariancePrune = varBasedDecisionTree;
        }

}
