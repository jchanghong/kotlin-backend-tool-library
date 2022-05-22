package com.github.jchanghong.http.server.handler;

import com.github.jchanghong.http.server.HttpServerRequest;
import com.github.jchanghong.http.server.HttpServerResponse;
import com.github.jchanghong.http.server.action.Action;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 * Action处理器，用于将HttpHandler转换为Action形式
 *
 * @author looly
 * @since 5.2.6
 */
public class ActionHandler implements HttpHandler {

	private final Action action;

	/**
	 * 构造
	 *
	 * @param action Action
	 */
	public ActionHandler(Action action) {
		this.action = action;
	}

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		action.doAction(
				new HttpServerRequest(httpExchange),
				new HttpServerResponse(httpExchange)
		);
		httpExchange.close();
	}
}
