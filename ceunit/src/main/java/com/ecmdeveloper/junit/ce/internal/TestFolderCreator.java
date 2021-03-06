/**
 * 
 */
package com.ecmdeveloper.junit.ce.internal;


import com.filenet.api.constants.PropertyNames;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.exception.ExceptionCode;
import com.filenet.api.property.PropertyFilter;

/**
 * @author ricardo.belfor
 *
 */
public class TestFolderCreator {

	private final ObjectStore objectStore;
	private static PropertyFilter idFilter = new PropertyFilter();
	
	static {
		idFilter.addIncludeProperty(0, null, null, PropertyNames.ID, null);
	}

	public TestFolderCreator(ObjectStore objectStore) {
		this.objectStore = objectStore;
	}

	public Folder create(String path) {
		return create(path, false );
	}
	
	public Folder create(String path, boolean usePropertyFilter) {
		
		try {
			Folder folder = Factory.Folder.fetchInstance(objectStore, path, usePropertyFilter ? idFilter : null);
			return folder;
		} catch (EngineRuntimeException exception) {
			if ( exception.getExceptionCode().equals(ExceptionCode.E_OBJECT_NOT_FOUND ) ) {
				return createFolder(path, usePropertyFilter );
			} else {
				throw new RuntimeException(exception);
			}
		}
	}

	private Folder createFolder(String path, boolean usePropertyFilter ) {
		int slashIndex = path.lastIndexOf("/");
		if ( slashIndex >= 0) {
			String parentPath = path.substring(0, slashIndex );
			if ( parentPath.isEmpty() ) {
				parentPath = "/";
			}
			Folder parentFolder = create(parentPath, true );
			Folder subFolder = parentFolder.createSubFolder( path.substring(slashIndex+1) );
			subFolder.save(RefreshMode.REFRESH, usePropertyFilter ? idFilter : null );
			return subFolder;
		} else {
			throw new IllegalArgumentException("Path separator not found in path '" + path + "'" );
		}
	}
}