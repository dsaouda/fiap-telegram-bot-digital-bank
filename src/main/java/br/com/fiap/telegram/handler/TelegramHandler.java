package br.com.fiap.telegram.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetUpdatesResponse;

import br.com.fiap.telegram.Session;
import br.com.fiap.telegram.actions.AbstractActions;
import br.com.fiap.telegram.actions.CallbackData;
import br.com.fiap.telegram.commands.AbstractCommand;
import br.com.fiap.telegram.exceptions.NaoEhUmComandoException;
import br.com.fiap.telegram.factory.TelegramFactory;

public class TelegramHandler implements Runnable {

	private TelegramBot bot;
	private GetUpdatesResponse updatesResponse;
	private int offset = 0;

	private Map<String, AbstractCommand> commands = new HashMap<>();
	private Map<String, AbstractActions> actions = new HashMap<>();

	public TelegramHandler() {
		Session.put("ultimoFluxo", Fluxo.ZERO);
		bot = TelegramFactory.create();
	}

	public TelegramHandler addCommand(AbstractCommand command) {
		commands.put(command.getNome(), command);
		return this;
	}

	public TelegramHandler addAction(AbstractActions action) {
		actions.put(action.getNome(), action);
		return this;
	}

	public void run() {		
		while(execute());
	}

	private boolean execute() {
		updatesResponse =  bot.execute(new GetUpdates().offset(offset).timeout(1));

		List<Update> updates = updatesResponse.updates();

		updates.stream().forEach(u -> {				
			nextOffset(u);

			switch(detectarFluxo(u)) {

			case COMANDO:
				commandFlow(u);
				break;

			case ACAO:
				callbackFlow(u);
				break;

			case NAO_RECONHECIDO:
			default:	

				System.out.println("OUTRO");

				break;	
			}

		});

		return true;
	}

	private void commandFlow(Update u) {	
		Long chatId = u.message().chat().id();				
		Message message = u.message();

		String texto = message.text();

		try {
			AbstractCommand comando = getComando(texto);
			comando.executar(bot, message);


		} catch (NaoEhUmComandoException | NullPointerException e) {
			bot.execute(new SendChatAction(chatId, ChatAction.typing.name()));
			bot.execute(new SendMessage(chatId, "N�o reconheci o seu comando, tente novamente por favor"));
		}

	}

	private void callbackFlow(Update u) {
		CallbackQuery callbackQuery = u.callbackQuery();
		
		if (callbackQuery != null) {
			Session.put("callbackQuery", callbackQuery);
		} else {
			callbackQuery = Session.get("callbackQuery", CallbackQuery.class);
		}
		
		CallbackData callbackData = CallbackData.fromJson(callbackQuery.data());
		
		Message messageInput = u.message();
		
		AbstractActions action = actions.get(callbackData.getAction());
		if (messageInput == null) {
			action.executarButton(bot, callbackQuery.message(), callbackData);
		} else {
			action.executarInput(bot, callbackQuery.message(), messageInput, callbackData);
		}
	}

	private Fluxo detectarFluxo(Update u) {
		
		if (isCallback(u)) {
			Session.put("ultimoFluxo", Fluxo.ACAO);
			return Fluxo.ACAO;
		}

		if (isComando(u)) {
			Session.put("ultimoFluxo", Fluxo.COMANDO);
			return Fluxo.COMANDO;
		}
		
		//isso permite continuar um fluxo iniciado
		if (!Session.get("ultimoFluxo", Fluxo.class).equals(Fluxo.ZERO)) {
			return Session.get("ultimoFluxo", Fluxo.class);
		}

		Session.put("ultimoFluxo", Fluxo.NAO_RECONHECIDO);
		return Fluxo.NAO_RECONHECIDO;
	}

	private boolean isCallback(Update u) {
		CallbackQuery callback = u.callbackQuery();
		return callback != null;
	}

	private boolean isComando(Update u) {
		final Message message = u.message();		
		return message == null ? false : AbstractCommand.isCommand(message.text());
	}

	private void nextOffset(Update u) {
		offset = u.updateId() + 1;
	}

	private AbstractCommand getComando(String texto) throws NaoEhUmComandoException {
		String nomeComando = AbstractCommand.extrairNomeComando(texto);
		return commands.get(nomeComando);
	}
}
