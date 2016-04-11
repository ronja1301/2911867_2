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

	protected boolean isCurrentUser() {
		return getCurrentUser() != null; //returns true if user is logged in
	}

	protected String getCurrentPath(HttpServletRequest req) {
		String currentPath = (String) req.getSession().getAttribute(
				"currentPath");
		if (currentPath != null && currentPath.length() > 0) {
			System.out.println("[getCurrentPath] - Current Path: "+currentPath);
		} else {
			currentPath = "/";
			System.out.println("[getCurrentPath] - Current Path: "+currentPath);
			req.getSession().setAttribute("currentPath", currentPath);
		}
		return currentPath;
	}

	protected void setCurrentPath(HttpServletRequest req, String path) {

		System.out.println("[setCurrentPath] - Current Path: " + path);
		req.getSession().setAttribute("currentPath", path);
	}

	protected DropboxDirectory generateDirectory(String path) {

		if (!isCurrentUser()) //User is not logged in 
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
