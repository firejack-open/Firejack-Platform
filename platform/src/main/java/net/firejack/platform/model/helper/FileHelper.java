package net.firejack.platform.model.helper;

import net.firejack.platform.core.model.registry.resource.AbstractResourceVersionModel;
import net.firejack.platform.core.utils.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

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
