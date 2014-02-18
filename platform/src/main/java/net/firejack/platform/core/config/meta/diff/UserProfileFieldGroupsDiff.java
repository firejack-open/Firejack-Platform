package net.firejack.platform.core.config.meta.diff;

import net.firejack.platform.core.config.meta.IPackageDescriptorElement;
import net.firejack.platform.core.config.meta.element.profile.UserProfileFieldGroupElement;

/**
 *
 */
public class UserProfileFieldGroupsDiff extends
        PackageDescriptorElementDiff<IPackageDescriptorElement, UserProfileFieldGroupElement> {

    public UserProfileFieldGroupsDiff(Boolean added, UserProfileFieldGroupElement element) {
        super(added, element);
    }

    public UserProfileFieldGroupsDiff(UserProfileFieldGroupElement oldElement, UserProfileFieldGroupElement newElement) {
        super(oldElement, newElement);
    }

}