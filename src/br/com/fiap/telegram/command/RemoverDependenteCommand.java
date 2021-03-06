package br.com.fiap.telegram.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;

import br.com.fiap.telegram.action.AbstractAction;
import br.com.fiap.telegram.action.RemoverDependenteAction;

/**
 * Remover um dependente da conta
 * @author Diego.Saouda
 *
 */
public class RemoverDependenteCommand extends AbstractCommand {

	public RemoverDependenteCommand() {
		super("/removerdependente", "Remover um dependente a conta sua conta");
	}

	@Override
	protected AbstractAction execute(TelegramBot bot, Long chatId, User user, Message message, String[] argumentos) {
		RemoverDependenteAction action = new RemoverDependenteAction();
		action.execute(bot, message);
		
		return action;
	}

}
