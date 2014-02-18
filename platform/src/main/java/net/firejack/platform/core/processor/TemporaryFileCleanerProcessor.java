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

package net.firejack.platform.core.processor;

import net.firejack.platform.core.utils.FileUtils;

import java.io.File;
import java.util.Date;

public class TemporaryFileCleanerProcessor {

    private static final long TEMPORARY_FILE_LIVE_TIME = 60 * 60 * 1000;

	 //    @Scheduled(cron="0 */10 * * * ?")   // every 10 minutes
	public void removeTemporaryFiles() {
		clean(false);
    }

	private void clean(boolean onlyFile) {
		long currentTime = new Date().getTime();

		File temporaryFileFolder = FileUtils.getTempDirectory();
		if (temporaryFileFolder.exists()) {
			File[] temporaryFiles = temporaryFileFolder.listFiles();
			for (File temporaryFile : temporaryFiles) {
				long lastModifierTime = temporaryFile.lastModified();
				if ((onlyFile ||  temporaryFile.isFile()) && lastModifierTime + TEMPORARY_FILE_LIVE_TIME < currentTime) {
					FileUtils.deleteQuietly(temporaryFile);
				}
			}
		}
	}
}
