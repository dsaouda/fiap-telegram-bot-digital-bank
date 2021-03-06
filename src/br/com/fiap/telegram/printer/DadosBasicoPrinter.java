package br.com.fiap.telegram.printer;

import br.com.fiap.telegram.model.Conta;
import br.com.fiap.telegram.util.Helpers;

/**
 * Imprimir dados básicos de uma conta
 * @author Diego.Saouda
 *
 */
public class DadosBasicoPrinter implements ContaPrinter {

	@Override
	public String imprimir(Conta conta) {		
		return "\nTitular: " + conta.getTitular().getNome() +
				"\nNumero: " + conta.getNumero() +
				"\nAbertura: " + Helpers.formatarDataHora(conta.getAbertura()) +
				"\nSaldo: " + conta.getSaldo();		
	}

}
