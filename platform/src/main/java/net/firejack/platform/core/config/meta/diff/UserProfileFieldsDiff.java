package net.firejack.platform.core.config.meta.diff;

import net.firejack.platform.core.config.meta.IPackageDescriptorElement;
import net.firejack.platform.core.config.meta.element.profile.UserProfileFieldElement;

/**
 *
 */
public class UserProfileFieldsDiff extends PackageDescriptorElementDiff<IPackageDescriptorElement, UserProfileFieldElement>{

    public UserProfileFieldsDiff(Boolean added, UserProfileFieldElement element) {
        super(added, element);
    }

    public UserProfileFieldsDiff(UserProfileFieldElement oldElement, UserProfileFieldElement newElement) {
        super(oldElement, newElement);
    }
}