package view;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import exception.LimitLoginException;
import model.Employee;
import utils.Constants;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import util.LookAndFeelUtil;

public class LoginView extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textFieldEmployeeId;
	private JTextField textFieldPassword;
	private JButton btnLogin;
	private int counterErrorLogin;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		LookAndFeelUtil.applySystemLookAndFeel();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginView frame = new LoginView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LoginView() {
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblEmployeeId = new JLabel("Número de empleado");
		lblEmployeeId.setBounds(55, 50, 130, 14);
		contentPane.add(lblEmployeeId);

		textFieldEmployeeId = new JTextField();
		textFieldEmployeeId.setBounds(65, 75, 176, 20);
		contentPane.add(textFieldEmployeeId);
		textFieldEmployeeId.setColumns(10);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(55, 115, 130, 14);
		contentPane.add(lblPassword);

		textFieldPassword = new JTextField();
		textFieldPassword.setBounds(65, 140, 176, 20);
		contentPane.add(textFieldPassword);
		textFieldPassword.setColumns(10);

		btnLogin = new JButton("Acceder");
		btnLogin.setBounds(308, 208, 89, 23);
		contentPane.add(btnLogin);
		// listen button 
		btnLogin.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnLogin) {
			handleLoginAttempt(textFieldEmployeeId.getText(), textFieldPassword.getText());
		}
	}

	protected void handleLoginAttempt(String employeeId, String password) {
		if (employeeId.isBlank() || password.isBlank()) {
			showMessage("Usuario y contraseña son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			boolean logged = createEmployee().login(Integer.parseInt(employeeId), password);
			if (logged) {
				openShopView();
				dispose();
				return;
			}

			counterErrorLogin++;
			if (counterErrorLogin >= Constants.MAX_LOGIN_TIMES) {
				throw new LimitLoginException("Error login superado", counterErrorLogin);
			}

			showMessage("Usuario o password incorrectos ", "Error", JOptionPane.ERROR_MESSAGE);
			clearLoginForm();
		} catch (NumberFormatException ex) {
			showMessage("El número de empleado debe ser numérico", "Error", JOptionPane.ERROR_MESSAGE);
			clearLoginForm();
		} catch (LimitLoginException ex) {
			showMessage("Error login, superados los " + counterErrorLogin + " intentos", "Error",
					JOptionPane.ERROR_MESSAGE);
			dispose();
		}
	}

	protected Employee createEmployee() {
		return new Employee();
	}

	protected void openShopView() {
		ShopView shop = createShopView();
		shop.setExtendedState(NORMAL);
		shop.setVisible(true);
	}

	protected ShopView createShopView() {
		return new ShopView();
	}

	protected void showMessage(String message, String title, int messageType) {
		JOptionPane.showMessageDialog(this, message, title, messageType);
	}

	protected void clearLoginForm() {
		textFieldEmployeeId.setText("");
		textFieldPassword.setText("");
	}
}
