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

package net.firejack.platform.model.helper;

import net.firejack.platform.core.model.registry.resource.AbstractResourceVersionModel;
import net.firejack.platform.core.utils.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileHelper {
	@Value("${temporary.path.folder}")
	private String temp;
	@Value("${package.version.path.folder}")
	private String version;
	@Value("${resource.image.path.folder}")
	private String image;
	@Value("${resource.audio.path.folder}")
	private String audio;
	@Value("${resource.video.path.folder}")
	private String video;
	@Value("${resource.document.path.folder}")
	private String document;
	@Value("${resource.file.path.folder}")
	private String file;
	@Value("${resource.case.path.folder}")
	private String cases;

	public void init(String path) {
		try {
			File file = new File(path, temp);
            if (file.exists()) {
                System.setProperty("java.io.tmpdir", file.getCanonicalPath());
            }
            File tempFolder = new File(System.getProperty("java.io.tmpdir"));
            if (!tempFolder.exists()) {
                FileUtils.forceMkdir(tempFolder);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getAudio() {
		return audio;
	}

	public String getImage() {
		return image;
	}

	public String getTemp() {
		return temp;
	}

	public String getVersion() {
		return version;
	}

	public String getVideo() {
		return video;
	}

    public String getDocument() {
        return document;
    }

    public String getFile() {
        return file;
    }

    public String getCase() {
		return cases;
	}

    public static String getResourceName(AbstractResourceVersionModel model){
        StringBuilder builder = new StringBuilder();
        return builder.append(model.getId()).append("_").append(model.getVersion()).append("_").append(model.getCulture().name()).toString();
    }
}
