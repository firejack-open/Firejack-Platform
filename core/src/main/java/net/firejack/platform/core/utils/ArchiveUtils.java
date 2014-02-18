/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.core.utils;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class ArchiveUtils {

    private static final int BUFFER = 2048;

    /**
     * @param basePath
     * @param zipPath
     * @param filePaths
     * @throws java.io.IOException
     */
    public static void zip(File basePath, File zipPath, Map<String, String> filePaths) throws IOException {
        BufferedInputStream origin = null;
        ZipOutputStream out = null;
        FileOutputStream dest = null;
        try {
            dest = new FileOutputStream(zipPath);
            out = new ZipOutputStream(new BufferedOutputStream(dest));
            //out.setMethod(ZipOutputStream.DEFLATED);
            byte data[] = new byte[BUFFER];
            for (Map.Entry<String, String> file : filePaths.entrySet()) {
                String filename = file.getKey();
                String filePath = file.getValue();
                System.out.println("Adding: " + basePath.getPath() + filePath + " => " + filename);
                File f = new File(basePath, filePath);
                if (f.exists()) {
                    FileInputStream fi = new FileInputStream(f);
                    origin = new BufferedInputStream(fi, BUFFER);
                    ZipEntry entry = new ZipEntry(filename);
                    entry.setCrc(FileUtils.checksumCRC32(new File(basePath, filePath)));
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER)) != -1) {
                        out.write(data, 0, count);
                    }
                    origin.close();
                }
            }
        } finally {
            if (origin != null) origin.close();
            if (out != null) out.close();
            if (dest != null) dest.close();
        }
    }

	public static void zip(OutputStream outputStream,Map<String, InputStream> filePaths) throws IOException {
		ZipOutputStream out = new ZipOutputStream(outputStream);

		for (Map.Entry<String, InputStream> file : filePaths.entrySet()) {
			String filename = file.getKey();
			InputStream stream = file.getValue();
            if (stream != null) {
                System.out.println("Adding: " + filename);
                ZipEntry entry = new ZipEntry(filename);
                out.putNextEntry(entry);
                IOUtils.copy(stream, out);
                stream.close();
            }
        }

		out.close();
	}

	/**
     * @param basePath
     * @param tarPath
     * @param filePaths
     * @throws java.io.IOException
     */
    public static void tar(String basePath, String tarPath, Map<String, String> filePaths) throws IOException {
        BufferedInputStream origin = null;
        TarArchiveOutputStream out = null;
        FileOutputStream dest = null;
        try {
            dest = new FileOutputStream(tarPath);
            out = new TarArchiveOutputStream(new BufferedOutputStream(dest));
            byte data[] = new byte[BUFFER];
            for (Map.Entry<String, String> entryFile : filePaths.entrySet()) {
                String filename = entryFile.getKey();
                String filePath = entryFile.getValue();
                System.out.println("Adding: " + filename + " => " + filePath);
                FileInputStream fi = new FileInputStream(basePath + filePath);
                origin = new BufferedInputStream(fi, BUFFER);
                TarArchiveEntry entry = new TarArchiveEntry(filename);
                out.putArchiveEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (origin != null) origin.close();
            if (out != null) out.close();
            if (dest != null) dest.close();
        }
    }

    /**
     *
     * @param file
     * @return
     * @throws java.io.IOException
     */
    public static InputStream create(File file) throws IOException {
        if (!file.exists()) return null;

	    ByteArrayOutputStream stream = new ByteArrayOutputStream();
	    ZipOutputStream out = new ZipOutputStream(stream);
        zipEntry(file, out, file.getPath());
        IOUtils.closeQuietly(out);

        return new ByteArrayInputStream(stream.toByteArray());
    }

    /**
     * @param file
     * @param dir
     * @throws java.io.IOException
     */
    public static void unZIP(File file, File dir) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        unZIP(stream, dir);
        stream.close();
    }

    public static List<String> unZIP(InputStream stream, File dir) throws IOException {
        return unZIP(stream, dir, false);
    }

    /**
     * @param stream zip input stream
     * @param dir directory to unzip
     * @param unique if need add unique suffix to every file name then true
     * @throws java.io.IOException
     * @return list of unzipped file names
     */
    public static List<String> unZIP(InputStream stream, File dir, boolean unique) throws IOException {
        Long createdTime = new Date().getTime();
        String randomName = SecurityHelper.generateRandomSequence(16);
        List<String> fileNames = new ArrayList<String>();
        ZipInputStream in = new ZipInputStream(stream);
        ZipEntry entry;
        while ((entry = in.getNextEntry()) != null) {
            String name = entry.getName();
            if (entry.isDirectory()) {
                FileUtils.forceMkdir(new File(dir, name));
            } else {
                String dirName = "";
                if (name.contains("/") && !File.separator.equals("/"))
                    name = name.replaceAll("/", File.separator + File.separator);
                if (name.contains("\\") && !File.separator.equals("\\")) name = name.replaceAll("\\\\", File.separator);

                if (name.lastIndexOf(File.separator) != -1)
                    dirName = name.substring(0, name.lastIndexOf(File.separator));
                String fileName = name.substring(name.lastIndexOf(File.separator) + 1, name.length());
                if (unique) {
                    fileName = fileName + "." + randomName + "." + createdTime;
                }
                OutputStream out = FileUtils.openOutputStream(FileUtils.create(dir, dirName, fileName));
                IOUtils.copy(in, out);
                IOUtils.closeQuietly(out);
                fileNames.add(fileName);
            }
        }
        IOUtils.closeQuietly(in);
        return fileNames;
    }

    /**
     * @param zip
     * @param name
     * @return
     * @throws java.io.IOException
     */
    public static InputStream getFile(File zip, String name) throws IOException {
        return getFile(new FileInputStream(zip), name);
    }

    /**
     * @param zip
     * @param name
     * @return
     * @throws java.io.IOException
     */
    public static InputStream getFile(InputStream zip, String name) throws IOException {
        ZipInputStream in = new ZipInputStream(zip);
        ZipEntry entry;
        while ((entry = in.getNextEntry()) != null) {
            String entityName = entry.getName();
            if (!entry.isDirectory() && entityName.contains(name)) {
                return in;
            }
        }
        in.close();
        return null;
    }

    private static void zipEntry(File file, ZipOutputStream out, String delPath) throws IOException {
        if (file.isFile()) {
            FileInputStream in = new FileInputStream(file);
            out.putNextEntry(new ZipEntry(file.getPath().replace(delPath + File.separator, "")));
            IOUtils.copy(in, out);

            out.closeEntry();
            IOUtils.closeQuietly(in);
        } else {
            for (File f : file.listFiles())
                zipEntry(f, out, delPath);
        }
    }

    /**
     * @param zipFile
     * @param files
     * @throws java.io.IOException
     */
    public static void addFilesToZip(File zipFile, Map<String, File> files) throws IOException {
        // get a temp file
        File tempFile = File.createTempFile(zipFile.getName(), null);
        // delete it, otherwise you cannot rename your existing zip to it.
	    FileUtils.deleteQuietly(tempFile);

        boolean renameOk = zipFile.renameTo(tempFile);
        if (!renameOk) {
            throw new RuntimeException("could not rename the file " + zipFile.getAbsolutePath() + " to " + tempFile.getAbsolutePath());
        }

        ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));

        ZipEntry entry = zin.getNextEntry();
        while (entry != null) {
            String name = entry.getName();
            boolean notInFiles = true;
            for (Map.Entry<String, File> e : files.entrySet()) {
                if (e.getKey().equals(name)) {
                    notInFiles = false;
                    break;
                }
            }
            if (notInFiles) {
                out.putNextEntry(new ZipEntry(name));
	            IOUtils.copy(zin, out);
            }
            entry = zin.getNextEntry();
        }
        // Close the streams
        zin.close();
        // Compress the files
        for (Map.Entry<String, File> e : files.entrySet()) {
            InputStream in = new FileInputStream(e.getValue());
            // Add ZIP entry to output stream.
            out.putNextEntry(new ZipEntry(e.getKey()));
            // Transfer bytes from the file to the ZIP file

	        IOUtils.copy(in, out);
            // Complete the entry
            out.closeEntry();
	        IOUtils.closeQuietly(in);
        }
        // Complete the ZIP file
	    IOUtils.closeQuietly(out);
	    FileUtils.deleteQuietly(tempFile);
    }
    /**
     * @param zipFile
     * @param files
     * @throws java.io.IOException
     */
    public static void addStreamsToZip(File zipFile, Map<String, InputStream> files) throws IOException {
        // get a temp file
        File tempFile = File.createTempFile(zipFile.getName(), null);
        // delete it, otherwise you cannot rename your existing zip to it.
	    FileUtils.deleteQuietly(tempFile);

        boolean renameOk = zipFile.renameTo(tempFile);
        if (!renameOk) {
            throw new RuntimeException("could not rename the file " + zipFile.getAbsolutePath() + " to " + tempFile.getAbsolutePath());
        }

        ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));

        ZipEntry entry = zin.getNextEntry();
        while (entry != null) {
            String name = entry.getName();
            boolean notInFiles = true;
            for (Map.Entry<String, InputStream> e : files.entrySet()) {
                if (e.getKey().equals(name)) {
                    notInFiles = false;
                    break;
                }
            }
            if (notInFiles) {
                out.putNextEntry(new ZipEntry(name));
	            IOUtils.copy(zin, out);
            }
            entry = zin.getNextEntry();
        }
        // Close the streams
        zin.close();
        // Compress the files
        for (Map.Entry<String, InputStream> e : files.entrySet()) {
            InputStream in = e.getValue();
            // Add ZIP entry to output stream.
            out.putNextEntry(new ZipEntry(e.getKey()));
            // Transfer bytes from the file to the ZIP file

	        IOUtils.copy(in, out);
            // Complete the entry
            out.closeEntry();
	        IOUtils.closeQuietly(in);
        }
        // Complete the ZIP file
	    IOUtils.closeQuietly(out);
	    FileUtils.deleteQuietly(tempFile);
    }

	public static void unzip(File zip, ArchiveCallback callback, String... matches) {
		try {
			FileInputStream stream = FileUtils.openInputStream(zip);
			unzip(stream, callback, matches);
			IOUtils.closeQuietly(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void unzip(InputStream stream, ArchiveCallback callback, String... matches) {
		ZipInputStream in = new ZipInputStream(stream);
		ZipEntry entry;
		try {
			Map<String, File> skipped = new HashMap<String, File>();
			matches = (String[]) ArrayUtils.add(matches, ".*");
			while ((entry = in.getNextEntry()) != null) {
				String name = entry.getName();
				if (!entry.isDirectory()) {
					if (name.contains("/") && !File.separator.equals("/")) name = name.replaceAll("/", File.separator + File.separator);
					if (name.contains("\\") && !File.separator.equals("\\")) name = name.replaceAll("\\\\", File.separator);

					String dir = name.replaceAll("^(.*?)(?:\\\\|/?)([~@\\$\\{\\}\\w\\-\\.\\+]+$)", "$1");
					String file = name.replaceAll("^(.*?)(?:\\\\|/?)([~@\\$\\{\\}\\w\\-\\.\\+]+$)", "$2");

					if (file.matches(matches[0])) {
						callback.callback(dir, file, in);
					} else {
						File path = FileUtils.getTempFile(SecurityHelper.generateRandomSequence(16));
						OutputStream out = FileUtils.openOutputStream(path);
						IOUtils.copy(in, out);
						IOUtils.closeQuietly(out);
						skipped.put(name, path);
					}
				}
			}

			for (int i = 1; i < matches.length; i++) {
				for (Iterator<Map.Entry<String, File>> iterator = skipped.entrySet().iterator(); iterator.hasNext(); ) {
					Map.Entry<String, File> next = iterator.next();
					String name = next.getKey();
					File path = next.getValue();

					String dir = name.replaceAll("(.*?)(?:\\\\|/?)([~@\\$\\{\\}\\w\\-\\.\\+]+$)", "$1");
					String file = name.replaceAll("(.*?)(?:\\\\|/?)([~@\\$\\{\\}\\w\\-\\.\\+]+$)", "$2");

					if (file.matches(matches[i])) {
						iterator.remove();
						FileInputStream inputStream = FileUtils.openInputStream(path);
						callback.callback(dir, file, inputStream);
						IOUtils.closeQuietly(inputStream);
						FileUtils.forceDelete(path);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void editzip(File zip, File dist, EditArchiveCallback callback, String... matches) {
		try {
			FileInputStream stream = FileUtils.openInputStream(zip);
			FileOutputStream outputStream = FileUtils.openOutputStream(dist);
			editzip(stream, outputStream, true, callback, matches);
			IOUtils.closeQuietly(stream);
			IOUtils.closeQuietly(outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void editzip(InputStream stream, OutputStream outputStream, boolean close, EditArchiveCallback callback, String... matches) {
		ZipInputStream in = new ZipInputStream(stream);
		ZipOutputStream zout = new ZipOutputStream(outputStream);
		ZipEntry entry;
		try {
			Map<String, File> skipped = new HashMap<String, File>();
			matches = (String[]) ArrayUtils.add(matches, ".*");
			while ((entry = in.getNextEntry()) != null) {
				String name = entry.getName();
				if (!entry.isDirectory()) {
					if (name.contains("/") && !File.separator.equals("/")) name = name.replaceAll("/", File.separator + File.separator);
					if (name.contains("\\") && !File.separator.equals("\\")) name = name.replaceAll("\\\\", File.separator);

					String dir = name.replaceAll("^(.*?)(?:\\\\|/?)([~@\\$\\{\\}\\w\\-\\.\\+]+$)", "$1");
					String file = name.replaceAll("^(.*?)(?:\\\\|/?)([~@\\$\\{\\}\\w\\-\\.\\+]+$)", "$2");

					if (file.matches(matches[0])) {
						zout.putNextEntry(new ZipEntry(name));
						callback.edit(dir, file, in, zout);
						zout.closeEntry();
					} else {
						File path = FileUtils.getTempFile(SecurityHelper.generateRandomSequence(16));
						OutputStream out = FileUtils.openOutputStream(path);
						IOUtils.copy(in, out);
						IOUtils.closeQuietly(out);
						skipped.put(name, path);
					}
				}
			}

			for (int i = 1; i < matches.length; i++) {
				for (Iterator<Map.Entry<String, File>> iterator = skipped.entrySet().iterator(); iterator.hasNext(); ) {
					Map.Entry<String, File> next = iterator.next();
					String name = next.getKey();
					File path = next.getValue();

					String dir = name.replaceAll("(.*?)(?:\\\\|/?)([~@\\$\\{\\}\\w\\-\\.\\+]+$)", "$1");
					String file = name.replaceAll("(.*?)(?:\\\\|/?)([~@\\$\\{\\}\\w\\-\\.\\+]+$)", "$2");

					if (file.matches(matches[i])) {
						iterator.remove();
						FileInputStream inputStream = FileUtils.openInputStream(path);
						zout.putNextEntry(entry);
						callback.edit(dir, file, inputStream, zout);
						zout.closeEntry();
						IOUtils.closeQuietly(inputStream);
						FileUtils.forceDelete(path);
					}
				}
			}

			while (callback.add(zout)) {
				zout.closeEntry();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (close) IOUtils.closeQuietly(zout);
		}
	}

	public interface ArchiveCallback {
		void callback(String dir, String name, InputStream stream);
	}

	public interface EditArchiveCallback {
		void edit(String dir, String name, InputStream stream, OutputStream outputStream) throws IOException;
		boolean add(ZipOutputStream outputStream) throws IOException;
	}
}
