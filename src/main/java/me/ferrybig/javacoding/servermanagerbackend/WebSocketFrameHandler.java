/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.ferrybig.javacoding.servermanagerbackend;

import io.netty.buffer.ByteBuf;
import java.util.Locale;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import java.text.MessageFormat;
import java.util.logging.Logger;

/**
 * Echoes uppercase content of text frames.
 */
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

	private static final Logger LOG = Logger.getLogger(WebSocketFrameHandler.class.getName());
	private ChannelHandlerContext ctx;
	private final LogRecorder logRecorder;

	WebSocketFrameHandler(LogRecorder logRecorder) {
		this.logRecorder = logRecorder;

	}

	private void sendBytes(byte[] data, int start, int end) {
		ByteBuf buf = ctx.alloc().buffer(end - start);
		buf.writeBytes(data, start, end);
		this.ctx.writeAndFlush(new TextWebSocketFrame(buf));
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		this.ctx = ctx;
	}

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        // ping and pong frames already handled

        if (frame instanceof TextWebSocketFrame) {
            // Send the uppercase string back.
            String request = ((TextWebSocketFrame) frame).text();
            LOG.info(MessageFormat.format("{0} received {1}", ctx.channel(), request));

			if(request.equals("register")) {
				logRecorder.addByteListener(this::sendBytes, true);
			}
        } else {
            String message = "unsupported frame type: " + frame.getClass().getName();
            throw new UnsupportedOperationException(message);
        }
    }
}
