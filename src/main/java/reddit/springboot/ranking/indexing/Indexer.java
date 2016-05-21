package reddit.springboot.ranking.indexing;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Component;

import reddit.springboot.ranking.cleaner.SerializeRedditPost;
import reddit.springboot.ranking.models.RedditPost;
import reddit.springboot.ranking.ranking.Ranker;

@Component
public class Indexer {
    private StandardAnalyzer analyzer;
    private IndexWriter writer;
    private IndexReader reader;
    private IndexSearcher searcher;
    private TopScoreDocCollector docCollector;
    private String indexDirectoryPath;
    private static ArrayList<RedditPost> redditPosts;
    private HashMap<String, RedditPost> redditPostMap;
    public Indexer() {
        if(redditPostMap == null){
            redditPostMap = SerializeRedditPost.getRedditIdRedditPostMap(SerializeRedditPost.getRedditPosts());
        }
    }
    
    public Indexer(String indexDirectoryPath) {
        this.indexDirectoryPath = indexDirectoryPath;
        if(redditPostMap == null){
            redditPostMap = SerializeRedditPost.getRedditIdRedditPostMap(SerializeRedditPost.getRedditPosts());
        }
    }
    
    
    
    public void initWriter(){
        try {
            Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
            analyzer = new StandardAnalyzer();
            IndexWriterConfig indexConfig = new IndexWriterConfig(analyzer);
            writer = new IndexWriter(indexDirectory , indexConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void closeWriter(){
        try {
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void indexReddit(RedditPost post){
        
        try{
            Document doc = getDocument(post);
            try {
                writer.addDocument(doc);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }catch(TitleIDNullException e){
            e.printStackTrace();
        }
        
    }

    public void indexRedditList(List<RedditPost> posts){
        for(RedditPost post : posts){
            indexReddit(post);
        }
    }
    
    public ArrayList<RedditPost> search(String queryString){
        
        ArrayList<RedditPost> redditPosts = new ArrayList<RedditPost>();
        Directory indexDirectory;
        
        try {
            indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
            analyzer = new StandardAnalyzer();
            reader = DirectoryReader.open(indexDirectory);
            searcher = new IndexSearcher(reader);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        Query q;
        try {
            q = new QueryParser("title", analyzer).parse(queryString);
            TopDocs topDocs = searcher.search(q, 100);
            for (ScoreDoc doc : topDocs.scoreDocs) {
                System.out.println(" >> title " + searcher.doc(doc.doc).get("title"));
                System.out.println(" >> id " + searcher.doc(doc.doc).get("id"));
                String id = searcher.doc(doc.doc).get("id");
                if(redditPostMap.keySet().contains(id)){
                    redditPosts.add(redditPostMap.get(id));
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } 
        
        try {
            reader.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        new Ranker().getRankedRedditPost(redditPosts);
        return redditPosts;
    }
    
    private Document getDocument(RedditPost post) throws TitleIDNullException{
        Document document = new Document();
        if(post.getTitle()==null || post.getId() == null || post.getSelfText() == null){
            throw new TitleIDNullException();
        }else{
            Field contentField = new Field("title", post.getTitle(), Field.Store.YES, Field.Index.ANALYZED);
            Field redditPostId = new Field("id", post.getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
            //Field redditSelfText = new Field("self_text", post.getSelfText(), Field.Store.YES, Field.Index.NOT_ANALYZED);
            document.add(contentField);
            document.add(redditPostId);
            //document.add(redditSelfText);
            return document;
        }
    }
}
