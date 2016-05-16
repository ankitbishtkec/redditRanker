package reddit.springboot.ranking.controller;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reddit.springboot.ranking.crawler.RedditCrawler;
import reddit.springboot.ranking.models.RedditPost;

@RestController
@RequestMapping(value="/reddit")
public class RedditPostController {

    @RequestMapping(value="/search", method=RequestMethod.GET)
    public ArrayList<RedditPost> searchReddit(@RequestParam(value="search", required=false, defaultValue="Politics") String searchString) {
        //ModelAndView mav = new ModelAndView("shop-new", "shop", new Shop());
        //return mav;
        System.out.println("search string >> " + searchString);
        ArrayList<RedditPost> posts = new ArrayList<RedditPost>();
        return posts;
    }
    
    @RequestMapping(value="/latestsubreddits", method=RequestMethod.GET)
    public ArrayList<RedditPost> getLatestPosts() {
        ArrayList<RedditPost> posts = new ArrayList<RedditPost>();
        return posts;
    }
    
    @RequestMapping(value="/test", method=RequestMethod.GET)
    public ArrayList<RedditPost> testReddit() {
        ArrayList<RedditPost> posts = new ArrayList<RedditPost>();
        return posts;
    }
    
    @RequestMapping(value="/subreddits", method=RequestMethod.GET)
    public ArrayList<RedditPost> getSubReddits(@RequestParam(value="name", required=false, defaultValue="Politics") String subredditName) {
        ArrayList<RedditPost> posts = new RedditCrawler().crawlReddits(subredditName);
        System.out.println("subbreddit name >> " + subredditName);
        return posts;
    }
}
