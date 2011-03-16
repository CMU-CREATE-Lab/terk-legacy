package RSSReaders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * <code>KeywordMatchingNewsReader</code> returns the number of headlines (since the last update) containing one or more
 * occurrences of a keyword.  This reader only works with news feeds which include a publication date with each entry.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class KeywordMatchingNewsReader implements NewsHeadlineCounter
   {
   /** The default URL for the news feed. */
   private static final String DEFAULT_FEED_URL = "http://newsrss.bbc.co.uk/rss/newsonline_uk_edition/world/rss.xml";

   private final RSSReader rssReader;
   private final Set<String> keywords;
   private long lastEventTimestamp;

   /**
    * Constructs a <code>KeywordMatchingNewsReader</code> using the {@link #DEFAULT_FEED_URL default feed URL} and (a
    * copy of) the given set of keywords.
    */
   public KeywordMatchingNewsReader(final Set<String> keywords)
      {
      this(DEFAULT_FEED_URL, keywords);
      }

   /**
    * Constructs a <code>KeywordMatchingNewsReader</code> using the given URL and (a copy of) the given set of keywords.
    * If the given feed URL is <code>null</code> or empty, the {@link #DEFAULT_FEED_URL default feed URL} is used
    * instead.
    */
   public KeywordMatchingNewsReader(final String feedUrl, final Set<String> keywords)
      {
      this.rssReader = new RSSReader((feedUrl != null) ? feedUrl : DEFAULT_FEED_URL);
      if ((keywords != null) && (!keywords.isEmpty()))
         {
         this.keywords = new HashSet<String>(keywords);
         }
      else
         {
         this.keywords = new HashSet<String>();
         }
      }

   /**
    * Update the feed and return the number of new headlines (since the last update) containing one or more of the
    * keywords.
    */
   public int getHeadlineCount()
      {
      return getEntries().size();
      }

   /**
    * Updates the feed and returns the new headline entries (since the last update) which contain one or more instances
    * of a keyword.  May return an empty {@link List}, but guaranteed to not return null.
    */
   public List<FeedEntry> getEntries()
      {
      final List<FeedEntry> matchingEntries = new ArrayList<FeedEntry>();

      final long currentTime = System.currentTimeMillis();

      // update the feed
      rssReader.updateFeed();

      final List<FeedEntry> entries = rssReader.getEntriesPublishedAfterTimestamp(lastEventTimestamp);

      if ((entries != null) && (!entries.isEmpty()))
         {
         lastEventTimestamp = currentTime;

         // now try to find entries that contain our keywords
         for (final FeedEntry entry : entries)
            {
            if (doesEntryContainKeyword(entry))
               {
               matchingEntries.add(entry);
               }
            }
         }

      return matchingEntries;
      }

   private boolean doesEntryContainKeyword(final FeedEntry entry)
      {
      final String headline = entry.getTitle().toLowerCase();
      if (headline != null)
         {
         // split on non-letters to obtain a set of words in the title
         final Set<String> wordSet = new HashSet<String>(Arrays.asList(headline.split("[^a-z]")));

         // check whether each word is a keyword, and return true immediately if so
         for (final String word : wordSet)
            {
            if (keywords.contains(word))
               {
               return true;
               }
            }
         }
      return false;
      }
   }
