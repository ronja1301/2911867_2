package assignment2;

import javax.jdo.PersistenceManager;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;

public class Datastore {
	
	private static PersistenceManager pm;
	
	public static void writeElement(Object element){
		if(pm == null){
			pm = PMF.get().getPersistenceManager();
		}
		pm.makePersistent(element); //writes element to data store
		
		if(pm==null)
		{
			pm.close();
		}
	}
	
	public static void deleteElement(Object element){
		if(pm == null){
			pm = PMF.get().getPersistenceManager();
		}
		pm.deletePersistent(element); //deletes element from data store
		if(pm==null)
		{
			pm.close();
		}
	}
	
	public static DropboxDirectory readDirectory(User u, String path) //tries to read a directory
	{
		if(pm == null){
			pm = PMF.get().getPersistenceManager();
		}
	
		if (u == null) //user is not logged in 
		{
			System.err.println("User is null!");
			return null;
		}
		Key dir_key = KeyFactory.createKey("DropboxDirectory",u.getUserId() + path); //creates key for directory
		
		DropboxDirectory dir = null;
		try {
			dir = pm.getObjectById(DropboxDirectory.class, dir_key); //tries to find the directory with the given user and path
		} catch (Exception e) {
			
			System.err.println("Directory " + path + " could not be loaded!");
			return null;
		}
		return dir;
	}
	
	public static DropboxUser readDropboxUser(User u) //tries to read a user
	{
		if(pm == null){
			pm = PMF.get().getPersistenceManager();
		}
	
		if (u == null) //user is not logged in 
		{
			System.err.println("User is null!");
			return null;
		}
		Key user_key = KeyFactory.createKey("DropboxUser", u.getUserId()); //creates key for user
		DropboxUser du = null;		
		try {
			du = pm.getObjectById(DropboxUser.class, user_key); //tries to find the user with the created key
		} catch (Exception e) {
			System.err.println("User could not be loaded!");
			return null;
		}
		return du;
	}
}
