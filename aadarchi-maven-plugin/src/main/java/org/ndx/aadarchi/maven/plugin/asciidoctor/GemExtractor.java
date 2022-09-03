package org.ndx.aadarchi.maven.plugin.asciidoctor;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.vfs2.AllFileSelector;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.twdata.maven.mojoexecutor.MojoExecutor.Element;

public class GemExtractor {

	private Log log;
	private FileSystemManager fsManager;
	private Map<String, FileObject> bundledGems;
	private String gemsPath;

	public GemExtractor(String gemsPath, Log log) {
		this.gemsPath = gemsPath;
		this.log = log;
	}

	/**
	 * Scan the list of contained gems looking for the ones which are bundled with
	 * this plugin. Each bundled element that is not present in effective folder
	 * will be copied
	 * 
	 * @param requiredGems
	 * @see #requiredGems()
	 */
	public Element processContainedGems(Element requires) {
			for (Xpp3Dom dependency : requires.toDom().getChildren("require")) {
				String gemName = dependency.getValue();
				if (getBundledGems().containsKey(gemName)) {
					copyContainedGem(gemName, getBundledGems().get(gemName));
				}
			}
		return requires;
	}


	private void copyContainedGem(String value, FileObject sourceGem) {
		try {
			FileObject gems = getGemsFolder();
			FileObject target = gems.resolveFile(sourceGem.getName().getBaseName());
			target.copyFrom(sourceGem, new AllFileSelector());
		} catch (FileSystemException e) {
			throw new RuntimeException(String.format("Unable to copy gem %s due to", value, e));
		}
	}

	private FileObject getGemsFolder() throws FileSystemException {
		FileObject destination = getFileSystemManager().resolveFile("file:"+gemsPath);
		if(!destination.exists()) {
			destination.createFolder();
		}
		FileObject gems = destination.resolveFile("gems");
		if(!gems.exists()) {
			gems.createFolder();
		}
		return gems;
	}

	/**
	 * Bundled gems should be sub-elements of META-INF/gems
	 * @return
	 */
	private Map<String, FileObject> loadBundledGems() {
		try {
			Map<String, FileObject> returned = new TreeMap<>();
			FileObject gemsFolder = getThisJarAsVFS().getChild("META-INF").getChild("gems").getChild("gems");
			for(FileObject gem : gemsFolder.getChildren()) {
				if(gem.isFolder()) {
					String gemVersionnedName = gem.getName().getBaseName();
					String gemName = gemVersionnedName.substring(0, gemVersionnedName.lastIndexOf('-'));
					returned.put(gemName, gem);
				}
			}
			return returned;
		} catch (FileSystemException e) {
			throw new RuntimeException(e);
		}
	}

	private FileObject getThisJarAsVFS() {
		URI currentJarUri;
		try {
			currentJarUri = getClass()
			    .getProtectionDomain()
			    .getCodeSource()
			    .getLocation()
			    .toURI();
		} catch (URISyntaxException e) {
			throw new RuntimeException("Interestingly, we can't resolve the path of the current jar...");
		}
		String currentJarPath = currentJarUri.getPath();
		String commonsVfsPath = "jar:"+currentJarPath+"!";
		try {
			return getFileSystemManager().resolveFile(commonsVfsPath);
		} catch (FileSystemException e) {
			throw new RuntimeException("Unable to resolve path to current jar (uri was "+commonsVfsPath+")", e);
		}
	}

	private FileSystemManager getFileSystemManager() {
		if (fsManager == null) {
			try {
				fsManager = VFS.getManager();
			} catch (FileSystemException e) {
				throw new RuntimeException("Unable to get a Commons-VFS file system manager. THings are really in a bad state");
			}
		}
		return fsManager;
	}
	
	protected Map<String, FileObject> getBundledGems() {
		if (bundledGems == null) {
			bundledGems = loadBundledGems();
		}
		return bundledGems;
	}

}
