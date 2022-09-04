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
	/**
	 * Commons VFS file manager used as long as this object exists
	 */
	private static FileSystemManager fsManager;
	/**
	 * Map linking a gem name to the elements required to unpack it
	 * (link name to versionned name that is used to unpack elements)
	 */
	private Map<String, GemInfo> bundledGems;
	private String gemsPath;
	
	public enum GemElement {
		gems {
			@Override
			FileObject pathOf(GemInfo gemInfo, GemContainer container) throws FileSystemException {
				return container.path(this).resolveFile(gemInfo.versionnedName);
			}
		},
		specifications {

			@Override
			FileObject pathOf(GemInfo gemInfo, GemContainer container) throws FileSystemException {
				return container.path(this).resolveFile(gemInfo.versionnedName+".gemspec");
			}
			
		};

		abstract FileObject pathOf(GemInfo gemInfo, GemContainer container) throws FileSystemException;
	}
	public abstract class GemContainer {

		FileObject path(GemElement gems) throws FileSystemException {
			return path().resolveFile(gems.name());
		}

		abstract FileObject path() throws FileSystemException;
	}
	
	public GemContainer SOURCE = new GemContainer() {

		@Override
		FileObject path() throws FileSystemException {
			return getThisJarAsVFS().resolveFile("META-INF").resolveFile("gems");
		}
		
	};
	public GemContainer TARGET = new GemContainer() {

		@Override
		FileObject path() throws FileSystemException {
			return getGemsFolder();
		}
		
	};
	public class GemInfo {
		public final String versionnedName;

		@Override
		public String toString() {
			return "GemInfo [versionnedName=" + versionnedName + "]";
		}

		public GemInfo(String versionnedName) {
			super();
			this.versionnedName = versionnedName;
		}
	}

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
					copyContainedGem(getBundledGems().get(gemName));
				}
			}
		return requires;
	}


	private void copyContainedGem(GemInfo gemInfo) {
		try {
			for(GemElement element : GemElement.values()) {
				FileObject source = element.pathOf(gemInfo, SOURCE);
				FileObject target = element.pathOf(gemInfo, TARGET);
				if(!target.exists()) {
					log.info(String.format("Copying bundled gem %s into %s", gemInfo.versionnedName, element));
					target.copyFrom(source, new AllFileSelector());
				}
			}
		} catch (FileSystemException e) {
			throw new RuntimeException(String.format("Unable to copy gem %s due to", gemInfo, e));
		}
	}

	private FileObject getGemsFolder() throws FileSystemException {
		FileObject destination = getFileSystemManager().resolveFile("file:"+gemsPath);
		if(!destination.exists()) {
			destination.createFolder();
		}
		return destination;
	}

	/**
	 * Bundled gems should be sub-elements of META-INF/gems
	 * @return
	 */
	private Map<String, GemInfo> loadBundledGems() {
		try {
			Map<String, GemInfo> returned = new TreeMap<>();
			FileObject gemsFolder = SOURCE.path(GemElement.gems);
			for(FileObject gem : gemsFolder.getChildren()) {
				if(gem.isFolder()) {
					String gemVersionnedName = gem.getName().getBaseName();
					String gemName = gemVersionnedName.substring(0, gemVersionnedName.lastIndexOf('-'));
					returned.put(gemName, new GemInfo(gemVersionnedName));
				}
			}
			return returned;
		} catch (FileSystemException e) {
			throw new RuntimeException(e);
		}
	}

	private static FileObject getThisJarAsVFS() {
		URI currentJarUri;
		try {
			currentJarUri = GemExtractor.class
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

	private static FileSystemManager getFileSystemManager() {
		if (fsManager == null) {
			try {
				fsManager = VFS.getManager();
			} catch (FileSystemException e) {
				throw new RuntimeException("Unable to get a Commons-VFS file system manager. THings are really in a bad state");
			}
		}
		return fsManager;
	}
	
	protected Map<String, GemInfo> getBundledGems() {
		if (bundledGems == null) {
			bundledGems = loadBundledGems();
		}
		return bundledGems;
	}

}
