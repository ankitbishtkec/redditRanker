package reddit.springboot.ranking.indexing;

import java.util.ArrayList;

import reddit.springboot.ranking.importdata.ReadAllCsv;
import reddit.springboot.ranking.models.RedditPost;

public class IndexAllPosts {
    
    private String dirPath;
    private ReadAllCsv readAllCsv;
    private Indexer indexer;
    
    public void IndexAllPosts(String dirPath, String indexDirPath){
        this.dirPath = dirPath;
        this.indexer = new Indexer(indexDirPath);
    }
    
    public void index(){
        ArrayList<RedditPost> posts = readAllCsv.readAllCSV(dirPath);
        for(RedditPost post : posts){
            this.indexer.indexReddit(post);
        }
    }
}
