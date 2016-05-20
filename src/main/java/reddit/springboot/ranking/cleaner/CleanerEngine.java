package reddit.springboot.ranking.cleaner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import reddit.springboot.ranking.importdata.ReadCsv;
import reddit.springboot.ranking.models.RedditPost;

public class CleanerEngine {


	//private HashMap<String, RedditPost> redditIdPostMap = new HashMap<String, RedditPost>();
	//private HashMap<String, ArrayList<String>> authorPostIDMap = new HashMap<String, ArrayList<String>>();
	//private HashMap<String, ArrayList<String>> domainPostIdMap = new HashMap<String, ArrayList<String>>();
	private ArrayList<RedditPost> posts = new ArrayList<RedditPost>();
	private HashMap<String, Integer> authorDownCount;
	private HashMap<String, Integer> authorUpCount;
	private HashMap<String, Integer> authorNumCount;
	private HashMap<String, Integer> domainDownCount;
	private HashMap<String, Integer> domainUpCount;
	private HashMap<String, Integer> domainNumComment;
	
	
	public CleanerEngine(String filename) {
		//String filename = "./dataset/4x4.csv";
		posts = new ReadCsv().readCsv(new File(filename));
		this.authorDownCount = createAuthorDownCount();
		this.authorUpCount = createAuthorUpCount();
		this.authorNumCount = createAuthorNumComment();
		this.domainDownCount = createDomainDownCount();
		this.domainUpCount = createDomainUpCount();
		this.domainNumComment = createDomainNumCount();
	}
	
	private HashMap<String, Integer> createAuthorDownCount(){
		HashMap<String, Integer> authorDownCount = new HashMap<String, Integer>();
		for (RedditPost post : posts){
			if(!authorDownCount.containsKey(post.getAuthor())){
				authorDownCount.put(post.getAuthor(), 0);
			}
			authorDownCount.put(post.getAuthor(), authorDownCount.get(post.getAuthor()) + post.getDowns());
		}
		return authorDownCount;
	}
	
	private HashMap<String, Integer> createAuthorUpCount(){

		HashMap<String, Integer> authorUpCount = new HashMap<String, Integer>();
		for (RedditPost post : posts){
			if(!authorUpCount.containsKey(post.getAuthor())){
				authorUpCount.put(post.getAuthor(), 0);
			}
			authorUpCount.put(post.getAuthor(), authorUpCount.get(post.getAuthor()) + post.getUps());
		}
		return authorUpCount;
	}
	
	private HashMap<String, Integer> createAuthorNumComment(){

		HashMap<String, Integer> authorNumComment = new HashMap<String, Integer>();
		for (RedditPost post : posts){
			if(!authorNumComment.containsKey(post.getAuthor())){
				authorNumComment.put(post.getAuthor(), 0);
			}
			authorNumComment.put(post.getAuthor(), authorNumComment.get(post.getAuthor()) + post.getNumComments());
		}
		return authorNumComment;
	}
	
	private HashMap<String, Integer> createDomainDownCount(){

		HashMap<String, Integer> domainDownCount = new HashMap<String, Integer>();
		for (RedditPost post : posts){
			if(!domainDownCount.containsKey(post.getDomain())){
				domainDownCount.put(post.getDomain(), 0);
			}
			domainDownCount.put(post.getDomain(), domainDownCount.get(post.getDomain()) + post.getDowns());
		}

		return domainDownCount;
	}
	
	private HashMap<String, Integer> createDomainUpCount(){
		HashMap<String, Integer> domainUpCount = new HashMap<String, Integer>();
		for (RedditPost post : posts){
			if(!domainUpCount.containsKey(post.getDomain())){
				domainUpCount.put(post.getDomain(), 0);
			}
			domainUpCount.put(post.getDomain(), domainUpCount.get(post.getDomain()) + post.getUps());
		}
		return domainUpCount;
	}
	
	private HashMap<String, Integer> createDomainNumCount(){
		HashMap<String, Integer> domainNumCount = new HashMap<String, Integer>();
		for (RedditPost post : posts){
			if(!domainNumCount.containsKey(post.getDomain())){
				domainNumCount.put(post.getDomain(), 0);
			}
			domainNumCount.put(post.getDomain(), domainNumCount.get(post.getDomain()) + post.getDowns());
		}
		return domainNumCount;
	}

	public static void main(String[] args){

		String filename = "./dataset/4x4.csv";
		String filenameFeature = "./feature/4x4.csv";
		CleanerEngine engine = new CleanerEngine(filename);
		ArrayList<String> features = new ArrayList<String>();
		features.add("num_comment");
		features.add("domain_influence");
		features.add("author_influence");
		features.add("created_utc");
		features.add("title_length");
		writeFeatures(engine, "./feature/4x4_down.csv", features, "downs");
		writeFeatures(engine, "./feature/4x4_up.csv", features, "ups");
	}
	
	private static void writeFeatures(CleanerEngine engine, String filenameFeature, ArrayList<String> features, String upOrDown){

		File fileFeature = new File(filenameFeature);
		if(!fileFeature.exists()){
			try {
				fileFeature.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		BufferedWriter br  = null;
		try {
			br = new BufferedWriter(new FileWriter(fileFeature));
			//br.write("num_comment,ups\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		// first line
		String line = "";
		if(features.contains("num_comment")){
			line = "num_comment";
		}
		
		if(features.contains("domain_influence")){
			line = line + "," + "domain_influence";
		}
		
		if(features.contains("author_influence")){
			line = line + "," + "author_influence";
		}
		
		if(upOrDown.equals("ups")){
			line = line + "," + "ups";
		}else if(upOrDown.equals("downs")){
			line = line + "," + "downs";
		}
		
		line = line + "\n";
		
		try {
			br.write(line);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		for(RedditPost post : engine.posts){
			if(features.contains("num_comment")){
				line = "" + post.getNumComments();
			}
			
			if(features.contains("domain_influence")){
				line = line + "," + (engine.domainUpCount.get(post.getDomain()) - engine.domainDownCount.get(post.getDomain()));
			}
			
			if(features.contains("author_influence")){
				line = line + "," + (engine.authorUpCount.get(post.getAuthor()) - engine.authorDownCount.get(post.getAuthor()));
			}

			if(upOrDown.equals("ups")){
				line = line + "," + post.getUps();
			}else if(upOrDown.equals("downs")){
				line = line + "," + post.getDowns();
			}
			
			line = line + "\n";
			try {
				br.write(line);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			br.flush();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void writeFeatureUp(ArrayList<RedditPost> posts, String filenameFeature, ArrayList<String> features){
		File fileFeature = new File(filenameFeature);
		
		if(!fileFeature.exists()){
			try {
				fileFeature.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		BufferedWriter br = null;
		try {
			br = new BufferedWriter(new FileWriter(fileFeature));
			br.write("num_comment,ups\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(RedditPost post : posts){
			String line = "" + post.getNumComments();
			line = line + "," + post.getUps();
			line = line + "\n";
			try {
				br.write(line);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			br.flush();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
