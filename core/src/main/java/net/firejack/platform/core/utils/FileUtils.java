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

package net.firejack.platform.core.utils;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.apache.commons.io.IOUtils;
import org.springframework.util.DigestUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Date;

public class FileUtils extends org.apache.commons.io.FileUtils {
	public static final String CDATA_DESCRIPTION = "^description";

	/** @return  */
	public static String tempFileName() {
		Long createFileTime = new Date().getTime();
		String randomName = SecurityHelper.generateRandomSequence(16);
		return randomName + "." + createFileTime;
	}

	/** @return  */
	public static String tempFolderName() {
		return SecurityHelper.generateRandomSequence(16);
	}

	/**
	 * @param parent
	 * @param children
	 *
	 * @return
	 */
	public static File create(File parent, String... children) {
		return create(parent.getPath(), children);
	}

	/**
	 * @param parent
	 * @param children
	 *
	 * @return
	 */
	public static File create(String parent, String... children) {
		if (StringUtils.isNotBlank(parent)) {
			return new File(parent, StringUtils.join(children, File.separator));
		} else {
			return new File(StringUtils.join(children, File.separator));
		}
	}

	public static String construct(String... children) {
		return StringUtils.join(children, File.separator);
	}

	/**
	 * @param name
	 *
	 * @return
	 *
	 * @throws java.io.IOException
	 */
	public static File getTempFile(String name) throws IOException {
		return new File(getTempDirectory(), name);
	}

	/**
	 * @param obj
	 *
	 * @return
	 *
	 * @throws java.io.IOException
	 * @throws javax.xml.bind.JAXBException
	 */
	public static <T> OutputStream writeJAXB(T obj) throws IOException, JAXBException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		writeJAXB(obj, stream);
		return stream;
	}

	public static <T> void writeJAXB(T obj, OutputStream stream, Class... classes) throws IOException, JAXBException {

		OutputFormat outputFormat = new OutputFormat();
		outputFormat.setCDataElements(new String[]{CDATA_DESCRIPTION});
		outputFormat.setIndenting(true);

		XMLSerializer serializer = new XMLSerializer(outputFormat);
		serializer.setOutputByteStream(stream);

		if (classes.length == 0) {
			classes = (Class[]) ArrayUtils.add(classes, obj.getClass());
		}

		JAXBContext jaxbContext = JAXBContext.newInstance(classes);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(obj, serializer.asContentHandler());
	}

	/**
	 *
	 *
	 *
	 * @param clazz
	 * @param is
	 *
	 * @return
	 *
	 * @throws javax.xml.bind.JAXBException
	 */
	public static <T> T readJAXB(Class<T> clazz, InputStream is) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(clazz);
		Unmarshaller u = jc.createUnmarshaller();

		return (T) u.unmarshal(is);
	}

	public static <T> T readJAXB(InputStream is, Class... clazz) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(clazz);
		Unmarshaller u = jc.createUnmarshaller();

		return (T) u.unmarshal(is);
	}

	/**
	 * @param clazz
	 * @param url
	 *
	 * @return
	 *
	 * @throws javax.xml.bind.JAXBException
	 */
	public static <T> T readJAXB(Class<T> clazz, URL url) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(clazz);
		Unmarshaller u = jc.createUnmarshaller();

		return (T) u.unmarshal(url);
	}

	/**
	 * @param clazz
	 * @param file
	 *
	 * @return
	 *
	 * @throws javax.xml.bind.JAXBException
	 */
	public static <T> T readJAXB(Class<T> clazz, File file) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(clazz);
		Unmarshaller u = jc.createUnmarshaller();

		return (T) u.unmarshal(file);
	}

	/**
	 * @param child
	 *
	 * @return
	 */
	public static File getResource(String... child) {
		URL url = Thread.currentThread().getContextClassLoader().getResource("");
		String file = url.getFile();
		try {
			file = URLDecoder.decode(file, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return FileUtils.create(file, child);
	}

	public static File getWebappFolder() {
		return getResource("..", "..");
	}

	public static File writeToFile(String filename, InputStream stream) {
		File file = new File(filename);
		writeToFile(file, stream);
		return file;
	}

	public static void writeToFile(File file, InputStream stream) {
		try {
			FileOutputStream outputStream = openOutputStream(file);
			IOUtils.copy(stream, outputStream);
			IOUtils.closeQuietly(outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String md5(File file) {
		if (!file.exists()) return null;
		String md5 = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			md5 = DigestUtils.md5DigestAsHex(IOUtils.toByteArray(fis));
			IOUtils.closeQuietly(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return md5;
	}

	public static void checkPath(String parent, String... path) throws IOException {
		if (!create(parent, path).getCanonicalPath().toLowerCase().startsWith(parent.toLowerCase())) {
			throw new IOException("Bad path");
		}
	}
}
