package com.gathin.tvhome;

/** Positioning rules for the passive Home panel and the dock's cinematic title. */
final class HomePanelLayout {
    private HomePanelLayout() { }

    /**
     * Returns the title's top edge, or NaN when the panel and title cannot fit between the header and dock.
     * All distances are pixels; density converts the layout's dp gaps.
     */
    static float heroTop(float originalTop, float panelBottom, float dockTop, float heroHeight, float density) {
        float shelfBottom = dockTop - 14f * density;
        float belowPanel = panelBottom + 10f * density;
        float beforeDock = shelfBottom - heroHeight - 6f * density;
        if (belowPanel > beforeDock) return Float.NaN;
        return Math.max(originalTop, belowPanel);
    }
}
