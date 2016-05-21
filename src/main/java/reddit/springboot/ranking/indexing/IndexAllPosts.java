package reddit.springboot.ranking.indexing;

import java.util.ArrayList;
import reddit.springboot.ranking.cleaner.SerializeRedditPost;
import reddit.springboot.ranking.models.RedditPost;

public class IndexAllPosts {
    
    public static void indexPath(){
        ArrayList<RedditPost> posts = SerializeRedditPost.getRedditPosts();
        Indexer indexer = new Indexer("./index");
        indexer.initWriter();
        for(RedditPost post : posts){
            indexer.indexReddit(post);
        }
        indexer.closeWriter();
    }
    
    public static void main(String[] args){
        IndexAllPosts.indexPath();
    }
    
}
