package org.ndx.aadarchi.base.utils.commonsvfs;

import java.util.function.Consumer;

import org.apache.commons.vfs2.FileSystemOptions;

@FunctionalInterface
public interface FileSystemOptionsConfigurer extends Consumer<FileSystemOptions>{

}
