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

package net.firejack.platform.core.config.meta.element.resource;

import net.firejack.platform.api.content.model.ResourceType;
import net.firejack.platform.core.config.meta.element.PackageDescriptorElement;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.model.registry.resource.ResourceModel;
import net.firejack.platform.core.utils.ArrayUtils;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;


public class ResourceElement extends PackageDescriptorElement<ResourceModel> {

    private Integer lastVersion;
    private ResourceType resourceType;
    private List<TextResourceVersionElement> textResourceVersionElements;
    private List<HtmlResourceVersionElement> htmlResourceVersionElements;
    private List<ImageResourceVersionElement> imageResourceVersionElements;
    private List<AudioResourceVersionElement> audioResourceVersionElements;
    private List<VideoResourceVersionElement> videoResourceVersionElements;
    private List<DocumentResourceVersionElement> documentResourceVersionElements;
    private List<FileResourceVersionElement> fileResourceVersionElements;

    /**
     * @return
     */
    @XmlAttribute(name = "last-version")
    public Integer getLastVersion() {
        return lastVersion;
    }

    /**
     * @param lastVersion
     */
    public void setLastVersion(Integer lastVersion) {
        this.lastVersion = lastVersion;
    }

    /**
     * @return
     */
    @XmlAttribute(name = "type")
    public ResourceType getResourceType() {
        return resourceType;
    }

    /**
     * @param resourceType
     */
    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * @return
     */
    @XmlTransient
    public TextResourceVersionElement[] getTextResourceVersions() {
        return DiffUtils.getArray(textResourceVersionElements, TextResourceVersionElement.class);
    }

    /**
     * @param textResourceVersionElements
     */
    public void setTextResourceVersions(List<TextResourceVersionElement> textResourceVersionElements) {
        this.textResourceVersionElements = textResourceVersionElements;
    }

    @XmlElement(name = "text-resource-version")
    public List<TextResourceVersionElement> getTextResourceVersionElements() {
        return textResourceVersionElements;
    }

    public void setTextResourceVersionElements(List<TextResourceVersionElement> textResourceVersionElements) {
        this.textResourceVersionElements = textResourceVersionElements;
    }

    /**
     * @param textResourceVersionElement
     */
    public void addTextResourceVersion(TextResourceVersionElement textResourceVersionElement) {
        if (this.textResourceVersionElements == null) {
            this.textResourceVersionElements = new ArrayList<TextResourceVersionElement>();
        }
        this.textResourceVersionElements.add(textResourceVersionElement);
    }

    /**
     * @return
     */
    @XmlTransient
    public HtmlResourceVersionElement[] getHtmlResourceVersions() {
        return DiffUtils.getArray(htmlResourceVersionElements, HtmlResourceVersionElement.class);
    }

    /**
     * @param htmlResourceVersionElements
     */
    public void setHtmlResourceVersions(List<HtmlResourceVersionElement> htmlResourceVersionElements) {
        this.htmlResourceVersionElements = htmlResourceVersionElements;
    }

    @XmlElement(name = "html-resource-version")
    public List<HtmlResourceVersionElement> getHtmlResourceVersionElements() {
        return htmlResourceVersionElements;
    }

    public void setHtmlResourceVersionElements(List<HtmlResourceVersionElement> htmlResourceVersionElements) {
        this.htmlResourceVersionElements = htmlResourceVersionElements;
    }

    /**
     * @param htmlResourceVersionElement
     */
    public void addHtmlResourceVersion(HtmlResourceVersionElement htmlResourceVersionElement) {
        if (this.htmlResourceVersionElements == null) {
            this.htmlResourceVersionElements = new ArrayList<HtmlResourceVersionElement>();
        }
        this.htmlResourceVersionElements.add(htmlResourceVersionElement);
    }

    /**
     * @return
     */
    @XmlTransient
    public ImageResourceVersionElement[] getImageResourceVersions() {
        return DiffUtils.getArray(imageResourceVersionElements, ImageResourceVersionElement.class);
    }

    /**
     * @param imageResourceVersionElements
     */
    public void setImageResourceVersions(List<ImageResourceVersionElement> imageResourceVersionElements) {
        this.imageResourceVersionElements = imageResourceVersionElements;
    }

    @XmlElement(name = "image-resource-version")
    public List<ImageResourceVersionElement> getImageResourceVersionElements() {
        return imageResourceVersionElements;
    }

    public void setImageResourceVersionElements(List<ImageResourceVersionElement> imageResourceVersionElements) {
        this.imageResourceVersionElements = imageResourceVersionElements;
    }

    /**
     * @param imageResourceVersionElement
     */
    public void addImageResourceVersion(ImageResourceVersionElement imageResourceVersionElement) {
        if (this.imageResourceVersionElements == null) {
            this.imageResourceVersionElements = new ArrayList<ImageResourceVersionElement>();
        }
        this.imageResourceVersionElements.add(imageResourceVersionElement);
    }

    /**
     * @return
     */
    @XmlTransient
    public AudioResourceVersionElement[] getAudioResourceVersions() {
        return DiffUtils.getArray(audioResourceVersionElements, AudioResourceVersionElement.class);
    }

    /**
     * @param audioResourceVersionElements
     */
    public void setAudioResourceVersions(List<AudioResourceVersionElement> audioResourceVersionElements) {
        this.audioResourceVersionElements = audioResourceVersionElements;
    }

    @XmlElement(name = "audio-resource-version")
    public List<AudioResourceVersionElement> getAudioResourceVersionElements() {
        return audioResourceVersionElements;
    }

    public void setAudioResourceVersionElements(List<AudioResourceVersionElement> audioResourceVersionElements) {
        this.audioResourceVersionElements = audioResourceVersionElements;
    }

    /**
     * @param audioResourceVersionElement
     */
    public void addAudioResourceVersion(AudioResourceVersionElement audioResourceVersionElement) {
        if (this.audioResourceVersionElements == null) {
            this.audioResourceVersionElements = new ArrayList<AudioResourceVersionElement>();
        }
        this.audioResourceVersionElements.add(audioResourceVersionElement);
    }

    /**
     * @return
     */
    @XmlTransient
    public VideoResourceVersionElement[] getVideoResourceVersions() {
        return DiffUtils.getArray(videoResourceVersionElements, VideoResourceVersionElement.class);
    }

    /**
     * @param videoResourceVersionElements
     */
    public void setVideoResourceVersions(List<VideoResourceVersionElement> videoResourceVersionElements) {
        this.videoResourceVersionElements = videoResourceVersionElements;
    }

    @XmlElement(name = "video-resource-version")
    public List<VideoResourceVersionElement> getVideoResourceVersionElements() {
        return videoResourceVersionElements;
    }

    public void setVideoResourceVersionElements(List<VideoResourceVersionElement> videoResourceVersionElements) {
        this.videoResourceVersionElements = videoResourceVersionElements;
    }

    /**
     * @param videoResourceVersionElement
     */
    public void addVideoResourceVersion(VideoResourceVersionElement videoResourceVersionElement) {
        if (this.videoResourceVersionElements == null) {
            this.videoResourceVersionElements = new ArrayList<VideoResourceVersionElement>();
        }
        this.videoResourceVersionElements.add(videoResourceVersionElement);
    }

    /**
     * @return
     */
    @XmlTransient
    public DocumentResourceVersionElement[] getDocumentResourceVersions() {
        return DiffUtils.getArray(documentResourceVersionElements, DocumentResourceVersionElement.class);
    }

    /**
     * @param documentResourceVersionElements
     */
    public void setDocumentResourceVersions(List<DocumentResourceVersionElement> documentResourceVersionElements) {
        this.documentResourceVersionElements = documentResourceVersionElements;
    }

    @XmlElement(name = "document-resource-version")
    public List<DocumentResourceVersionElement> getDocumentResourceVersionElements() {
        return documentResourceVersionElements;
    }

    public void setDocumentResourceVersionElements(List<DocumentResourceVersionElement> documentResourceVersionElements) {
        this.documentResourceVersionElements = documentResourceVersionElements;
    }

    /**
     * @param documentResourceVersionElement
     */
    public void addDocumentResourceVersion(DocumentResourceVersionElement documentResourceVersionElement) {
        if (this.documentResourceVersionElements == null) {
            this.documentResourceVersionElements = new ArrayList<DocumentResourceVersionElement>();
        }
        this.documentResourceVersionElements.add(documentResourceVersionElement);
    }

    /**
     * @return
     */
    @XmlTransient
    public FileResourceVersionElement[] getFileResourceVersions() {
        return DiffUtils.getArray(fileResourceVersionElements, FileResourceVersionElement.class);
    }

    /**
     * @param fileResourceVersionElements
     */
    public void setFileResourceVersions(List<FileResourceVersionElement> fileResourceVersionElements) {
        this.fileResourceVersionElements = fileResourceVersionElements;
    }

    @XmlElement(name = "file-resource-version")
    public List<FileResourceVersionElement> getFileResourceVersionElements() {
        return fileResourceVersionElements;
    }

    public void setFileResourceVersionElements(List<FileResourceVersionElement> fileResourceVersionElements) {
        this.fileResourceVersionElements = fileResourceVersionElements;
    }

    /**
     * @param fileResourceVersionElement
     */
    public void addFileResourceVersion(FileResourceVersionElement fileResourceVersionElement) {
        if (this.fileResourceVersionElements == null) {
            this.fileResourceVersionElements = new ArrayList<FileResourceVersionElement>();
        }
        this.fileResourceVersionElements.add(fileResourceVersionElement);
    }

    @Override
    public Class<ResourceModel> getEntityClass() {
        return ResourceModel.class;
    }

    @Override
    public boolean equals(Object o) {
        boolean equals = super.equals(o);
        if (equals) {
            ResourceElement that = (ResourceElement) o;
            if (this.getResourceType() != that.getResourceType()) {
                equals = false;
            } else {
                ResourceVersionElement[] thisElements;
                ResourceVersionElement[] thatElements;
                switch (this.getResourceType()) {
                    case TEXT:
                        thisElements = this.getTextResourceVersions();
                        thatElements = that.getTextResourceVersions();
                        break;
                    case HTML:
                        thisElements = this.getHtmlResourceVersions();
                        thatElements = that.getHtmlResourceVersions();
                        break;
                    case IMAGE:
                        thisElements = this.getImageResourceVersions();
                        thatElements = that.getImageResourceVersions();
                        break;
                    case AUDIO:
                        thisElements = this.getAudioResourceVersions();
                        thatElements = that.getAudioResourceVersions();
                        break;
                    case VIDEO:
                        thisElements = this.getVideoResourceVersions();
                        thatElements = that.getVideoResourceVersions();
                        break;
                    case DOCUMENT:
                        thisElements = this.getDocumentResourceVersions();
                        thatElements = that.getDocumentResourceVersions();
                        break;
                    case FILE:
                        thisElements = this.getFileResourceVersions();
                        thatElements = that.getFileResourceVersions();
                        break;
                    default:
                        throw new UnsupportedOperationException(
                                "Diff calculation does not support resources of type = " +
                                        this.getResourceType().name());
                }
                equals = (thisElements == null && thatElements == null) ||
                        (thisElements != null && thatElements != null &&
                                ArrayUtils.containsAll(thisElements, thatElements) &&
                                ArrayUtils.containsAll(thatElements, thisElements));
            }
        }
        return equals;
    }

}