package br.com.fiap.telegram.model;

import java.math.BigDecimal;
/**
 * Taxas de serviço de uma conta são informadas nesse enum
 * @author Diego.Saouda
 *
 */
public enum Taxas {

	SAQUE(new BigDecimal("-2.50")), EXTRATO(new BigDecimal("-1.00")), EMPRESTIMO(new BigDecimal("-15.00"));
	
	private BigDecimal valor;

	Taxas(BigDecimal valor) {
		this.valor = valor;		
	}
	
	public BigDecimal getValor() {
		return valor;
	}
}
