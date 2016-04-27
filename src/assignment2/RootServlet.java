package assignment2;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class RootServlet extends DropboxUtilityServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		req.setAttribute("message", req.getParameter("message"));
		
		resp.setContentType("text/html");
		this.setUserServiceParameters(req);
		
		if (getCurrentUser()!=null) //user is logged in 
		{
			//initialize dropbox user if not already done
			initializeDropboxUser();
			
			//load current directory
			DropboxDirectory currentDir = Datastore.readDirectory(getCurrentUser(), getCurrentPath(req));

			
			//display subDirectories and fileNames
			try
			{
				List<String> subDirectories = currentDir.directories();
				req.setAttribute("subDirectories", subDirectories);
				req.setAttribute("fileNames", currentDir.files());
			}
			catch (Exception e)
			{
				System.out.println("Subdirectories could not be loaded!");
			}
			
			//List<String> fileNames = currentDirectory.fileNames();			
			
			//current directory
			req.setAttribute("currentPath", getCurrentPath(req));
			
			//uploadURL for uploading blobs
			req.setAttribute("uploadURL",BlobstoreServiceFactory.getBlobstoreService().createUploadUrl("/files"));
		}
		else
		{
			HttpSession session = req.getSession(false);
			if(req.getSession(false)!=null)
			{
				session.invalidate();
				req.getSession(true);
				req.getSession().setAttribute("currentPath", "/");
			}
		}
		
		// forward on the request to the JSP
		RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/root.jsp");
		rd.forward(req, resp);
	}
	
	private void initializeDropboxUser(){
		//if user is not set up
		if(Datastore.readDropboxUser(getCurrentUser())== null){
			//create new user and write him to the data store
			Key user_key = KeyFactory.createKey("DropboxUser", getCurrentUser().getUserId());
			DropboxUser du = new DropboxUser(user_key);
			Datastore.writeElement(du);
			
			//create root directory and write it to data store
			DropboxDirectory rootDir = generateDirectory("/");
			Datastore.writeElement(rootDir);
		}
	}
	
	private void setUserServiceParameters(HttpServletRequest req) {
		// we need to get access to the google user service
		UserService us = UserServiceFactory.getUserService();
		String login_url = us.createLoginURL("/");
		String logout_url = us.createLogoutURL("/");

		// attach a few things to the request such that we can access them in
		// the jsp
		req.setAttribute("user", getCurrentUser());
		req.setAttribute("login_url", login_url);
		req.setAttribute("logout_url", logout_url);
	}
}
