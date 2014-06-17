import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class TestQlearning
{
	
	
	/*private UserProfile aProfile;
	
	@Before
	public void setUp()
	{
		aProfile = new UserProfile();
		aProfile.addPreferredMP("stephen.harper@parl.gc.ca");
		aProfile.addInterest("fishing");
		Capone.getInstance().addProfile(aProfile);
	}
	
	@Test
	public void testGetParliament()
	{
		Object anObject = Capone.getParliament();
		assertEquals(Parliament.class, anObject.getClass());
	}
	
	@Test
	public void testGetAddProfile()
	{
		Capone.getInstance().addProfile(aProfile);
		UserProfile p = Capone.getInstance().getProfile();
		assertEquals(true, p.getInterests().contains("fishing"));
		assertEquals(true, p.getPreferredMPs().contains("stephen.harper@parl.gc.ca"));
	}
	
	@Test
	public void testUpdateParliament()
	{
		Capone.getInstance().updateParliament("data-snapshot-since-june-2013", false);
		
		MP stephen = Capone.getParliament().getMP("stephen.harper@parl.gc.ca");
		
		assertEquals(true, stephen != null);
	}
	
	@Test
	public void testRecommendSpeeches()
	{
		ParliamentLoader loader = new OpenParliamentFileLoader("data-snapshot-before-june-2013");
		loader.loadAllMps(Capone.getParliament());
		
		ArrayList<RecommendedSpeech> sp = Capone.getInstance().recommendSpeeches(new ContentBasedRecommender());
		
		assertEquals(true, sp != null);
	}
	
	@Test
	public void testPersistAndLoadData()
	{
		//persist aUserProfile in JSON format
		Capone.getInstance().persistData(new PersistJSON());
		
		//change the user profile to be null and try to persist it
		Capone.getInstance().addProfile(null);
		Capone.getInstance().persistData(new PersistBinary());		
		
		//retrieve aUserProfile from JSON
		Capone.getInstance().loadProfile(new PersistJSON());
		
		UserProfile p = Capone.getInstance().getProfile();
		
		assertEquals(true, p.getInterests().contains("fishing"));
		assertEquals(true, p.getPreferredMPs().contains("stephen.harper@parl.gc.ca"));
		
	}*/
}
