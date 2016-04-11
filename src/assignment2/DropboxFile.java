package assignment2;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class DropboxFile {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;
	
	@Persistent
	private String name;
	
	@Persistent
	private DropboxDirectory owner;
	
	@Persistent
	private BlobKey blob;
	
	public DropboxFile (String name, BlobKey blob, DropboxDirectory owner){
		this.name = name;
		this.blob = blob;
		this.owner = owner;
	}

	public String getName() {
		return name;
	}

	public BlobKey getBlobKey() {
		return blob;
	}
}
