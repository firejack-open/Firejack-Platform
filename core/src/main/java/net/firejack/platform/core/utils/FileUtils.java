/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
