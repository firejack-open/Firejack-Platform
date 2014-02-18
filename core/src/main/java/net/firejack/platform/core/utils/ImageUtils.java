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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;


public class ImageUtils {

    /**
     * @param bytes
     * @return
     * @throws java.io.IOException
     */
    public static Integer[] getImageSize(byte[] bytes) throws IOException {
        Integer[] size = new Integer[]{-1, -1};
        ByteArrayInputStream imageInputStream = new ByteArrayInputStream(bytes);
        BufferedImage bufferedImage = ImageIO.read(imageInputStream);
        size[0] = bufferedImage.getWidth();
        size[1] = bufferedImage.getHeight();
        return size;
    }

    /**
     * @param imageFile
     * @return
     * @throws java.io.IOException
     */
    public static Integer[] getImageSize(File imageFile) throws IOException {
        Integer[] size = new Integer[]{-1, -1};
        BufferedImage bufferedImage = ImageIO.read(imageFile);
        size[0] = bufferedImage.getWidth();
        size[1] = bufferedImage.getHeight();
        return size;
    }

}
