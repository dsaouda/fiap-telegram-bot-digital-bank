package br.com.fiap.telegram.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;

import br.com.fiap.telegram.action.AbstractAction;
import br.com.fiap.telegram.action.CriarContaAction;

public class CriarContaCommand extends AbstractCommand {

	public CriarContaCommand() {
		super("/criarconta", "Cria uma nova conta");
	}

	@Override
	protected AbstractAction execute(TelegramBot bot, Long chatId, User user, Message message, String[] argumentos) {
		AbstractAction action = new CriarContaAction();
		return action.execute(bot, message) ? action : null;
	}
}
