package view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.swing.JOptionPane;

import org.junit.jupiter.api.Test;

import main.Shop;
import support.MongoIntegrationSupport;

class ViewBehaviorTest extends MongoIntegrationSupport {

	@Test
	void exportInventoryShowsInformationMessageWhenMongoWriteSucceeds() {
		CapturingShopView view = new CapturingShopView(new Shop());

		view.exportInventory();

		assertEquals("Inventario exportado correctamente", view.lastMessage);
		assertEquals(JOptionPane.INFORMATION_MESSAGE, view.lastMessageType);
		view.dispose();
	}

	@Test
	void exportInventoryShowsErrorMessageWhenMongoWriteFails() {
		Shop failingShop = new Shop() {
			@Override
			public void initializeInventory() {
			}

			@Override
			public Boolean exportInventoryToDatabase() {
				return false;
			}
		};

		CapturingShopView view = new CapturingShopView(failingShop);

		view.exportInventory();

		assertEquals("Error exportando el inventario", view.lastMessage);
		assertEquals(JOptionPane.ERROR_MESSAGE, view.lastMessageType);
		view.dispose();
	}

	@Test
	void correctLoginOpensShopMenu() {
		CapturingLoginView view = new CapturingLoginView();

		view.handleLoginAttempt("1", "1234");

		assertTrue(view.shopOpened);
		assertTrue(view.disposedByTest);
		assertNull(view.lastMessage);
	}

	@Test
	void incorrectLoginShowsErrorMessage() {
		CapturingLoginView view = new CapturingLoginView();

		view.handleLoginAttempt("1", "incorrecta");

		assertFalse(view.shopOpened);
		assertEquals("Usuario o password incorrectos ", view.lastMessage);
		assertEquals(JOptionPane.ERROR_MESSAGE, view.lastMessageType);
		view.dispose();
	}

	private static final class CapturingShopView extends ShopView {
		private String lastMessage;
		private int lastMessageType;

		private CapturingShopView(Shop shop) {
			super(shop);
		}

		@Override
		protected void showMessage(String message, String title, int messageType) {
			this.lastMessage = message;
			this.lastMessageType = messageType;
		}
	}

	private static final class CapturingLoginView extends LoginView {
		private String lastMessage;
		private int lastMessageType;
		private boolean shopOpened;
		private boolean disposedByTest;

		@Override
		protected void showMessage(String message, String title, int messageType) {
			this.lastMessage = message;
			this.lastMessageType = messageType;
		}

		@Override
		protected void openShopView() {
			this.shopOpened = true;
		}

		@Override
		public void dispose() {
			this.disposedByTest = true;
			super.dispose();
		}
	}
}
