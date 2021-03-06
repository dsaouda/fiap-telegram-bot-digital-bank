package br.com.fiap.telegram.exception;

/**
 * Exception em caso de saldo insuficiente
 * @author Diego.Saouda
 *
 */
public class SaldoInsuficienteException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public SaldoInsuficienteException() {
		super("Saldo de sua conta é insuficiente para operação.");
	}

	public SaldoInsuficienteException(String message) {
		super(message);
	}
}
