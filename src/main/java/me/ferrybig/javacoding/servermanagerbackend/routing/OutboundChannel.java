/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.routing;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import me.ferrybig.javacoding.servermanagerbackend.api.request.KillStreamRequest;
import me.ferrybig.javacoding.servermanagerbackend.api.request.Request;
import me.ferrybig.javacoding.servermanagerbackend.api.response.DelayedDataResponse;
import me.ferrybig.javacoding.servermanagerbackend.api.response.DelayedResponse;
import me.ferrybig.javacoding.servermanagerbackend.api.response.Response;
import me.ferrybig.javacoding.servermanagerbackend.api.response.StreamingDataResponse;
import me.ferrybig.javacoding.servermanagerbackend.api.response.StreamingResponse;

public class OutboundChannel {

	private final Function<? super Response, ? extends Future<?>> packetSender;
	private final IntSupplier idSupplier;
	private final Map<Integer, OutgoingInteraction> interactions = new ConcurrentHashMap<>();
	private final Supplier<? extends Promise<?>> newPromise;

	public OutboundChannel(
		Function<? super Response, ? extends Future<?>> packetSender,
		IntSupplier idSupplier,
		Supplier<? extends Promise<?>> newPromise
	) {
		this.packetSender = packetSender;
		this.idSupplier = idSupplier;
		this.newPromise = newPromise;
	}

	public Future<?> writeResponse(Response rep) {
		return packetSender.apply(rep);
	}

	private OutgoingInteraction startInteraction(IntFunction<Response> constructor) {
		// This method relies on this order of operations
		int id = this.idSupplier.getAsInt();
		OutgoingInteraction interaction = new OutgoingInteraction(newPromise.get(), id);
		interactions.put(id, interaction);
		interaction.addDoneListener(f -> {
			interactions.remove(id, interaction);
		});
		writeResponse(constructor.apply(id));
		return interaction;
	}

	public OutgoingInteraction startStream(Request req) {
		return this.startInteraction(id -> new StreamingResponse(true, req, id));
	}

	public OutgoingInteraction startDelayed(Request req) {
		return this.startInteraction(id -> new DelayedResponse(true, req, id));
	}

	public boolean cancelStream(KillStreamRequest req) {
		OutgoingInteraction interaction = this.interactions.get(req.id);
		if (interaction == null) {
			return false;
		}
		interaction.done.cancel(true);
		return interaction.done.isCancelled();
	}

	public class OutgoingInteraction {

		private final Promise<?> done;
		private final int id;

		public OutgoingInteraction(Promise<?> done, int id) {
			this.done = done;
			this.id = id;
		}

		public Future<?> checkStatus() {
			return done;
		}

		public void addDoneListener(GenericFutureListener<? extends Future<? super Object>> listener) {
			this.done.addListener(listener);
		}

		private void sendResponse(Response res) {
			packetSender.apply(res).addListener(f -> {
				if (!f.isSuccess()) {
					this.done.tryFailure(f.cause());
				}
			});
		}

		public boolean onStream(boolean success, Object data, boolean moreData) {
			if (done.isDone()) {
				return false;
			}
			sendResponse(new StreamingDataResponse(success, id, data, moreData));
			return moreData;
		}

		public boolean onStream(boolean success, Object data) {
			if (done.isDone()) {
				return false;
			}
			sendResponse(new StreamingDataResponse(success, id, data));
			return true;
		}

		public boolean onData(boolean success, Object data) {
			if (done.isDone()) {
				return false;
			}
			sendResponse(new DelayedDataResponse(success, id, data));
			return false;
		}
	}
}
