/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */
package net.firejack.platform.core.store.registry;

import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.core.model.registry.system.FileStoreModel;
import net.firejack.platform.core.utils.ArchiveUtils;
import net.firejack.platform.core.utils.FileUtils;
import net.firejack.platform.core.utils.StringUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component("fileStore")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class FileStore extends AliasableStore<FileStoreModel> implements IFileStore {

	private String base;

	/***/
	@PostConstruct
	public void init() {
		setClazz(FileStoreModel.class);
	}

	public void setBase(String base) {
		this.base = base;
	}


	public String createDirectory(String... path) throws IOException {
		FileUtils.checkPath(base, path);
		String construct = FileUtils.construct(path);
		FileUtils.forceMkdir(new File(base, construct));
		return construct;
	}

	@Override
	public void renameDirectory(String name, String... path) throws IOException {
		FileUtils.checkPath(base, path);
		File folder =  FileUtils.create(base, path);
		if (!folder.exists() || !folder.isDirectory()) {
			throw new IOException("Directory doesn't exist.");
		} else {
			File newDirName = new File(folder.getParentFile(), name);
			FileUtils.moveDirectory(folder,newDirName);
		}
	}

	@Override
	public List<File> search(Pattern pattern, String... path) throws IOException {
		List<File> result = new ArrayList<File>();

		FileUtils.checkPath(base, path);
		File folder = FileUtils.create(base, path);
		RegexFileFilter fileFilter = new RegexFileFilter(pattern);
		search(folder, fileFilter, result);
		return result;
	}

	private void search(File folder, FileFilter fileFilter, List<File> result) {
		File[] files = folder.listFiles();
		File[] res = folder.listFiles(fileFilter);
		if (res != null) {
			result.addAll(Arrays.asList(res));
		}
		if (files != null) {
			for (File file : files) {
				search(file, fileFilter, result);
			}
		}
	}

	@Override
	public void upload(InputStream stream, String... path) throws IOException {
		FileUtils.checkPath(base, path);
		File file = FileUtils.create(base, path);
		FileUtils.forceMkdir(file.getParentFile());

		FileOutputStream outputStream = new FileOutputStream(file);
		IOUtils.copy(stream, outputStream);
		IOUtils.closeQuietly(outputStream);

		String name = file.getName();
		String ttl = name.substring(name.indexOf(".") + 1);
		if (StringUtils.isNumeric(ttl) && file.setLastModified(Long.parseLong(ttl))) {
			logger.debug("Change ttl:" + ttl + " for file: " + file.getAbsolutePath());
		}
	}

	@Override
	public InputStream download(String... path) throws IOException {
		FileUtils.checkPath(base, path);
		File file = FileUtils.create(base, path);
		if (!file.isFile()) return null;

		return FileUtils.openInputStream(file);
	}

	@Override
	public void delete(String... path) throws IOException {
		FileUtils.checkPath(base, path);
		File file = FileUtils.create(base, path);

		if (file.exists()) {
			FileUtils.forceDelete(file);
		}
	}

	@Override
	public FileInfo getInfo(String... path) {
		try {
			FileUtils.checkPath(base, path);
		} catch (IOException e) {
			return null;
		}
		File file = FileUtils.create(base, path);
		if(!file.exists()){
			return null;
		}
		return new FileInfo(file.getName(), file.lastModified());
	}

	@Override
	public void unzip(InputStream stream, String... path) throws IOException {
		FileUtils.checkPath(base, path);
		File file = FileUtils.create(base, path);
		ArchiveUtils.unZIP(stream, file);
	}

    @Override
    public List<FileInfo> unzipTemp(InputStream stream, String[] path) throws IOException {
        List<FileInfo> fileInfos = new ArrayList<FileInfo>();
        FileUtils.checkPath(base, path);
		File file = FileUtils.create(base, path);
		List<String> fileNames = ArchiveUtils.unZIP(stream, file, true);
        for (String filename : fileNames) {
            FileInfo fileInfo = new FileInfo(filename);
            fileInfos.add(fileInfo);
        }
        return fileInfos;
    }

    @Override
	public void zip(Map<String, InputStream> streams, String... path) throws IOException {
		FileUtils.checkPath(base, path);
		File file = FileUtils.create(base, path);

		FileOutputStream stream = new FileOutputStream(file);
		ArchiveUtils.zip(stream, streams);
		IOUtils.closeQuietly(stream);
	}

	@Override
	public void updatezip(Map<String, InputStream> streams, String... path) throws IOException {
		FileUtils.checkPath(base, path);
		File file = FileUtils.create(base, path);

		ArchiveUtils.addStreamsToZip(file, streams);
	}
}
