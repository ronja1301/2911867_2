package assignment2;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

@SuppressWarnings("serial")
public class FilesServlet extends DropboxUtilityServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String message;
		
		BlobstoreService blobstoreService = BlobstoreServiceFactory
				.getBlobstoreService(); // load blobstore service

		Map<String, List<BlobKey>> files_sent = blobstoreService
				.getUploads(req); // extract uploaded file

		try
		{
			BlobKey b = files_sent.get("file").get(0); // create blob key
			
			String fileName = blobstoreService.getBlobInfos(req).get("file").get(0)
					.getFilename(); // get file name

			DropboxDirectory dir = Datastore.readDirectory(getCurrentUser(),
					getCurrentPath(req)); // load parent directory

			DropboxFile file = new DropboxFile(fileName, b, dir); // create new
																	// dropbox file

			dir.addFile(file); // add file

			Datastore.writeElement(file);
			Datastore.writeElement(dir);

			message = "File " + fileName + " was added to directory "
					+ dir.getDirName() + "!";
		}
		catch (NullPointerException e)
		{
			message = "Please select a file!";
		}
		
		req.setAttribute("message", message);
		resp.sendRedirect("/?message=" + req.getAttribute("message")); // redirect
																		// to
																		// root
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		String message;

		String value = req.getParameter("button"); // Which button has been
													// pressed?
		if (value == null) {
			resp.sendRedirect("/");
		} else if (value.compareToIgnoreCase("delete") == 0) {
			message = deleteFile(req);
			req.setAttribute("message", message);
			resp.sendRedirect("/?message=" + req.getAttribute("message"));
		} else if (value.compareToIgnoreCase("download") == 0) {
			downloadFile(req, resp);
		}
	}

	
	private String deleteFile(HttpServletRequest req) {
		String message = null;
		String fileName = req.getParameter("hidden_value"); // get filename
		DropboxDirectory dir = Datastore.readDirectory(getCurrentUser(),
				getCurrentPath(req)); // load directory

		DropboxFile file = null;
		List<DropboxFile> files = dir.files(); // all files of current directory
		for (int i = 0; i < files.size(); i++) {
			if (files.get(i).getName().compareTo(fileName) == 0) {
				file = files.get(i);
			}
		}
		if (file == null) {
			message = "File " + fileName + " has not been found!";
			return message;
		}
		BlobstoreServiceFactory.getBlobstoreService().delete(file.getBlobKey()); // delete
																					// file
		dir.deleteFile(file);
		Datastore.writeElement(file);
		Datastore.writeElement(dir);
		message = "File " + fileName + " has been deleted from "
				+ dir.getDirName() + "!";
		return message;
	}

	private void downloadFile(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		// get currentPath
		// get fileName
		String fileNameString = (String) req.getParameter("hidden_value");
		// load directory
		DropboxDirectory directory = Datastore.readDirectory(getCurrentUser(),
				getCurrentPath(req));

		// get file object
		List<DropboxFile> files = directory.files();
		for (int i = 0; i < files.size(); i++) {
			if (files.get(i).getName().compareTo(fileNameString) == 0) {
				// get BlobKey
				BlobKey key = files.get(i).getBlobKey();
				BlobstoreServiceFactory.getBlobstoreService().serve(key, resp);
				return;
			}
		}
	}
}
