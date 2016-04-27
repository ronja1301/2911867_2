package assignment2;

import javax.servlet.http.HttpServlet;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;


import javax.servlet.http.*;

@SuppressWarnings("serial")
public abstract class DropboxUtilityServlet extends HttpServlet {
	
	protected User getCurrentUser() {
		UserService us = UserServiceFactory.getUserService();
		return us.getCurrentUser();
	}

	protected String getCurrentPath(HttpServletRequest req) {
		String currentPath = (String) req.getSession().getAttribute(
				"currentPath");
		if (currentPath != null && currentPath.length() > 0) {
		} else {
			currentPath = "/";
			req.getSession().setAttribute("currentPath", currentPath);
		}
		return currentPath;
	}

	protected DropboxDirectory generateDirectory(String path) {

		User u = getCurrentUser();
		if (u==null) //User is not logged in 
		{
			System.err
					.println("User is not logged in!");
			return null;
		}
		
		Key dir_key = KeyFactory.createKey("DropboxDirectory", getCurrentUser().getUserId() + path);
		DropboxDirectory dir = new DropboxDirectory(dir_key, path);
		return dir;
	}
}
