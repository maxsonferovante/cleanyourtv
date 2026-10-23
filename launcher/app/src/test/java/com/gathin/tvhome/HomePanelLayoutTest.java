package com.gathin.tvhome;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HomePanelLayoutTest {
    @Test
    public void titleMovesBelowPanelWhenThereIsRoom() {
        assertEquals(210f, HomePanelLayout.heroTop(150f, 200f, 500f, 100f, 1f), 0.01f);
    }

    @Test
    public void titleKeepsItsOriginalPositionWhenAlreadyBelowPanel() {
        assertEquals(250f, HomePanelLayout.heroTop(250f, 200f, 500f, 100f, 1f), 0.01f);
    }

    @Test
    public void titleIsHiddenWhenPanelAndTitleCannotFitBeforeDock() {
        assertTrue(Float.isNaN(HomePanelLayout.heroTop(150f, 330f, 450f, 100f, 1f)));
    }

    @Test
    public void exactFitKeepsTitleClearOfBothPanelAndDock() {
        assertEquals(340f, HomePanelLayout.heroTop(150f, 330f, 460f, 100f, 1f), 0.01f);
    }
}
