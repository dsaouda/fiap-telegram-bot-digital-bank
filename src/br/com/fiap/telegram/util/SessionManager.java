package br.com.fiap.telegram.util;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.internal.Primitives;

/**
 * Essa classe tem o objetivo de simular um session de HTTP, ou seja, armazenar estados entre cliente(usuário do telegram)/servidor(bot api).
 * A session armazena objetos do tipo genérico.
 * Esse sesion manager é gravado em um arquivo que deve ser configurado no arquivo properties do sistema 
 * @author Diego.Saouda
 *
 */
final public class SessionManager implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * HashMap contendo os valores que serão gravados em disco
	 */
	private Map<Object, Object> storage = new ConcurrentHashMap<>();
	
	/**
	 * local aonde o arquivo será gravado
	 */
	private static Path sessionPath = Paths.get(Config.get("session.path"));
	
	/**
	 * id da sessão (um identificador único que será usado como nome para geração do arquivo de session)
	 */
	private Integer sessionId;
	
	/**
	 * Armazena uma instância desse sessionManager por usuário
	 */
	private transient static Map<Integer, SessionManager> instances = new ConcurrentHashMap<>();
	
	private SessionManager(Integer sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * Retorna o session manager do usuário para que o mesmo recupere os valores gravados
	 * @param sessionId identificador da sessão
	 * @return instância única de session manager
	 */
	public static SessionManager getInstance(Integer sessionId) {
		if (!instances.containsKey(sessionId)) {
			sessionPath.toFile().mkdirs();
			File file = fileSession(sessionId);

			if (file.exists()) {
				return new Serialize<SessionManager>(file).unserialize();
				
			}
			
			instances.put(sessionId, new SessionManager(sessionId));	
		}
		
		return instances.get(sessionId);
	}

	private static File fileSession(Integer id) {
		File file = sessionPath.resolve("user_" + id).toFile();
		return file;
	}
	
	private void save() {
		File file = fileSession(sessionId);
		new Serialize<SessionManager>(file).serialize(this);
	}
	
	/**
	 * Objeto que será gravado
	 * @param key chave para armazenar o objeto
	 * @param o objeto
	 * @return permitir interface fluente
	 */
	public SessionManager put(Object key, Object o) {
		storage.put(key, o);
		this.save();
		return this;
	}
	
	/**
	 * Retornando um objeto de uma chave
	 * @param <T> tipo genérico 
	 * @param key chave que o objeto está representado
	 * @param classOfT cast de objet para o objeto especialista
	 * @return retorna um objeto genérico, para não precisar fazer cast
	 */
	public <T> T get(Object key, Class<T> classOfT) {
		Object object = get(key);
		return Primitives.wrap(classOfT).cast(object);
	}
	
	/**
	 * Retornando um objeto de uma chave sem utilizar casting 
	 * @param key chave que guarda um objeto
	 * @return Retorna um objeto na sessão
	 */
	public Object get(Object key) {
		return storage.get(key);
	}
	
	/**
	 * Remover um objeto do session storage
	 * @param key Objeto a ser removido
	 * @return permitir interface fluente
	 */
	public SessionManager remove(Object key) {
		storage.remove(key);
		this.save();
		return this;
	}
	
	/**
	 * Verifica se uma chave existe no storage
	 * @param key O objeto a ser verificado
	 * @return true se existir a chave
	 */
	public boolean containsKey(Object key) {
		return storage.containsKey(key);
	}

	@Override
	public String toString() {
		return storage.toString();
	}
	
}
