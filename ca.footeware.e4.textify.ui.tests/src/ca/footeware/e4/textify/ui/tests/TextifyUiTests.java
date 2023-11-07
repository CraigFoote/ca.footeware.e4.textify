/**
 * 
 */
package ca.footeware.e4.textify.ui.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotStyledText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * 
 */
class TextifyUiTests {

	private static SWTBot bot;

	@BeforeAll
	static void setUpBeforeClass() {
		bot = new SWTBot();
		SWTBotShell shell = bot.shell("Error");
		if (shell != null) {
			shell.bot().button("OK").click();
		}
	}

	@Test
	void testNewToolbarItem() {
		SWTBotToolbarButton toolbarButton = bot.toolbarButtonWithTooltip("Create a new document");
		assertNotNull(toolbarButton);
		assertEquals("", toolbarButton.getText());
		assertTrue(toolbarButton.isVisible());
		assertTrue(toolbarButton.isEnabled());
	}

	@Test
	void testOpenToolbarButton() {
		SWTBotToolbarButton toolbarButton = bot.toolbarButtonWithTooltip("Open an existing document");
		assertNotNull(toolbarButton);
		assertEquals("", toolbarButton.getText());
		assertTrue(toolbarButton.isVisible());
		assertTrue(toolbarButton.isEnabled());
	}

	@Test
	void testSaveToolbarItem() {
		SWTBotToolbarButton toolbarButton = bot.toolbarButtonWithTooltip("Save current document");
		assertNotNull(toolbarButton);
		assertEquals("", toolbarButton.getText());
		assertTrue(toolbarButton.isVisible());
		assertTrue(toolbarButton.isEnabled());
	}

	@Test
	void testSaveAsToolbarItem() {
		SWTBotToolbarButton toolbarButton = bot.toolbarButtonWithTooltip("Save current document under new name");
		assertNotNull(toolbarButton);
		assertEquals("", toolbarButton.getText());
		assertTrue(toolbarButton.isVisible());
		assertTrue(toolbarButton.isEnabled());
	}

	@Test
	void testNewMenuItem() {
		SWTBotMenu menu = bot.menu("New");
		assertNotNull(menu);
		assertTrue(menu.isVisible());
		assertTrue(menu.isEnabled());
	}

	@Test
	void testOpenMenuItem() {
		SWTBotMenu menu = bot.menu("Open");
		assertNotNull(menu);
		assertTrue(menu.isVisible());
		assertTrue(menu.isEnabled());
	}

	@Test
	void testSaveMenuItem() {
		SWTBotMenu menu = bot.menu("Save");
		assertNotNull(menu);
		assertTrue(menu.isVisible());
		assertTrue(menu.isEnabled());
	}

	@Test
	void testSaveAsMenuItem() {
		SWTBotMenu menu = bot.menu("Save As");
		assertNotNull(menu);
		assertTrue(menu.isVisible());
		assertTrue(menu.isEnabled());
	}

	@Test
	void testShowViewMenu() {
		SWTBotShell shell = bot.shell("Footeware");
		shell.activate();
		SWTBotMenu menu = shell.bot().menu("Window").menu("Show View").menu("textify");
		assertNotNull(menu);
		assertTrue(menu.isVisible());
		assertTrue(menu.isEnabled());
	}

	@Test
	void testBackgroundPreference() {
		bot.menu("Window").menu("Preferences").click();
		SWTBotShell dialog = null;
		SWTBotShell[] shells = bot.shells();
		for (SWTBotShell shell : shells) {
			String text = shell.getText();
			if ("Preferences".equals(text)) {
				dialog = shell;
				break;
			}
		}
		if (dialog == null) {
			Assertions.fail("Preferences dialog not found.");
		}
		dialog.bot().tree().getTreeItem("Background").click();
		dialog.bot().button("Restore Defaults").click();
		SWTBotCheckBox checkBox = dialog.bot().checkBox();
		assertTrue(checkBox.isVisible());
		assertTrue(checkBox.isEnabled());
		assertFalse(checkBox.isChecked());
		dialog.bot().button("Apply and Close").click();
		SWTBotShell shell = bot.shell("Footeware");
		shell.activate();
		final SWTBotStyledText styledText = shell.bot().styledText();
		UIThreadRunnable.asyncExec(new VoidResult() {
			@Override
			public void run() {
				StyledText widget = styledText.widget;
				Image image = widget.getBackgroundImage();
				assertTrue(image == null);
			}
		});
		bot.menu("Window").menu("Preferences").click();
		dialog = null;
		shells = bot.shells();
		for (SWTBotShell botShell : shells) {
			String text = botShell.getText();
			if ("Preferences".equals(text)) {
				dialog = botShell;
				break;
			}
		}
		if (dialog == null) {
			Assertions.fail("Preferences dialog not found.");
		}
		dialog.bot().tree().getTreeItem("Background").click();
		dialog.bot().checkBox().click();
		dialog.bot().button("Apply and Close").click();
		shell = bot.shell("Footeware");
		shell.activate();
		final SWTBotStyledText styledText2 = shell.bot().styledText();
		UIThreadRunnable.asyncExec(new VoidResult() {
			@Override
			public void run() {
				StyledText widget = styledText2.widget;
				Image image = widget.getBackgroundImage();
				assertTrue(image != null);
			}
		});
	}

	@Test
	void testCurrentLinePreference() {
		bot.menu("Window").menu("Preferences").click();
		SWTBotShell dialog = null;
		SWTBotShell[] shells = bot.shells();
		for (SWTBotShell shell : shells) {
			String text = shell.getText();
			if ("Preferences".equals(text)) {
				dialog = shell;
				break;
			}
		}
		if (dialog == null) {
			Assertions.fail("Preferences dialog not found.");
		}
		dialog.bot().tree().getTreeItem("Current Line").click();
		dialog.bot().button("Restore Defaults").click();
		SWTBotCheckBox checkBox = dialog.bot().checkBox();
		assertTrue(checkBox.isVisible());
		assertTrue(checkBox.isEnabled());
		assertFalse(checkBox.isChecked());
		dialog.bot().checkBox().click();
		dialog.bot().button("Apply and Close").click();
		SWTBotShell shell = bot.shell("Footeware");
		shell.activate();
		final SWTBotStyledText styledText = shell.bot().styledText();
		styledText.setFocus();
		styledText.typeText("test");
		assertEquals(1, styledText.getLineCount());
		// FIXME why does this throw exception?
		// RGB lineBackground = styledText.getLineBackground(0);
		// assertEquals(new RGB(80, 80, 80), lineBackground);
	}
}
