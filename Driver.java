import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Driver {
	
	public static String[][] dd ={};
	


	public static void main(String[] args) throws IOException, Exception, TwitterException {
		Scanner Input = new Scanner(System.in);
		String trainText = "", classifyText = "";
		int option =0;
		int size = 0;
		System.out.println();
		BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
		while (option != 3) {
			System.out.println("1. Train Classifier" +"\n"+ "2. Enter Text To Classify" + "\n"+ "3. Exit"
					+"\n"+ ">> PLEASE SELECT FROM OPTION 1-3 <<");

			option = Input.nextInt();
			switch (option) {
			case 1:
				System.out.println("ENTER CATEGORY YOU WISH TO TEACH CLASSFIER ABOUT");
				trainText = bReader.readLine();
				populateCategories(trainText);
				size = populateCategories(trainText).length;
				 dd = copyArray(populateCategories(trainText));
//				String[][] data = new String[size - 1][];
//				IntStream.range(0, size - 1).forEach(i -> data[i] = dd[i + 1][0].toString().toLowerCase().split(" "));
//				String[] label = new String[size - 1];
//				IntStream.range(0, size - 1).forEach(row ->label[row] = dd[row + 1][1]);
//				HashSet<String> vocab = new HashSet<String>();
//				IntStream.range(0, data.length).forEach(row -> IntStream.range(0, data[row].length).forEach(column -> vocab.add(data[row][column])));
				System.out.println("\n");
				
				for(int i =0 ; i <dd.length; i++){
					for(int j =0; j <dd[i].length; j++){
						System.out.println(dd[i][j].toString());
					}
				}
			
				 break;

			case 2:
				String[][] data = new String[size - 1][];
				IntStream.range(0, size - 1).forEach(i -> data[i] = dd[i + 1][0].toString().toLowerCase().split(" "));
				String[] label = new String[size - 1];
				IntStream.range(0, size - 1).forEach(row ->label[row] = dd[row + 1][1]);
				HashSet<String> vocab = new HashSet<String>();
				IntStream.range(0, data.length).forEach(row -> IntStream.range(0, data[row].length).forEach(column -> vocab.add(data[row][column])));
				
				handleCommandLine(new NaiveBayes(data, label, new ArrayList<String>(vocab)));

				break;

			case 3:
				System.exit(0);
				break;

			}
		}

	}

	static void handleCommandLine(NaiveBayes nb) throws IOException, TwitterException {
	
		BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Enter text to classify or Exit");
		String[] values = bReader.readLine().split(" ");
		if (values[0].equals("Exit"))
			System.exit(0);
		else
			System.out.println("\n" + nb.classify(values));

	}

	static String[][] copyArray(String[][] mainArr) {
		String[][] copyArr = new String[mainArr.length+1][];
		int len = mainArr.length;
		System.arraycopy(mainArr, 0, copyArr, 0, len);
//		for (int i = 0; i < copyArr.length; ++i) {
//			copyArr[i] = new String[mainArr[i].length];
//			for (int j = 0; j < copyArr[i].length; ++j) {
//				copyArr[i][j] = mainArr[i][j];
//			}
//		}

		return copyArr;
	}

	static String[][] populateCategories(String text) throws TwitterException {

		ConfigurationBuilder connect2Twitter = new ConfigurationBuilder();
		connect2Twitter.setDebugEnabled(true).setOAuthConsumerKey("uDRdaSrMzHUsTAWV5M95GvlnD")
				.setOAuthConsumerSecret("Z99B5f8PtWlCPc90M0XqqPpRB1KAPT46N4PA9bHA0gDetjvjfF")
				.setOAuthAccessToken("1056288155232419841-qw855TT9lRkXbnX4XnxYyoMCyna4fS")
				.setOAuthAccessTokenSecret("yiDmPs0TrcV961KOHrXu7ZWROgtxogqpkvShKlIGTazAk");
		TwitterFactory tf = new TwitterFactory(connect2Twitter.build());
		Twitter twit = tf.getInstance();

		Query query = new Query(text);
		QueryResult result = twit.search(query);
		List<Status> stat = result.getTweets();
		Iterator<Status> strIterator = stat.iterator();
		String[][] trainingData = new String[stat.size()][2];

		while (strIterator.hasNext()) {
			for (int i = 0; i < stat.size(); i++) {
				trainingData[i][0] = strIterator.next().getText();
				trainingData[i][1] = text;

			}
		}
		
//		for(int i =0 ; i <trainingData.length; i++){
//			for(int j =0; j <trainingData[i].length; j++){
//				System.out.println(trainingData[i][j].toString());
//			}
//		}
//		

		return trainingData;
	}

}