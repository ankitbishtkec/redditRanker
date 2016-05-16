package reddit.springboot.ranking.indexing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.stereotype.Component;

import reddit.springboot.ranking.models.RedditPost;

@Component
public class Indexer {
    private StandardAnalyzer analyzer;
    private IndexWriter writer;
    private IndexReader reader;
    private IndexSearcher searcher;
    private TopScoreDocCollector docCollector;
    private String indexDirectoryPath;
    
    public Indexer() {
    }
    
    public Indexer(String indexDirectoryPath) {
        this.indexDirectoryPath = indexDirectoryPath;
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
        Document doc = getDocument(post);
        try {
            writer.addDocument(doc);
        } catch (IOException e) {
            // TODO Auto-generated catch block
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
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        Query q;
        try {
            q = new QueryParser("title", analyzer).parse(queryString);
            TopDocs topDocs = searcher.search(q, 100);
            for (ScoreDoc doc : topDocs.scoreDocs) {
                System.out.println(" >> self text " + searcher.doc(doc.doc).get("title"));
                System.out.println(" >> self text " + searcher.doc(doc.doc).get("id"));
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
        return redditPosts;
    }
    
    private Document getDocument(reddit.springboot.ranking.models.RedditPost post){
        Document document = new Document();
        Field contentField = new Field("title", post.getTitle()
           , Field.Store.YES, Field.Index.ANALYZED);
        Field redditPostId = new Field("id", post.getId(), Field.Store.YES, Field.Index.NOT_ANALYZED);
        document.add(contentField);
        document.add(redditPostId);
        return document;
    }
    
    public static void main(String[] args){
        
        
        
    }
}
