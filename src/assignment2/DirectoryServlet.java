package assignment2;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class DirectoryServlet extends DropboxUtilityServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		
		//detects which button has been pressed
		String value = req.getParameter("button");
		
		if (value == null) //error! 
		{	
			System.err.println("Invalid!");
		} 
		else if (value.compareToIgnoreCase("add") == 0) //add button has been pressed 
		{	
			String message = addDirectory(req);
			req.setAttribute("message", message);
		}
		else if (value.compareToIgnoreCase("change") == 0) //change button has been pressed 
		{
			String message = changeDirectory(req);
			req.setAttribute("message", message);
		}
		else if (value.compareToIgnoreCase("delete") == 0) //delete button has been pressed 
			{
			String message = deleteDirectory(req);
			req.setAttribute("message", message);
			}
		resp.sendRedirect("/?message=" + req.getAttribute("message")); //redirect to root
	}

	private String addDirectory(HttpServletRequest req) {
		String message;
		
		String newDirName = req.getParameter("textfield").trim().replace("/", "") + "/"; //reads the name requested by the user
		
		// load currentDirectory
		DropboxDirectory currentDirectory = Datastore.readDirectory(getCurrentUser(), getCurrentPath(req));
		
		// check if the directory already exists
		if (!currentDirectory.subdirExists(newDirName)) //subdirectory doesn't exist yet
		{
			//Subdirectory is added to parent directory
			currentDirectory.addSubDir(newDirName);
			//DropboxDirectory is created and written to the data store
			DropboxDirectory newDirectory = generateDirectory(getCurrentPath(req) + newDirName);
			Datastore.writeElement(currentDirectory);
			Datastore.writeElement(newDirectory);
			message = "Directory " + newDirName + " was added to parent " + currentDirectory.getDirName() + "!";
			return message;
		}
		message = "Subdirectory " + newDirName + " already exists!";
		return message;
	}

	private String changeDirectory(HttpServletRequest req)
	{
		String destinationDir = req.getParameter("hidden_value");
		if (destinationDir.compareTo("../") == 0) //if hidden_value = ../ user wants to go one level up
		{
			String message;
			String path = getCurrentPath(req).trim();
			String[] split = path.split("/"); //splits the path and eliminates slashes

			String parentPath = "";
			for (int i = 1; i < split.length - 1; i++) {
				parentPath += "/" + split[i];
			}
			parentPath += "/";
			
			req.getSession().setAttribute("currentPath", parentPath);
			message = "You are now in directory " + parentPath + "!";
			return message;
		}
		else //user wants to go one level down
		{
			req.getSession().setAttribute("currentPath", getCurrentPath(req) + destinationDir);
			String message = "You are now in directory " + getCurrentPath(req);
			return message;
		}

	}

	private String deleteDirectory(HttpServletRequest req) {
		String message;
		String destinationDir = req.getParameter("hidden_value"); //hidden_value is set in root.jsp
		try
		{
			DropboxDirectory dir = Datastore.readDirectory(getCurrentUser(), getCurrentPath(req)+destinationDir); //searches for directory
			// check if subDir is empty
			if (dir.isEmpty()) {
				Datastore.deleteElement(dir);
				DropboxDirectory parentDir = Datastore.readDirectory(getCurrentUser(), getCurrentPath(req));
				parentDir.deleteSubDir(destinationDir);
				Datastore.writeElement(parentDir);
				message = "Directory " + destinationDir + " was deleted!";
				return message;
			}
			else
			{
				message = "Directory " + destinationDir + " is not empty!";
				return message;
			}
		}
		catch (Exception e)
		{
			message = "Directory can not be read!";
			return message;
		}
	}
}
