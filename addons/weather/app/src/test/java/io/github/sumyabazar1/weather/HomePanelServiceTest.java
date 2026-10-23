package io.github.sumyabazar1.weather;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HomePanelServiceTest {
    @Test
    public void rejectsMissingOrUnrelatedPackages() {
        assertFalse(HomePanelService.isLauncherPackage(null));
        assertFalse(HomePanelService.isLauncherPackage(new String[0]));
        assertFalse(HomePanelService.isLauncherPackage(new String[]{"com.example.other"}));
    }

    @Test
    public void acceptsTheLauncherAmongPackagesSharingTheUid() {
        assertTrue(HomePanelService.isLauncherPackage(new String[]{
                "com.example.other", "com.gathin.tvhome"
        }));
    }

    @Test
    public void rejectsPackageNamesThatOnlyContainTheLauncherName() {
        assertFalse(HomePanelService.isLauncherPackage(new String[]{
                "com.gathin.tvhome.fake"
        }));
    }
}
