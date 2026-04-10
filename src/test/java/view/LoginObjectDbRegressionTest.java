package view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.swing.JOptionPane;

import org.junit.jupiter.api.Test;

class LoginObjectDbRegressionTest {

	@Test
	void loginCorrectoAccedeAlMenuPrincipal() {
		CapturingLoginView view = new CapturingLoginView();

		view.handleLoginAttempt("123", "test");

		assertTrue(view.shopOpened);
		assertTrue(view.disposedByTest);
		assertNull(view.lastMessage);
	}

	@Test
	void loginIncorrectoMuestraMensajeDeError() {
		CapturingLoginView view = new CapturingLoginView();

		view.handleLoginAttempt("666", "nose");

		assertFalse(view.shopOpened);
		assertEquals("Usuario o password incorrectos ", view.lastMessage);
		assertEquals(JOptionPane.ERROR_MESSAGE, view.lastMessageType);
		view.dispose();
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
